import {Injectable} from '@angular/core';
import {catchError, filter, map, switchMap, tap} from 'rxjs/operators';
import migrationV1 from '../../migrations/migration-v1';
import {environment} from '../../environments/environment';
import {SqliteStorageService} from './sqlite-storage.service';
import {createTableQuery} from '../classes/sql-helpers';
import {EMPTY, forkJoin, of, throwError} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MigrationsService {

  private readonly TABLE_NAME = 'migrations';
  private readonly TABLE_COLUMNS = ['id integer primary key autoincrement', 'version integer', 'date text'];

  private migrations = [
    migrationV1
  ];

  constructor(private sqlite: SqliteStorageService) {}

  initMigrationTable() {
    return this.sqlite.executeSQLWithLog(
      'CREATE MIGRATIONS TABLE',
      createTableQuery(this.TABLE_NAME, this.TABLE_COLUMNS)
    ).pipe(
      catchError(_ => EMPTY)
    );
  }

  getMigrationVersion() {
    // to save the value and use in multiple pipe operations without needing to include it each time
    let migrationVersion;
    return this.sqlite.select(['version'], 'order by version desc limit 1', [], this.TABLE_NAME).pipe(
      // 2 options here which depend on whether it's the first time we open the app or not
      switchMap(res => {
        if (res && res.length > 0) {
          migrationVersion = res[0].version;
          return EMPTY;
        }
        return throwError(null);
      }),
      // in case it the first time we are opening the app
      catchError(_ => {
        migrationVersion = environment.migration_version;
        return this.sqlite.insert(['version', 'date'], [
            environment.migration_version,
            new Date().toISOString().split('T')[0]
          ],
          this.TABLE_NAME);
      }),
      map(_ => migrationVersion)
    );
  }

  migrate() {
    let migrationData: any[];
    return this.getMigrationVersion().pipe(
      // don't continue if we are on the latest migration
      filter(lastMigration => lastMigration < environment.migration_version),
      // return the last migration value + the data
      switchMap(lastMigration => forkJoin([of(lastMigration), this.sqlite.select()])),
      // transform the data
      switchMap(([lastMigration, data]) => {
        console.log(lastMigration, data);
        migrationData = data;
        let migrationVersion = lastMigration;
        // loop until we read the latest migration data
        while (migrationVersion < environment.migration_version) {
          migrationData = this.migrations[migrationVersion - 1](migrationData);
          migrationVersion++;
        }
        return EMPTY;
      }),
      // drop the old table of old migrations
      switchMap(_ => this.sqlite.dropDataTable()),
      // create the new migration table
      switchMap(_ => this.sqlite.initDataTable()),
      // save the new data in the new table
      tap(_ => {
        this.sqlite.insertBulk(migrationData);
      })
    );
  }
}

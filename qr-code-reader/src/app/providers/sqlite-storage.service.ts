import {Injectable} from '@angular/core';
import {EMPTY, of} from 'rxjs';
import {catchError, map, tap} from 'rxjs/operators';
import {SQLite, SQLiteObject} from '@awesome-cordova-plugins/sqlite/ngx';
import {executeSQLWithLog, toObservable} from '../classes/rxjs-helpers';
import {createTableQuery, deleteQuery, dropTableQuery, insertQuery, selectQuery} from '../classes/sql-helpers';

@Injectable({
  providedIn: 'root'
})
export class SqliteStorageService {

  db: SQLiteObject;
  private readonly TABLE_NAME = 'scanning_results';
  private readonly TABLE_COLUMNS = ['id integer primary key autoincrement', 'data text', 'date text'];

  constructor(private sqlite: SQLite) { }

  executeSQLWithLog(type, query, data = []) {
    return executeSQLWithLog(this.db, type, query, data);
  }

  initDB() {
    return toObservable(
      this.sqlite.create({
        name: 'data.db',
        location: 'default'
      })
    ).pipe(
      tap((db: SQLiteObject) => this.db = db)
    );
  }

  initDataTable() {
    return this.executeSQLWithLog('CREATE MAIN TABLE', createTableQuery(this.TABLE_NAME, this.TABLE_COLUMNS)).pipe(
      catchError(err => EMPTY)
    );
  }

  dropDataTable() {
    return this.executeSQLWithLog('DROP MAIN TABLE', dropTableQuery(this.TABLE_NAME));
  }

  insert(columns: string[], data: any[], table: string = this.TABLE_NAME) {
    return this.executeSQLWithLog('INSERT', insertQuery(table, columns), data);
  }

  delete(columns: string[], data: any[], table: string = this.TABLE_NAME) {
    return this.executeSQLWithLog('DELETE', deleteQuery(table, columns), data);
  }

  select(columns: string[] = ['*'], extra: string = '', data: any[] = [], table: string = this.TABLE_NAME) {
    return this.executeSQLWithLog('SELECT', selectQuery(table, columns, extra), data).pipe(
      map((resultSet: any) => {
        const data = [];
        for (let i = 0; i < resultSet.rows.length; i++) {
          data.push(resultSet.rows.item(i));
        }
        return data;
      })
    );
  }

  insertBulk(data: any[], columns: string[] = this.TABLE_COLUMNS, table: string = this.TABLE_NAME) {
    return toObservable(this.db.sqlBatch(data.map(d => this.db.executeSql(insertQuery(this.TABLE_NAME, columns), d))));
  }

}

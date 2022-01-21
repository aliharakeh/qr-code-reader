import {Component, OnDestroy, OnInit} from '@angular/core';
import {AlertController, Platform} from '@ionic/angular';
import {SqliteStorageService} from './providers/sqlite-storage.service';
import {finalize, switchMap, tap} from 'rxjs/operators';
import {MigrationsService} from './providers/migrations.service';

@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: ['app.component.scss']
})
export class AppComponent implements OnInit, OnDestroy {

  backButtonSub;

  constructor(
    private platform: Platform,
    private migration: MigrationsService,
    private sqlite: SqliteStorageService
  ) {
  }

  ngOnInit() {
    this.observeBackButtonEvent();
    this.migration.migrate().pipe(
      // we need this in case the filter inside migrate stopped the next operators. it will only affect the app on the
      // first run.
      finalize(() => this.sqlite.initDataTable())
    ).subscribe();
  }

  ngOnDestroy() {
    this.backButtonSub.unsubscribe();
  }

  observeBackButtonEvent() {
    if (this.platform.is('android') || this.platform.is('ios')) {
      this.backButtonSub = this.platform.backButton.subscribeWithPriority(0, () => {
        // @ts-ignore
        navigator.app.exitApp();
      });
    }
  }
}

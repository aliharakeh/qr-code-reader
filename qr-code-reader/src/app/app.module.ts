import {HttpClient, HttpClientModule} from '@angular/common/http';
import {APP_INITIALIZER, NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {RouteReuseStrategy} from '@angular/router';
import {IonicModule, IonicRouteStrategy} from '@ionic/angular';
import {AppComponent} from './app.component';
import {AppRoutingModule} from './app-routing.module';
import {TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {TranslateHttpLoader} from '@ngx-translate/http-loader';
import {File} from '@awesome-cordova-plugins/file/ngx';
import {FileTransfer} from '@awesome-cordova-plugins/file-transfer/ngx';
import {MediaCapture} from '@awesome-cordova-plugins/media-capture/ngx';
import {AndroidPermissions} from '@awesome-cordova-plugins/android-permissions/ngx';
import {SQLite} from '@awesome-cordova-plugins/sqlite/ngx';
import {SqliteStorageService} from './providers/sqlite-storage.service';
import {switchMap} from 'rxjs/operators';
import {MigrationsService} from './providers/migrations.service';

// eslint-disable-next-line
function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http);
}

function initializeApp(sqlite: SqliteStorageService, migration: MigrationsService) {
  return (): Promise<any> => {
    return sqlite.initDB().pipe(
      switchMap(_ => migration.initMigrationTable())
    ).toPromise();
  };
}

@NgModule({
  declarations: [
    AppComponent
  ],
  entryComponents: [],
  imports: [
    BrowserModule,
    HttpClientModule,
    IonicModule.forRoot(),
    TranslateModule.forRoot({
      defaultLanguage: 'fr',
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient]
      }
    }),
    AppRoutingModule
  ],
  providers: [
    File,
    FileTransfer,
    MediaCapture,
    AndroidPermissions,
    SQLite,
    {
      provide: APP_INITIALIZER,
      useFactory: initializeApp,
      deps: [SqliteStorageService, MigrationsService],
      multi: true
    },
    { provide: RouteReuseStrategy, useClass: IonicRouteStrategy }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}

import {Component, OnInit} from '@angular/core';
import {from} from 'rxjs';
import {switchMap, take, tap} from 'rxjs/operators';
import {ActionSheetController, ModalController} from '@ionic/angular';
import {TranslateService} from '@ngx-translate/core';
import {CameraService} from '../../providers/camera.service';
import {SqliteStorageService} from '../../providers/sqlite-storage.service';
import {DataDateFilterComponent} from '../../components/data-date-filter/data-date-filter.component';
import {Router} from '@angular/router';

@Component({
  selector: 'app-home',
  templateUrl: './home.page.html',
  styleUrls: ['./home.page.scss']
})
export class HomePage implements OnInit {

  qrData = [];
  showQRResult = false;

  constructor(
    private translate: TranslateService,
    private actionSheetCtrl: ActionSheetController,
    private camera: CameraService,
    private sqlite: SqliteStorageService,
    private modalCtrl: ModalController,
    private router: Router
  ) { }

  ngOnInit() {}

  ionViewDidEnter() {
      this.loadData();
  }

  loadData() {
    this.sqlite.select(['*'], 'limit 10').pipe(
      tap((data: any) => this.qrData = data)
    ).subscribe();
  }

  reset() {
    this.qrData = null;
    this.showQRResult = false;
  }

  showScanOptions() {
    from(this.actionSheetCtrl.create({
      header: this.translate.instant('SCAN_OPTIONS'),
      buttons: [
        {
          text: this.translate.instant('SCAN_PHOTO'),
          icon: 'camera-outline',
          handler: () => {
            this.camera.getPhoto().pipe(
              switchMap(res => {
                const data = [
                  res || this.translate.instant('SCAN_FAILED'),
                  new Date().toISOString().split('T')[0]
                ]
                return this.sqlite.insert(['data', 'date'], data);
              }),
              tap(_ => this.loadData())
            ).subscribe();
          }
        }, {
          text: this.translate.instant('SCAN_SCREEN'),
          icon: 'laptop-outline',
          handler: () => {
            this.camera.captureVideo().pipe(
              switchMap(res => {
                const data = [
                  res || this.translate.instant('SCAN_FAILED'),
                  new Date().toISOString().split('T')[0]
                ]
                return this.sqlite.insert(['data', 'date'], data);
              }),
              tap(_ => this.loadData())
            ).subscribe();
          }
        }
      ]
    })).pipe(
      take(1),
      tap(sheet => sheet.present())
    ).subscribe();
  }

  chooseDate() {
    from(
      this.modalCtrl.create({
        component: DataDateFilterComponent,
        breakpoints: [0, 0.5],
        initialBreakpoint: 0.5
      })
    ).pipe(
      switchMap(m => {
        m.present();
        return from(m.onDidDismiss());
      }),
      tap(data => {
        this.router.navigateByUrl('date-data-list', { state: data.data });
      })
    ).subscribe();
  }

  openSettings() {
    this.router.navigateByUrl('settings');
  }
}

import {Component, Input, OnInit} from '@angular/core';
import {AlertController, PopoverController} from '@ionic/angular';
import {SqliteStorageService} from '../../providers/sqlite-storage.service';
import {filter, switchMap, tap} from 'rxjs/operators';
import {Router} from '@angular/router';
import {from} from 'rxjs';
import {TranslateService} from '@ngx-translate/core';

@Component({
  selector: 'app-item-options',
  templateUrl: './item-options.component.html',
  styleUrls: ['./item-options.component.scss']
})
export class ItemOptionsComponent implements OnInit {

  @Input('data') data: any;

  constructor(
    private popoverCtrl: PopoverController,
    private alertCtrl: AlertController,
    private sqlite: SqliteStorageService,
    private router: Router,
    private translate: TranslateService
  ) { }

  ngOnInit() {}

  view() {
    from(
      this.router.navigateByUrl('data-view', {
        state: this.data
      })
    ).pipe(
      tap(_ => this.close(null))
    ).subscribe();
  }

  delete() {
    from(
      this.alertCtrl.create({
        message: this.translate.instant('CONFIRM_DELETE'),
        buttons: [
          {
            text: this.translate.instant('BUTTONS.OK'),
            role: 'ok'
          },
          {
            text: this.translate.instant('BUTTONS.CANCEL'),
            role: 'cancel'
          }
        ]
      })
    ).pipe(
      switchMap(a => {
        a.present();
        return from(a.onDidDismiss());
      }),
      filter(data => {
        if (data.role === 'ok') {
          return true;
        }
        this.close(null);
        return false;
      }),
      switchMap(_ => this.sqlite.delete(['id'], [this.data.id])),
      tap(_ => {
        this.close(this.data.id);
      }),
    ).subscribe();
  }

  close(data) {
    this.popoverCtrl.dismiss(data);
  }
}

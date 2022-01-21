import {Component, Input, OnInit} from '@angular/core';
import {from, pipe} from 'rxjs';
import {ItemOptionsComponent} from '../item-options/item-options.component';
import {switchMap, tap} from 'rxjs/operators';
import {PopoverController} from '@ionic/angular';

@Component({
  selector: 'app-qr-data-list',
  templateUrl: './qr-data-list.component.html',
  styleUrls: ['./qr-data-list.component.scss'],
})
export class QrDataListComponent implements OnInit {

  @Input() qrData = [];
  @Input() title = '';

  constructor(
    private popoverCtrl: PopoverController
  ) { }

  ngOnInit() {}

  showPopup(ev, data) {
    from(
      this.popoverCtrl.create({
        component: ItemOptionsComponent,
        event: ev,
        componentProps: { data: data }
      })
    ).pipe(
      switchMap(p => {
        p.present();
        return from(p.onDidDismiss());
      }),
      tap(data => {
        if (data.data) {
          this.qrData = this.qrData.filter(d => d.id !== data.data);
        }
      })
    ).subscribe();
  }
}

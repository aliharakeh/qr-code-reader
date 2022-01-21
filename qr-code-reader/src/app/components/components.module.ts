import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {ItemOptionsComponent} from './item-options/item-options.component';
import {QrDataListComponent} from './qr-data-list/qr-data-list.component';
import {IonicModule} from '@ionic/angular';
import {TranslateModule} from '@ngx-translate/core';
import {DataDateFilterComponent} from './data-date-filter/data-date-filter.component';



@NgModule({
  declarations: [
    ItemOptionsComponent,
    QrDataListComponent,
    DataDateFilterComponent
  ],
  imports: [
    CommonModule,
    IonicModule,
    TranslateModule
  ],
  exports: [
    ItemOptionsComponent,
    QrDataListComponent
  ]
})
export class ComponentsModule { }

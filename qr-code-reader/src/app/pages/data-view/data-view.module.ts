import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { DataViewPageRoutingModule } from './data-view-routing.module';

import { DataViewPage } from './data-view.page';
import {TranslateModule} from '@ngx-translate/core';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    DataViewPageRoutingModule,
    TranslateModule
  ],
  declarations: [DataViewPage]
})
export class DataViewPageModule {}

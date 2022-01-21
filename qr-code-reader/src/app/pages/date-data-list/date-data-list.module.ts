import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { DataListPageRoutingModule } from './date-data-list-routing.module';

import { DateDataListPage } from './date-data-list.page';
import {ComponentsModule} from '../../components/components.module';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        IonicModule,
        DataListPageRoutingModule,
        ComponentsModule
    ],
  declarations: [DateDataListPage]
})
export class DataListPageModule {}

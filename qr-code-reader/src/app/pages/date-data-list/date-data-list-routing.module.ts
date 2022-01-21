import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { DateDataListPage } from './date-data-list.page';

const routes: Routes = [
  {
    path: '',
    component: DateDataListPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class DataListPageRoutingModule {}

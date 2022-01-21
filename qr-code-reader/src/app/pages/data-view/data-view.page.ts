import { Component, OnInit } from '@angular/core';
import {Router} from '@angular/router';

@Component({
  selector: 'app-data-view',
  templateUrl: './data-view.page.html',
  styleUrls: ['./data-view.page.scss'],
})
export class DataViewPage implements OnInit {

  data: any;

  constructor(
    private router: Router
  ) {
    this.data = this.router.getCurrentNavigation().extras.state as any;
  }

  ngOnInit() {
  }

}

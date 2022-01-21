import { Component, OnInit } from '@angular/core';
import {ModalController} from '@ionic/angular';

@Component({
  selector: 'app-data-date-filter',
  templateUrl: './data-date-filter.component.html',
  styleUrls: ['./data-date-filter.component.scss'],
})
export class DataDateFilterComponent implements OnInit {

  constructor(private modalCtrl: ModalController) { }

  ngOnInit() {}

  onChange(event: any) {
    const date = event.detail.value.split('T')[0];
    this.modalCtrl.dismiss(date);
  }
}

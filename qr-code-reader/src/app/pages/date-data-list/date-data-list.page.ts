import { Component, OnInit } from '@angular/core';
import {Router} from '@angular/router';
import {SqliteStorageService} from '../../providers/sqlite-storage.service';
import {tap} from 'rxjs/operators';

@Component({
  selector: 'app-date-data-list',
  templateUrl: './date-data-list.page.html',
  styleUrls: ['./date-data-list.page.scss'],
})
export class DateDataListPage implements OnInit {

  qrData = [];
  date: string;

  constructor(
    private router: Router,
    private sqlite: SqliteStorageService
  ) {
    this.date = this.router.getCurrentNavigation().extras.state as any;
  }

  ngOnInit() {
    this.sqlite.select(['*'], 'where date = ?', [this.date]).pipe(
      tap(data => this.qrData = data)
    ).subscribe();
  }

}

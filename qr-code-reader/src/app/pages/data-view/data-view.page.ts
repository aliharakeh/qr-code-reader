import {Component, OnInit, SecurityContext} from '@angular/core';
import {Router} from '@angular/router';
import {DomSanitizer} from '@angular/platform-browser';

@Component({
  selector: 'app-data-view',
  templateUrl: './data-view.page.html',
  styleUrls: ['./data-view.page.scss'],
})
export class DataViewPage implements OnInit {

  data: any;

  constructor(
    private router: Router,
    private sanitizer: DomSanitizer
  ) {
    this.data = this.router.getCurrentNavigation().extras.state as any;
  }

  ngOnInit() {
  }

  get sanitizedData() {
    return this.data ? this.sanitizer.sanitize(SecurityContext.HTML, this.data.data) : "";
  }

}

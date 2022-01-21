import {Component, OnInit} from '@angular/core';
import {SettingsService} from '../../providers/settings.service';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.page.html',
  styleUrls: ['./settings.page.scss']
})
export class SettingsPage implements OnInit {

  constructor(public settings: SettingsService) { }

  ngOnInit() {
  }

  setLang() {
    this.settings.changeLang(this.settings.lang);
  }
}

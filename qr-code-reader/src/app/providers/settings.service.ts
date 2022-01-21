import {Injectable} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';

@Injectable({
  providedIn: 'root'
})
export class SettingsService {

  public appUrl = 'http://10.0.2.2:8080';
  public lang = 'fr';

  constructor(private translate: TranslateService) { }

  changeLang(lang) {
    this.translate.use(lang);
  }

}

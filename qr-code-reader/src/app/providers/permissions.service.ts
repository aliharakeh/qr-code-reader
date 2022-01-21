import { Injectable } from '@angular/core';
import {AndroidPermissions} from '@awesome-cordova-plugins/android-permissions/ngx';

@Injectable({
  providedIn: 'root'
})
export class PermissionsService {

  constructor(private androidPermissions: AndroidPermissions) { }

  requestPermission(permissions: string[], action) {
    permissions = permissions.map(p => this.androidPermissions.PERMISSION[p]);
    this.androidPermissions.requestPermissions(permissions).then(result => {
      if (result.hasPermission) {
        action();
      }
    });
  }
}

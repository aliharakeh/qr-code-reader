import {Injectable} from '@angular/core';
import {from} from 'rxjs';
import {Camera, CameraResultType, ImageOptions, Photo} from '@capacitor/camera';
import {finalize, map, switchMap, take, tap} from 'rxjs/operators';
import {CaptureError, MediaCapture, MediaFile} from '@awesome-cordova-plugins/media-capture/ngx';
import {FileTransfer, FileTransferObject, FileUploadOptions} from '@awesome-cordova-plugins/file-transfer/ngx';
import {Filesystem} from '@capacitor/filesystem';
import {LoadingController} from '@ionic/angular';
import {SettingsService} from './settings.service';
import {TranslateService} from '@ngx-translate/core';

@Injectable({
  providedIn: 'root'
})
export class CameraService {

  cameraOptions: ImageOptions = {
    resultType: CameraResultType.Base64,
    quality: 100
  };
  image;
  imageFile;
  imageBase64;
  fileUploadOptions: FileUploadOptions = {
    fileKey: 'file',
    httpMethod: 'POST',
    chunkedMode: false,
    headers: {}
  };

  constructor(
    private transfer: FileTransfer,
    private mediaCapture: MediaCapture,
    private loadingCtrl: LoadingController,
    private urlService: SettingsService,
    private translate: TranslateService
  ) { }

  getPhoto() {
    return from(Camera.getPhoto(this.cameraOptions)).pipe(
      take(1),
      map((image: Photo) => {
        let webPath = image.webPath;
        if (!webPath) {
          switch (this.cameraOptions.resultType) {
            case CameraResultType.Base64:
              webPath = `data:image/${image.format};base64,` + image.base64String;
              break;
            case CameraResultType.DataUrl:
              webPath = image.dataUrl;
              break;
            case CameraResultType.Uri:
              webPath = image.path;
              break;
          }
        }
        return { ...image, webPath };
      }),
      switchMap(image => {
        this.image = image.webPath;
        return this.uploadNative();
      })
    );
  }

  captureVideo() {
    return from(
      this.mediaCapture.captureVideo({
        duration: 2
      })
    ).pipe(
      switchMap(data => this.uploadNative(data[0]))
    );
  }

  uploadNative(mediaFile: MediaFile = null) {
    let _loader;
    return from(
      this.loadingCtrl.create({
        message: this.translate.instant('SCANNING')
      })
    ).pipe(
      take(1),
      switchMap((loader: any) => {
        _loader = loader;
        _loader.present();
        const fileTransfer: FileTransferObject = this.transfer.create();
        let serverURL = this.urlService.appUrl + '/detectQRCodes';
        if (mediaFile) {
          this.fileUploadOptions.mimeType = 'video/mp4';
          serverURL += 'Video';
        }
        return from(fileTransfer.upload(mediaFile?.fullPath || this.image, serverURL, this.fileUploadOptions));
      }),
      map((res: any) => res.response),
      finalize(() => {
        _loader.dismiss();
        if (mediaFile) {
          console.log(mediaFile);
          Filesystem.deleteFile({
            path: mediaFile.fullPath
          })
            .then(res => console.log(res))
            .catch(err => {
              console.log(err);
              // const dir = /(.*)\/.*\.mp4/.exec(mediaFile.fullPath);
              // if (dir && dir.length > 0) {
              //   this.file.removeFile(dir[1], mediaFile.name)
              //     .then(res => console.log(res))
              //     .catch(err => console.log(err));
              // }
            });
        }
      })
    );
  }

}

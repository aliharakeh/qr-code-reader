const fs = require('fs');

const androidManifestXML = 'android/app/src/main/AndroidManifest.xml';

if (fs.existsSync(androidManifestXML)) {
    fs.readFile(androidManifestXML, 'utf-8', function (err, data) {
        if (err) throw err;

        if (data.includes('android:usesCleartextTraffic="true"')) {
            console.log(`Android HTTP is already enabled in [${androidManifestXML}]`);
            return;
        }

        const newValue = data.replace('<application', '<application android:usesCleartextTraffic="true"');

        fs.writeFile(androidManifestXML, newValue, 'utf-8', function (err) {
            if (err) throw err;
            console.log(`Completed Android HTTP Fix in [${androidManifestXML}]`);
        });
    });
}
else {
    console.log('Android Manifest XML was not found!!');
}

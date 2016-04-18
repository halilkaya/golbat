![Golbat](http://i.imgur.com/D92jDFf.png)

[![GPL Licence][licence-badge]](LICENSE)
[![Join the chat at https://gitter.im/halilkaya/golbat](https://badges.gitter.im/halilkaya/golbat.svg)](https://gitter.im/halilkaya/golbat?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

# Golbat
Golbat is an Android library that helps you on working with camera, gallery and output and its encoded types.

# Quick Start
Put it into your project! In your Gradle file:
```java
repositories {
    ...
    maven {
        url "https://jitpack.io"
    }
}

dependencies {
    ...
    compile 'com.github.halilkaya:golbat:0.0.1'
}
```

Golbat deals with storage. So, you need to take perrmissions below first:
```xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
```

To begin to use Golbat, build it in your `Application` class:
```java
new Golbat.Builder()
        .setDirectoryName("MyPhotos")
        .setContentResolver(getContentResolver())
        .setCameraRequestCode(200)
        .setImageQuality(100)
        .build();
```

Well, now let's make a simple app that opens camera app and shows the captured image in an ImageView via Golbat!

Define 3 global variables:
```java
private Button openCameraApp;
private ImageView myImageView;
private Uri fileUri;
```

Let them know who them are in `onCreate` method:
```java
openCameraApp = (Button) findViewById(R.id.open_camera_app);
myImageView = (ImageView) findViewById(R.id.my_image_view);
```

Open camera app when `openCameraApp` button is pressed in again `onCreate` method:
```java
openCameraApp.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        fileUri = Golbat.openCamera(MainActivity.this);
    }
});
```

Yeah! Our button opens the camera app! Let's handle the captured image:
```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == Activity.RESULT_OK && requestCode == 200) {
        Golbat.showImage(
                Golbat.CaptureType.CAPTURED_FROM_CAMERA,
                myImageView,
                fileUri
        );
    }
}
```

How does it look? Oh, API wants base64 encoded version? Here you go:
```java
String base64forApi = Golbat.getBase64(
        Golbat.getSelectedImageAsBitmap(fileUri),
        false
);
```

Voila!

Want more? Then, see the full [documentation](https://github.com/halilkaya/golbat/wiki/Documentation).


# License
```
Golbat is an Android library that helps on working with camera,
gallery and output and its encoded types.
Copyright (C) 2016  Halil Kaya

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
```

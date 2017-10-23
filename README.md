# Deprecated!

We have released a new WebRTC platform, [ECLWebRTC](https://webrtc.ecl.ntt.com/en/?origin=skyway), to take the place of SkyWay. We will be shutting down the SkyWay servers in March 2018. Customers who are currently using SkyWay are required to migrate to ECLWebRTC by then or their services will stop working.

If you are looking for the repository of ECLWebRTC, please see the [new examples](https://github.com/skyway/skyway-android-sdk/tree/master/examples).

このレポジトリは、2018年3月に提供を終了する旧SkyWayのAndroid SDKのサンプルアプリです。[新しいSkyWay](https://webrtc.ecl.ntt.com/?origin=skyway)への移行をお願いします。

すでに新しいSkyWayをご利用の方は、[新しいサンプルアプリ](https://github.com/skyway/skyway-android-sdk/tree/master/examples)をご覧ください。

# SKWAndroidSampleApp

## How to build (Android Studio)
 1. Register an account on [SkyWay](http://nttcom.github.io/skyway/) and get an API key
 1. Clone or download this repository.
 1. Open "SkyWay-Android-Sample"
 1. Make directory "app/libs" of your project. Add "SkyWay.aar" to the "app/libs".
  1. Download "SkyWay.aar" from [SkyWay](http://nttcom.github.io/skyway/)
 1. Set APIKey and Domain to your API key/Domain registered on SkyWay.io at both "DatActivity.java" and "MediaActivity" and build!
```Java
// Please check this page. >> https://skyway.io/ds/
//Enter your API Key and registered Domain.
options.key = "";
options.domain = "";
```
---

## ビルド方法 (Android Studio)
 1. [SkyWay](http://nttcom.github.io/skyway/)でアカウントを作成し、APIkeyを取得
 1. このレポジトリをクローンまたはダウンロード
 1. "SkyWay-Android-Sample"を開く
 1. プロジェクト内でapp/libsディレクトリ作成し、"SkyWay.aar"をapp/libsに追加
  1. "SkyWay.aar"は[SkyWay](http://nttcom.github.io/skyway/)からダウンロード
 1. "DataActivity.java" と "MediaActivity.java"のAPIKeyとDomainにAPIkeyとDomainを入力し、ビルド
```Java
// Please check this page. >> https://skyway.io/ds/
//Enter your API Key and registered Domain.
options.key = "";
options.domain = "";

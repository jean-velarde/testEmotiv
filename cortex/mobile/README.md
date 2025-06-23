
## Prerequisites
- Emotiv headset (if you don't have, you can purchase one [here](https://www.emotiv.com)) (optional)
- Emotiv account (if you don't have, you can register [here](https://www.emotiv.com))
- A pair of client id, client secret of your Cortex app (if you don't have, you can register [here](https://www.emotiv.com/my-account/cortex-apps/))

#### iOS
- iOS device (iOS version 13.0 or higher) (optional)
- XCode 11 or above

#### Android
* Android device with Android 7.0 (API level 24) or above (optional)
* Android Studio 4.1 or above.

## Understand EmotivCortexLib API

Check out the API doc: https://emotiv.gitbook.io/cortex-library-for-mobile/

#### `EmotivCortexLib` library
- You can find the latest releases [here](https://github.com/Emotiv/cortex-embedded-mobile-lib-example/releases).
- For iOS: please use the `EmotivCortexLib.xcframework`
- For Android: please use the `EmotivCortexLib.aar`
- `EmotivCortexLib` supports iOS simulators and Android emulators in case you don't have iOS/Android devices.
- `EmotivCortexLib` has APIs to create and use virtual headsets in case you don't have Emotiv headsets.

## Try the example
- Cortex Embedded Lib Example demonstrates how to work with [EmotivCortexLib API](https://emotiv.gitbook.io/cortex-library-for-mobile/).
- Open project `ios/CortexV2-Emotiv-iOS` by Xcode or `Android/CortexV2Example`. You can see CortexV2Example project is a set of modules, each module contains example code for a specific feature:

Module| Related Cortex embedded APIs
----------------|----------------
cortexclient | [Working with the EmotivCortexLib API](https://emotiv.gitbook.io/cortex-library-for-mobile/objective-c-api)
authorize | [Login](https://emotiv.gitbook.io/cortex-library-for-mobile/jsonapi) & [Authentication](https://emotiv.gitbook.io/cortex-api/authentication)
headset-session | [Headsets](https://emotiv.gitbook.io/cortex-api/headset) & [Sessions](https://emotiv.gitbook.io/cortex-api/session) & [Virtual headsets](https://emotiv.gitbook.io/cortex-library-for-mobile/jsonapi)
record-marker | [Record](https://emotiv.gitbook.io/cortex-api/records) & [Markers](https://emotiv.gitbook.io/cortex-api/markers)
eeg / motion / bandpower / facial-expression / mental-command / performance-metrics | [Data Subscription](https://emotiv.gitbook.io/cortex-api/data-subscription)
facial-expression-training / mental-command-training | [BCI](https://emotiv.gitbook.io/cortex-api/bci) & [Advanced BCI](https://emotiv.gitbook.io/cortex-api/advanced-bci)

- Before running example code, you need to replace values of these below constants:

In `cortexclient/src/main/java/com/emotiv/cortexv2example/utils/Constant.java` with Android project

    public final static String CLIENT_ID = "your_client_id";
    public final static String CLIENT_SECRET = "your_client_secret";

And `CortexV2-Emotiv-iOS/CortexV2-Emotiv-ObjC/CortexV2-Emotiv-ObjC/Config.m` with Objective C project

In `build.gradle` of each Android project

    String client_id = ""
    if (client_id.isEmpty())
        System.err << "You need to config value for client_id"
    else
    {
        String value = client_id.md5()
        manifestPlaceholders = [
            'appAuthRedirectScheme': 'emotiv-' + value
        ]
    }

If you want to run with a specific license. You also can change the value of `LICENSE_ID` in this file as well.

- Choose the module you want to build and run on mobile device. Make sure you accept all permission requests.
- The module app has a list of buttons demonstrating API calls. Try click on each button, you can see a pair of request/response json strings printed out in `Android Studio logcat` or `Xcode console`.

## Use EmotivCortexLib in your app 

### Import EmotivCortexLib

#### iOS
* Get the `EmotivCortexLib.xcframework` from github release page
* Include the framework into your Xcode project following [this guide](https://developer.apple.com/library/archive/documentation/MacOSX/Conceptual/BPFrameworks/Tasks/IncludingFrameworks.html).

#### Android
* Get the `EmotivCortexLib.aar` from github release page
* Include the framework into your Android Studio project following [this guide](https://developer.android.com/studio/projects/android-library#CreateLibrary).

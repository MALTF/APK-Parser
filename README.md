# APK-Parser <a href=""><img src="https://img.shields.io/badge/Build-pass-brightgreen"/></a> [![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://github.com/MALTF/APK-Parser/blob/main/LICENSE)
Apk parser lib, for decoding binary XML files, getting APK meta info.

---------------------------------------------------------------------

![Screenshot](/Screenshot.png =200x)

Table of Contents
=================

* [Features](#features)
* [Get APK-parser](#get-apk-parser)
* [Usage](#usage)
    * [1、APK Info](#1-apk-info)
    * [2、Get Binary XML and Manifest XML Files](#2-get-binary-xml-and-manifest-xml-files)
    * [3、Get DEX Classes](#3-get-dex-classes)
    * [4、Get APK Signing Info](#4-get-apk-sign-info)
    * [5、Locales](#5-locales)
* [Reporting Issues](#reporting-issues)

#### Features

* Retrieve APK meta info, such as title, icon, package name, version, etc.
* Parse and convert binary XML files to text 
* Get classes from DEX files
* Get APK singer info

#### Get APK-parser

Get apk-parser use source code integration.

#### Usage

The ordinary way is using the ApkFile class, which contains convenient methods to get AndroidManifest.xml, APK info, etc.
The ApkFile need to be closed when no longer used. 
There is also a ByteArrayApkFile class for reading APK files from byte array.

##### 1. APK Info

ApkMeta contains name(label), packageName, version, SDK, used features, etc.

```kotlin
val apkFile = ApkFile(filePath)
val apkMeta = apkFile.apkMeta
Log.d(
     "apkMate",
    "label:${apkMeta?.name} packageName:${apkMeta?.packageName} versionCode:${apkMeta?.versionCode}"
    )
val usesFeatures = apkMeta?.usesFeatures
if (usesFeatures != null) {
    for (index in 0 until usesFeatures.size) {
        val featureName = usesFeatures[index].name
        Log.d("featureName", "name:$featureName")
    }
}
apkFile.close()
```
##### 2. Get Binary XML and Manifest XML Files

```kotlin
val apkFile = ApkFile(filePath)
val manifestXml = apkFile.manifestXml
val transBinaryXml = apkFile.transBinaryXml("res/layout/acticity_main.xml")
Log.d("getXml", "manifestXml:${manifestXml} transBinaryXml:$transBinaryXml")
apkFile.close()
```

##### 3. Get DEX Classes

```kotlin
val apkFile = ApkFile(filePath)
val dexClasses = apkFile.getDexClasses()
dexClasses?.let {
    for (index in dexClasses.indices) {
        val dexClass = dexClasses[index]
        val dexHeader = dexClass?.dexHeader
        Log.d("getDex", "dexClass:$dexClass dexHeader:$dexHeader")
    }
}
apkFile.close()
```

##### 4. Get APK Sign Info

Get the APK signer certificate info and other messages, using:

```kotlin
val apkFile = ApkFile(filePath)
// apk v1 signers
val apkSingers = apkFile.apkSingers
// apk v2 signers
val apkV2Singers = apkFile.apkV2Singers
apkFile.close()
```

##### 5. Locales

An APK may have different info (title, icon, etc.) for different regions and languages——or we can call it a "locale".
If a locale is not set, the default "en_US" locale (<code>Locale.US</code>) is used. You can set a preferred locale by:

```kotlin
val apkFile = ApkFile(filePath)
apkFile.preferredLocale = Locale.SIMPLIFIED_CHINESE
val apkMeta = apkFile.apkMeta
Log.d(
    "apkMate",
    "label:${apkMeta?.name} packageName:${apkMeta?.packageName} versionCode:${apkMeta?.versionCode}"
)
apkFile.close()
```

apk-parser will find the best matching languages for the locale you specified.

If locale is set to null, ApkFile will not translate the resource tag, and instead just give the resource ID.
For example, the title will be something like '@string/app_name' instead of the real name.


#### Reporting Issues
If this parser has any problem with a specific APK, open a new issue, **with a link to download the APK file** thanks. 

---------------------------------------------------------------------
Apk parser is based on [hsiafan's apk-parser](https://github.com/hsiafan/apk-parser)

---------------------------------------------------------------------

## License
<a href="LICENSE"><img src="https://img.shields.io/github/license/fstudio/clangbuilder.svg"></a>
<a href="https://996.icu"><img src="https://img.shields.io/badge/link-996.icu-red.svg"></a>

本项目遵循[MIT license](https://github.com/MALTF/APK-Parser/blob/main/LICENSE)，方便交流与学习。如果您发现本项目有侵犯您的知识产权，请与我取得联系，我会及时修改或删除。

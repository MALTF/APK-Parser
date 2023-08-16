package com.malt.parser.test

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.malt.parser.ApkFile
import com.malt.parser.ApkParsers
import java.util.Locale

/**
 * @author by malt on 2023/8/16 11:02
 * #First Created Time:
 * @code @ProjectName：parser
 * @code @Package: com.malt.parser.test
 * @code @ClassName: MainActivity
 * @code @Description: parser lib, for decoding binary XML files, getting APK meta info.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var filePath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        filePath = applicationContext.packageResourcePath
        Log.d("filePath", "filePath:$filePath")

        if (filePath.isEmpty()) {
            return
        }

        findViewById<TextView>(R.id.info_tv).text = ApkParsers.getManifestXml(filePath)

        getApkMeta(filePath)
        getXml(filePath)
        getDex(filePath)
        getSigning(filePath)
        setLocales(filePath)
    }

    private fun setLocales(filePath: String) {
        val apkFile = ApkFile(filePath)
        apkFile.preferredLocale = Locale.SIMPLIFIED_CHINESE
        val apkMeta = apkFile.apkMeta
        Log.d(
            "apkMate",
            "label:${apkMeta?.name} packageName:${apkMeta?.packageName} versionCode:${apkMeta?.versionCode}"
        )
    }

    private fun getSigning(filePath: String) {
        val apkFile = ApkFile(filePath)
        // apk v1 signers
        val apkSingers = apkFile.apkSingers
        // apk v2 signers
        val apkV2Singers = apkFile.apkV2Singers
        Log.d("apkSingers", "apkSingers:$apkSingers apkV2Singers:$apkV2Singers")
    }

    private fun getDex(filePath: String) {
        val apkFile = ApkFile(filePath)
        val dexClasses = apkFile.getDexClasses()
        dexClasses?.let {
            for (index in dexClasses.indices) {
                val dexClass = dexClasses[index]
                val dexHeader = dexClass?.dexHeader
                Log.d("getDex", "dexClass:$dexClass dexHeader:$dexHeader")
            }
        }
    }

    private fun getXml(filePath: String) {
        val apkFile = ApkFile(filePath)
        val manifestXml = apkFile.manifestXml
        val transBinaryXml = apkFile.transBinaryXml("res/layout/acticity_main.xml")
        Log.d("getXml", "manifestXml:${manifestXml} transBinaryXml:$transBinaryXml")
    }

    private fun getApkMeta(filePath: String) {
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
    }
}
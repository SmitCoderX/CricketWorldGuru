package com.wedoapps.cricketLiveLine.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.wedoapps.cricketLiveLine.utils.Constants.BIG_BANNER
import com.wedoapps.cricketLiveLine.utils.Constants.FULL_ADS
import com.wedoapps.cricketLiveLine.utils.Constants.FULL_NAME
import com.wedoapps.cricketLiveLine.utils.Constants.SET_ADS
import com.wedoapps.cricketLiveLine.utils.Constants.SMALL_BANNER
import com.wedoapps.cricketLiveLine.utils.Constants.SMALL_NAME
import com.wedoapps.cricketLiveLine.utils.Constants.VOLUME_SET

class PreferenceManager(context: Context) {

    //val keyBannerAdsUrl = "URL"
    val keyBannerAdsPath = "PATH"
    var keyBannerAdsUrl = "BannerAdsURL"

    @JvmField
    var keyFullBannerAdsUrl = "AlertDialogBannerAdsURL"

    @JvmField
    var keyFullBannerAdsPath = "AlertDialogBannerAdsPath"

    var keyFireBaseToken = "KeyToken"


    private var prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun setVolumeOn(volume: Boolean) {
        prefs.edit().putBoolean(VOLUME_SET, volume).apply()
    }

    fun getVolumeOn(): Boolean {
        return prefs.getBoolean(VOLUME_SET, true)
    }
    fun setAdsVisible(ads: Boolean) {
        prefs.edit().putBoolean(SET_ADS, ads).apply()
    }

    fun getAdsVisible(): Boolean {
        return prefs.getBoolean(SET_ADS, false)
    }

    fun setFullAdsVisible(fullAds: Boolean) {
        prefs.edit().putBoolean(FULL_ADS, fullAds).apply()
    }

    fun getFullAdsVisible(): Boolean {
        return prefs.getBoolean(FULL_ADS, false)
    }

    fun getString(keyBannerAdsUrl: String, s: String): String {
        return prefs.getString(keyBannerAdsUrl, s)!!

    }

    fun setFullAdName(name: String) {
        prefs.edit().putString(FULL_NAME, name).apply()
    }

    fun getFullAdName(): String? {
        return prefs.getString(FULL_NAME, "")
    }

    fun setSmallAdName(name: String) {
        prefs.edit().putString(SMALL_NAME, name).apply()
    }

    fun getSmallAdName(): String? {
        return prefs.getString(SMALL_NAME, "")
    }


    fun putBoolean(key: String?, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun putString(key: String?, value: String?) {
        prefs.edit().putString(key, value).apply()
    }

    fun putFloat(key: String?, value: Float) {
        prefs.edit().putFloat(key, value).apply()
    }

    fun putLong(key: String?, value: Long) {
        prefs.edit().putLong(key, value).apply()
    }

    fun putInt(key: String?, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    fun getBoolean(key: String?, defaultValue: Boolean): Boolean {
        return prefs.getBoolean(key, defaultValue)
    }

    @JvmName("getString1")
    fun getString(key: String?, defValue: String?): String? {
        return prefs.getString(key, defValue)
    }

    fun getFloat(key: String?, defValue: Float): Float {
        return prefs.getFloat(key, defValue)
    }

    fun getInt(key: String?, defValue: Int): Int {
        return prefs.getInt(key, defValue)
    }

    fun getLong(key: String?, defValue: Long): Long {
        return prefs.getLong(key, defValue)
    }

    fun putSmallBannerURI(url: String) {
        prefs.edit().putString(SMALL_BANNER, url).apply()
    }

    fun getSmallBannerURI(): String? {
        return prefs.getString(SMALL_BANNER, "")
    }

    fun putDialogAdURI(url: String) {
        prefs.edit().putString(BIG_BANNER, url).apply()
    }

    fun getDialogAdURI(): String? = prefs.getString(BIG_BANNER, "")
}
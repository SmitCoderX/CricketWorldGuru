package com.wedoapps.cricketLiveLine.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.BufferedInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

object Constants {
    var KBOOKID = 1
    var KBOOKNAME = "SELF"
    const val SPLASH_DELAY = 3000L
    // var bannerAdsFilePath = "Files"
    var SMALL_BANNER_ADS_NAME = "BannerAds"
    var BIG_BANNER_ADS_NAME = "BannerAdsBig"
    var FULL_BANNER_ADS_NAME = "FullAdsName"
    var disclaimerMsg: String? = ""
    var mobileNo = ""
    var isFullAddsShow = false
    var isAdsVisible = false
    const val LOG_DEBUG = true
    const val FULL_NAME = "FullName"
    const val SMALL_NAME = "SmallName"
    const val ADS_STORAGE_PATH = "Ads/"
    const val SET_ADS = "SetSmallAds"
    const val VOLUME_SET = "VolumeSet"
    const val FULL_ADS = "SetFullAds"
    const val TAG = "Cricket World Guru"
    const val REPO = "Repository"
    const val BOWLER = "BOWLER"
    const val WICKET = "WICKET"
    const val PLAYER = "Players/"
    const val BALLS = "Balls/"
    const val ID = "ID"
    const val PID = "PID"
    const val TEAM1 = "Team1"
    const val TEAM2 = "Team2"
    const val REQUEST_PERMISSION = 100
    const val JOB_ID = 123
    const val SESSION_ID = "SessionID"
    const val SESSION_NAME = "SessionName"
    const val SERIES_DATA = "SeriesData"
    var COLLECTION_PATH_LIVE_MATCH = "LiveMatch"
    var COLLECTION_PATH_MATCH_LISTING = "MatchList"
    var MATCH_INDEX = "MatchIndex"
    var STATUS_LIVE = "LIVE"
    var STATUS_UPCOMING = "UPCOMING"
    var STATUS_COMPLETED = "COMPLETED"
    var STATUS_DONO = "DONO"
    var MatchStatus = "MatchStatus"
    var MatchDate = "MatchDate"
    var PLAYERS_PATH = "Players/"
    const val MY_REQUEST_CODE_APP_UPDATE = 14
    var TEAM_NAME_1 = ""
    var TEAM_NAME_2 = ""
    const val TOPIC_CRICKET_GURU = "CRICKETGURU"
    const val TOPIC_ALL = "all"
    var SMALL_BANNER = "SmallBanner"
    var BIG_BANNER = "BigBanner"
    var adsMobileNumber = ""
    var ADS_URL: String? = null

    @JvmField
    var bannerAdsFilePath = ""

    @JvmField
    var alertDialogAdsFilePath = ""

    @JvmField
    var bitmapBannerAds: Bitmap? = null
    var bitmapFullBannerAds: Bitmap? = null

    @JvmField
    var BANNER_URL = ""
    var FULL_BANNER_URL = ""
    const val BANNER_WIDTH = 720
    const val BANNER_HEIGHT = 60

    @JvmField
    var isUrl = false
 
    var ContactUs = ""

    var teleGramChanelLink = ""
    var adsMessage = ""

    var counterAdsShow = 0

    @JvmStatic
    val showAdMessageView: String
        get() = "CricketLineGuru"

    const val ALERT_OUTPUT = "AlertOutput"
    const val BANNER_OUTPUT = "BannerOutput"

    const val API_BASE_URL = "https://wedoapps.in/kap/"

    const val TOPIC_T20_CRICKET = "CRICKET_T_20"
    const val FIRE_BASE_NEW_TOKEN = "NewToken"
    var FIRE_BASE_TOKEN = ""

    var smallBannerAdsName = ""
    var bigBannerAdsName = ""
    var telegramLink = ""
    var adsMsg = ""
    var isPermissionReadGrant = false

    const val READ_STORAGE_REQUEST = 111

    const val FEEDBACK = "mailto:rudrainfotech12@gmail.com"
    const val shareToIphone = "https://itunes.apple.com/us/app/cricket-hisab-kitab/id1432436669?ls=1&mt=8"
    var contactUsUrl = ""

    fun sendToServer(token:String){

        val url = URL("https://fcm.googleapis.com/fcm/send")
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection

        try{

            connection.doOutput = true
            connection .doInput = true
            connection.requestMethod ="POST"

            val dos = DataOutputStream(connection.outputStream)
            dos.writeBytes("token=$token")
            connection.connect()

            if(connection.responseCode == HttpURLConnection.HTTP_OK){
                Log.d(TAG, """Token send server successfully""")
                connection.disconnect()
            }

        }catch (e: MalformedURLException){
            connection.disconnect()
            e.printStackTrace()

        }catch (e: IOException){
            connection.disconnect()
            e.printStackTrace()
        }

    }

    fun getImageBitmap(url: String?): Bitmap? {
        var bm: Bitmap? = null
        try {
            val aURL = URL(url)
            val conn = aURL.openConnection()
            conn.connect()
            val `is` = conn.getInputStream()
            val bis = BufferedInputStream(`is`)
            bm = BitmapFactory.decodeStream(bis)
            bis.close()
            `is`.close()
        } catch (e: IOException) {
            Log.e(TAG, "Error getting bitmap", e)
        }
        return bm
    }

    fun resizeBitmap(source: Bitmap, width: Int, height: Int): Bitmap? {
        var source = source
        if (source.height == height && source.width == width) return source
        val maxLength = kotlin.math.min(width, height)
        return try {
            source = source.copy(source.config, true)
            if (source.height <= source.width) {
                if (source.height <= maxLength) { // if image already smaller than the required height
                    return source
                }
                val aspectRatio = source.width.toDouble() / source.height.toDouble()
                val targetWidth = (maxLength * aspectRatio).toInt()
                Bitmap.createScaledBitmap(source, targetWidth, maxLength, false)
            } else {
                if (source.width <= maxLength) { // if image already smaller than the required height
                    return source
                }
                val aspectRatio = source.height.toDouble() / source.width.toDouble()
                val targetHeight = (maxLength * aspectRatio).toInt()
                Bitmap.createScaledBitmap(source, maxLength, targetHeight, false)
            }
        } catch (e: java.lang.Exception) {
            source
        }
    }

}

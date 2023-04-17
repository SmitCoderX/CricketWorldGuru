package com.wedoapps.cricketLiveLine.Workers

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.graphics.Bitmap
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.wedoapps.cricketLiveLine.utils.Constants
import com.wedoapps.cricketLiveLine.utils.Constants.TAG
import com.wedoapps.cricketLiveLine.utils.Constants.isAdsVisible
import com.wedoapps.cricketLiveLine.utils.PreferenceManager
import com.wedoapps.cricketLiveLine.utils.ReusedMethod
import com.wedoapps.cricketLiveLine.utils.ShowLogToast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class DownloadImageWorker(private val mContext: Context, workerParams: WorkerParameters) :
    Worker(mContext, workerParams) {

    private var firestore = FirebaseFirestore.getInstance()
    private val preference = PreferenceManager(mContext)
    override fun doWork(): Result {
        Log.d(TAG, Thread.currentThread().toString())

        return try {
            getAds()
          /*  getSmallBannerUrl()
            getFullAdUrl()*/
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }

    }

    private fun getAds() {
        firestore.collection(Constants.COLLECTION_PATH_LIVE_MATCH)
            .document(Constants.showAdMessageView)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(TAG, "Listen Failed", error)
                    return@addSnapshotListener
                }

                if (value != null) {
                    Log.d(TAG, "doWork Data: ${value.data}")
                    if (value.data!!.containsKey("AdsVisible")) {
                        isAdsVisible = value.getBoolean("AdsVisible")!!
                        preference.setAdsVisible(isAdsVisible)
                        isAdsVisible = preference.getAdsVisible()
                    } else {
                        isAdsVisible = false
                        preference.setAdsVisible(isAdsVisible)
                        isAdsVisible = preference.getAdsVisible()
                    }

                    if (value.data!!.containsKey("IsFullAdsShow")) {
                        Constants.isFullAddsShow = value.getBoolean("IsFullAdsShow")!!
                        preference.setFullAdsVisible(Constants.isFullAddsShow)
                        Constants.isFullAddsShow = preference.getFullAdsVisible()
                    }

                    if (value.data!!.containsKey("FullAdsName")) {
                        Constants.FULL_BANNER_ADS_NAME =
                            value.getString("FullAdsName")!!
                        preference.setFullAdName(value.getString("FullAdsName")!!)
                    }

                    if (value.data!!.containsKey("BannerAdsBig")) {
                        Constants.BIG_BANNER_ADS_NAME =
                            value.getString("BannerAdsBig")!!
                    }

                    if (value.data!!.containsKey("BannerAds")) {
                        Constants.SMALL_BANNER_ADS_NAME =
                            value.getString("BannerAds")!!
                        preference.setSmallAdName(value.getString("BannerAds")!!)
                        Log.d(TAG, "doWork: VALUE GOT")
                        Log.d(TAG, "doWork:" + Constants.SMALL_BANNER_ADS_NAME)
                    } else {
                        Log.d(TAG, "doWork: NO value")
                    }

                    if (value.data!!.containsKey("IsUrl")) {
                        Constants.isUrl = value.getBoolean("IsUrl") as Boolean
                    }

                    if (value.data!!.containsKey("UrlName")) {
                        Constants.ADS_URL = value.data!!["UrlName"] as String?
                    }

                    if (value.data!!.containsKey("Telegram")) {
                        Constants.teleGramChanelLink = value.data!!["Telegram"] as String
                    }

                    if (value.data!!.containsKey("Phone")) {
                        Constants.adsMobileNumber = value.data!!["Phone"] as String
                    }

                    if (value.data!!.containsKey("ContactUs")) {
                        Constants.ContactUs = value.data!!["ContactUs"] as String
                    }

                    if (value.data!!.containsKey("Message")) {
                        Constants.adsMessage = value.data!!["Message"] as String
                    }


                }
            }
    }

  /*  private fun getFullAdUrl() {
        if (!TextUtils.isEmpty(preference.getFullAdName())) {
            storageRef.child(Constants.ADS_STORAGE_PATH + preference.getFullAdName()).downloadUrl
                .addOnSuccessListener { uri ->
//                    val bitmap: Bitmap = CommonUtils.getImageBitmap(uri.toString())!!

                    val fullBannerUrl = uri.toString()
                    Log.d(TAG, "getFullBannerUrl: $fullBannerUrl")
                    val preferenceSmallBannerUrl =
                        preference.getString(preference.keyBannerAdsUrl, "")
                    if (TextUtils.isEmpty(preferenceSmallBannerUrl)) {
                        val thread = Thread {
                            try {
                                storeAlertDialogBannerFile(fullBannerUrl)

                                *//*storeImage(
                                    URL(fullBannerUrl),
                                    Constants.BIG_BANNER
                                )*//*
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                        }
                        thread.start()

                    } else {
                        if (preferenceSmallBannerUrl.contentEquals(fullBannerUrl)) {
                            preference.getString(preference.keyBannerAdsPath, "")
                            //homeFragment!!.setSmallBannerAdvertiseView()
                        } else {
                            val thread = Thread {
                                try {
                                    storeAlertDialogBannerFile(fullBannerUrl)
                                    Constants.FULL_BANNER_ADS_NAME
                                    *//* storeImage(
                                            URL(fullBannerUrl),
                                            Constants.BIG_BANNER
                                        )*//*

                                } catch (e: java.lang.Exception) {
                                    e.printStackTrace()
                                }
                            }
                            thread.start()
                        }
                    }
                }.addOnFailureListener { exception -> // Handle any errors
                    Log.d(TAG, "BigBanner image Failure: ${exception.message}")
                }
        } else {
            Log.d(TAG, "FullAdUrl: TextUtil Empty")
        }
    }


    private fun getSmallBannerUrl() {
        if (!TextUtils.isEmpty(preference.getSmallAdName())) {
            storageRef.child(Constants.ADS_STORAGE_PATH + preference.getSmallAdName()).downloadUrl
                .addOnSuccessListener { uri ->
//                    val bitmap: Bitmap = CommonUtils.getImageBitmap(uri.toString())!!

                    val smallBannerUrl = uri.toString()
                    ShowLogToast.showLog("Test", "getSmallBannerUrl: $smallBannerUrl")
                    val preferenceSmallBannerUrl =
                        preference.getString(preference.keyBannerAdsUrl, "")
                    if (TextUtils.isEmpty(preferenceSmallBannerUrl)) {
                        val thread = Thread {
                            try {
                                storeBannerFile(smallBannerUrl)
                                *//*storeImage(
                                    URL(smallBannerUrl),
                                    Constants.SMALL_BANNER
                                )*//*
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                        }
                        thread.start()

                    } else {
                        if (preferenceSmallBannerUrl.contentEquals(smallBannerUrl)) {
                            Constants.bannerAdsFilePath =
                                preference.getString(preference.keyBannerAdsPath, "")
                            ShowLogToast.showLog(
                                "Test Download Banner ads",
                                Constants.bannerAdsFilePath
                            )
                            val intent = Intent()
                            intent.action = "utils.AdsVisibleReceiver"
                            intent.flags = Intent.FILL_IN_ACTION
                            intent.putExtra("adsVisible", true)
                            intent.putExtra("isAdsVisible", isAdsVisible)
                            mContext.sendBroadcast(intent)

                            //homeFragment!!.setSmallBannerAdvertiseView()
                        } else {
                            val thread = Thread {
                                try {
                                    storeBannerFile(smallBannerUrl)
                                    *//*storeImage(
                                        URL(smallBannerUrl),
                                        Constants.SMALL_BANNER
                                    )*//*
                                } catch (e: java.lang.Exception) {
                                    e.printStackTrace()
                                }
                            }
                            thread.start()
                        }
                    }
                }.addOnFailureListener { exception -> // Handle any errors
                    Log.d(TAG, "Small image Failure: ${exception.message}")
                }

        }

    }

    private fun storeBannerFile(imgUrl: String) {
        preference.putString(preference.keyBannerAdsUrl, imgUrl)
        val fileName = "smallBannerCricket-Guru.png" //Name of the file
        val folder =
            applicationContext.getExternalFilesDir(null)!!.absolutePath // Name of the folder you want to keep your file in the local storage.
        val file = File(folder, "images")
        if (!file.exists()) {
            val isDir = file.mkdir()
            ShowLogToast.showLog("Test is dir", isDir.toString())
        }
        val file1 = File(file, fileName)
        Log.d("Test url", imgUrl)
        try {
            val b = file1.createNewFile() // creating the file inside the folder
            ShowLogToast.showLog("Test File path Banner==>", "" + b)
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
        try {
            val fileOut = FileOutputStream(file1) //Opening the file
            var bitmap = ReusedMethod.getImageBitmap(imgUrl)
            bitmap!!.compress(
                Bitmap.CompressFormat.PNG,
                100,
                fileOut
            ) //Writing all your row column inside the file
            bitmap = ReusedMethod.resizeBitmap(
                bitmap,
                Constants.BANNER_WIDTH,
                Constants.BANNER_HEIGHT
            )
            bitmap.compress(
                Bitmap.CompressFormat.PNG,
                100,
                fileOut
            ) //Writing all your row column inside the file
            fileOut.write(bitmap.rowBytes)
            applicationContext.openFileOutput(fileName, MODE_PRIVATE)
            fileOut.close() //closing the file and done
            Constants.BANNER_URL = imgUrl
            Constants.bitmapBannerAds = bitmap
            Constants.bannerAdsFilePath = file1.absolutePath
            preference.putString(preference.keyBannerAdsPath, Constants.bannerAdsFilePath)

            *//*if (!Constants.isPaidVersion) {
                if (dashBoardFragment != null) {
                    dashBoardFragment!!.setSmallBannerAdvertiseView()
                }
            }*//*
        } catch (e: IOException) {
            e.printStackTrace()
        }


    }

    @SuppressLint("TimberArgCount")
    private fun storeAlertDialogBannerFile(imgUrl: String) {
        preference.putString(preference.keyFullBannerAdsUrl, imgUrl)
        val fileName = "alertDialogBannerCricket-Guru.png" //Name of the file
        val folder =
            applicationContext.getExternalFilesDir(null)!!.absolutePath // Name of the folder you want to keep your file in the local storage.
        val file = File(folder, "images")
        if (!file.exists()) {
            val b = file.mkdir()
            ShowLogToast.showLog(TAG, file.absolutePath + "," + b)
        }
        val file1 = File(file, fileName)
        try {
            val b = file1.createNewFile() // creating the file inside the folder
            ShowLogToast.showLog("Test File path FullBanner==>", "" + b)
        } catch (e1: IOException) {
            e1.printStackTrace()
            Log.d(TAG, "storeAlertDialogBannerFile: ${e1.localizedMessage}")
        }
        try {
            val fileOut = FileOutputStream(file1) //Opening the file
            var bitmap = ReusedMethod.getImageBitmap(imgUrl)
            bitmap!!.compress(
                Bitmap.CompressFormat.PNG,
                100,
                fileOut
            ) //Writing all your row column inside the file
            bitmap = ReusedMethod.resizeBitmap(
                bitmap,
                Constants.BANNER_WIDTH,
                Constants.BANNER_HEIGHT
            )
            bitmap.compress(
                Bitmap.CompressFormat.PNG,
                100,
                fileOut
            ) //Writing all your row column inside the file
            fileOut.write(bitmap.rowBytes)
            applicationContext.openFileOutput(fileName, MODE_PRIVATE)
            fileOut.close() //closing the file and done
            Constants.alertDialogAdsFilePath = file1.absolutePath
            preference.putString(
                preference.keyFullBannerAdsPath,
                Constants.alertDialogAdsFilePath
            )
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(mContext, e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }*/


    /*private fun storeImage(url: URL, name: String) {
        val pictureFile = getOutputMediaFile(name)
        try {
            val fos = FileOutputStream(pictureFile, true)
            val bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, fos)
            fos.flush()
            fos.close()
            Log.d(TAG, "File Saved in LocalStorage: $fos")
        } catch (e: FileNotFoundException) {
            Log.d(TAG, "File not found: " + e.localizedMessage)
        } catch (e: IOException) {
            Log.d(TAG, "Error accessing file: " + e.message)
        }
    }

    private fun getOutputMediaFile(name: String): File {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        *//*val mediaStorageDir = File(
            Environment.getRootDirectory()
                .toString() + "/Android/data/"
                    + applicationContext.packageName
                    + "/Files"
        )*//*

        val mediaStorageDir = File(
            Environment.getDataDirectory()
                .toString() + "/" + applicationContext.packageName + "/Ads/images/Files/"
        )

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                val b = mediaStorageDir.mkdirs()
                Log.d(TAG, "File Path$b")
            }
        }
        // Create a media file name
        return File(mediaStorageDir.path + File.separator + "$name.jpeg")
    }*/
}
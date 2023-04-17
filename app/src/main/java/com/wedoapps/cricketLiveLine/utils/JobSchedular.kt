package com.wedoapps.cricketLiveLine.utils

import android.annotation.SuppressLint
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.graphics.Bitmap
import android.text.TextUtils
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.JsonObject
import com.wedoapps.cricketLiveLine.utils.Constants.TAG
import com.wedoapps.cricketLiveLine.utils.Constants.TEAM1
import com.wedoapps.cricketLiveLine.utils.Constants.TEAM2
import cricket.t20.api.RetrofitService
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


@SuppressLint("SpecifyJobSchedulerIdRange")
class JobSchedular : JobService() {

    private var preference: PreferenceManager? = null
    private var jobCancelled = false
    private val storageRef = FirebaseStorage.getInstance().reference
    private val retrofitService = RetrofitService.getInstance()
    var job: Job? = null
    var jobApplication: Job? = null
    var jobNotification: Job? = null
    private var apps: Apps? = null


    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.d(TAG, "Api Exception handled: ${throwable.localizedMessage}")
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        preference = PreferenceManager(this)
        getAllApplicationList()
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.d(TAG, "onStopJob: Stopped")
        jobCancelled = true
        return true
    }

    fun getAllApplicationList() {
        job = GlobalScope.launch {
            withContext(Dispatchers.Main + exceptionHandler) {
                val response = retrofitService.getAllAppsList()
                Log.d(TAG, "TestApplicationLE: ${response.errorBody().toString()}")
                Log.d(TAG, "TestApplicationL1: ${response}")
                Log.d(TAG, "TestApplicationLB: ${response.body().toString()}")
                if (response.isSuccessful) {

                    Log.d(TAG, response.body()!!.status.toString())
                    if (response.body()!!.status) {
                        val it: GetApplicationList? = response.body()
                        GlobalScope.launch {
                            for (i in it?.appsArrayList?.indices!!) {

                                if (it.appsArrayList[i].name == Constants.TAG) {
                                    apps = it.appsArrayList[i]
                                }

                            }

                            if (apps != null) {
                                //call for particular app.
                                val jsonObject = JsonObject()
                                jsonObject.addProperty("name", apps!!.name)
                                Log.d(TAG, "ApplicationList: $jsonObject")
                                getApplicationData(jsonObject)
                            }
                        }
                    } else {
                        Log.d(TAG, response.body()!!.message)
                    }

                } else {
                    Log.d(TAG, "TestApplicationLF: ${response.body()?.status}")
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getApplicationData(jsonObject: JsonObject) {
        jobApplication = GlobalScope.launch {

            val response: Call<ApplicationList> = retrofitService.sendApplicationNameToServer(jsonObject)
            withContext(Dispatchers.Main + exceptionHandler) {

                response.enqueue(object : Callback<ApplicationList> {
                    override fun onResponse(
                        call: Call<ApplicationList>, response: Response<ApplicationList>
                    ) {
                        Log.d(TAG, "TestApplicationE: ${response.errorBody().toString()}")
                        Log.d(TAG, "TestApplication1: ${response}")
                        Log.d(TAG, "TestApplicationB: ${response.body().toString()}")
                        if (response.isSuccessful) {

                            Log.d(TAG, response.body()!!.status.toString())
                            if (response.body()!!.status) {
                                val item: ApplicationList? = response.body()
                                Constants.adsMsg = item?.apps?.message.toString()
                                Constants.mobileNo = item?.apps?.phone.toString()

                                Constants.isAdsVisible = item?.apps?.is_banner_ads_visible == 1

                                preference!!.setAdsVisible(Constants.isAdsVisible)
                                Constants.isAdsVisible = preference!!.getAdsVisible()


                                Constants.isFullAddsShow = item?.apps?.is_big_ads_visible == 1
                                preference!!.setFullAdsVisible(Constants.isFullAddsShow)
                                Constants.isFullAddsShow = preference!!.getFullAdsVisible()


                                Constants.bigBannerAdsName = item?.apps?.big_ads.toString()
                                Constants.smallBannerAdsName = item?.apps?.banner_ads.toString()
                                Constants.telegramLink = item?.apps?.telegram.toString()
                                Constants.isUrl = item?.apps?.is_url == 1
                                Constants.ADS_URL = item?.apps?.url_name.toString()
                                Constants.disclaimerMsg = item?.apps?.url_name.toString()
                                Constants.contactUsUrl = item?.apps?.contactUs.toString()
                                val smallBannerUrl = item?.apps?.banner_ads.toString()
                                val preferenceSmallBannerUrl = preference!!.getString(preference!!.keyBannerAdsUrl, "")

                                if (TextUtils.isEmpty(preferenceSmallBannerUrl)) {
//                scopeForSaving.launch {
                                    storeBannerFileToDevice(smallBannerUrl)
//                }
                                } else {
                                    if (preferenceSmallBannerUrl.contentEquals(smallBannerUrl)) {
                                        Constants.bannerAdsFilePath =
                                            preference!!.getString(preference!!.keyBannerAdsPath, "")
//                    setSmallBannerAdvertiseView()
                                    } else {
//                    scopeForSaving.launch {
                                        storeBannerFileToDevice(smallBannerUrl)
//                    }
                                    }
                                }

                                val alertDialogBannerUrl = Constants.bigBannerAdsName
                                val preferenceAlertBannerUrl =
                                    preference!!.getString(preference!!.keyFullBannerAdsUrl, "")
                                if (TextUtils.isEmpty(preferenceAlertBannerUrl)) {
//                scopeForSaving.launch {
                                    storeBigBannerFileToDevice(alertDialogBannerUrl)
//                }
                                } else {
                                    if (preferenceAlertBannerUrl.contentEquals(alertDialogBannerUrl)) {
                                        Constants.alertDialogAdsFilePath = preference!!.getString(
                                            preference!!.keyFullBannerAdsUrl,
                                            ""
                                        )
                                    } else {
//                    scopeForSaving.launch {
                                        storeBigBannerFileToDevice(alertDialogBannerUrl)
//                    }
                                    }
                                }
                            } else {
                                Log.d(TAG,response.body()!!.message)
                            }

//                            loading.value = false
                        } else {
                            response.body()?.status?.let { Log.d(TAG, it.toString()) }
                            Log.d(TAG, "Error : ${response.message()} ")
                        }

                    }

                    override fun onFailure(call: Call<ApplicationList>, t: Throwable) {
                        Log.d(TAG, "TestApplicationF: ${t.localizedMessage}")

                        Log.d(TAG, "Error : ${t.localizedMessage} ")
                    }

                })


            }
        }
    }

    private fun storeBannerFileToDevice(imgUrl: String) {
//        withContext(Dispatchers.IO) {
        //Logic for saving the image goes here
        preference!!.putString(preference!!.keyBannerAdsUrl, imgUrl)
        val fileName = "smBannerT20Cricket.png" //Name of the file
        val folder =
            applicationContext!!.getExternalFilesDir(null)?.absolutePath// Name of the folder you want to keep your file in the local storage.

        val file = File(folder, "images")
        if (!file.exists()) {
            file.mkdir()
        }
        val file1 = File(file, fileName)

        try {
            file1.createNewFile() // creating the file inside the folder
            ShowLogToast.showLog("Test File path==>" + file1.absolutePath.toString())
        } catch (e1: IOException) {
            e1.printStackTrace()
            ShowLogToast.showLog("Test File path not create==>" + e1.localizedMessage)
            ShowLogToast.showLog("Test Folder path==>" + file.absolutePath.toString())
        }

        try {
            val fileOut = FileOutputStream(file1) //Opening the file
//            var bitmap  = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            var bitmap = Constants.getImageBitmap(imgUrl)
            bitmap!!.compress(
                Bitmap.CompressFormat.PNG,
                100,
                fileOut
            ) //Writing all your row column inside the file
            bitmap = Constants.resizeBitmap(
                bitmap,
                Constants.BANNER_WIDTH,
                Constants.BANNER_HEIGHT
            )
            bitmap!!.compress(
                Bitmap.CompressFormat.PNG,
                100,
                fileOut
            ) //Writing all your row column inside the file
            fileOut.close() //closing the file and done

            openFileOutput(fileName, Context.MODE_PRIVATE).use {
                it.write(file1.readBytes())
            }

            Constants.BANNER_URL = imgUrl
            Constants.bitmapBannerAds = bitmap
            Constants.bannerAdsFilePath = file1.absolutePath
            preference!!.putString(preference!!.keyBannerAdsPath, Constants.bannerAdsFilePath)

//            setSmallBannerAdvertiseView()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            ShowLogToast.showLog("Test File path File not Found Exception" + e.printStackTrace())
        } catch (e: IOException) {
            e.printStackTrace()
            ShowLogToast.showLog("Test File path IO Exception" + e.printStackTrace())
        }
//        }
    }

    private fun storeBigBannerFileToDevice(imgUrl: String) {
//        withContext(Dispatchers.IO) {
        preference!!.putString(preference!!.keyFullBannerAdsUrl, imgUrl)
        val fileName = "bigBannerT20Cricket.png" //Name of the file
        val folder =
            applicationContext!!.getExternalFilesDir(null)?.absolutePath// Name of the folder you want to keep your file in the internal storage.

        val file = File(folder, "images")
        if (!file.exists()) {
            file.mkdir()
        }
        val file1 = File(file, fileName)

        try {
            file1.createNewFile() // creating the file inside the folder
            ShowLogToast.showLog("Test File path AlertDialog==>" + file1.absolutePath.toString())
        } catch (e1: IOException) {
            e1.printStackTrace()
            ShowLogToast.showLog("Test File path AlertDialog not create==>" + e1.localizedMessage)
            ShowLogToast.showLog("Test Folder path AlertDialog==>" + file.absolutePath.toString())
        }

        try {
            val fileOut = FileOutputStream(file1) //Opening the file
            val bitmap = Constants.getImageBitmap(imgUrl)
            bitmap!!.compress(
                Bitmap.CompressFormat.PNG,
                100,
                fileOut
            ) //Writing all your row column inside the file
            fileOut.close() //closing the file and done
            openFileOutput(fileName, Context.MODE_PRIVATE).use {
                it.write(file1.readBytes())
            }

            Constants.alertDialogAdsFilePath = file1.absolutePath
            preference!!.putString(
                preference!!.keyFullBannerAdsUrl,
                Constants.alertDialogAdsFilePath
            )

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            ShowLogToast.showLog("Test File path  AlertDialog File not Found Exception" + e.printStackTrace())
        } catch (e: IOException) {
            e.printStackTrace()
            ShowLogToast.showLog("Test File path AlertDialog IO Exception" + e.printStackTrace())
        }
//        }
    }


    /*private fun saveFile(sourceUri: Uri, params: JobParameters?) {
        if (jobCancelled) {
            val contextWrapper = ContextWrapper(applicationContext)
            val direct = contextWrapper.getDir("/CricketLiveLine", Context.MODE_PRIVATE)

            if (!direct.exists()) {
                val wallpaperDirectory = File("/CricketLiveLine/")
                wallpaperDirectory.mkdirs()
            }

            val file = File("/CricketLiveLine/", sourceUri.toString())
            if (file.exists()) {
                file.delete()
            }
            try {
                val out = FileOutputStream(file)
                //val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, sourceUri)
                *//*val bitmap = if(Build.VERSION.SDK_INT>=29){
                    val source: ImageDecoder.Source = ImageDecoder.createSource(this.contentResolver
                        , sourceUri)
                    ImageDecoder.decodeBitmap(source)
                } else{
                    MediaStore.Images.Media.getBitmap(this.contentResolver, sourceUri)
                }*//*

                        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            ImageDecoder.decodeBitmap(
                                ImageDecoder.createSource(
                                    this.contentResolver,
                                    sourceUri
                                )
                            )
                        } else {
                            val stream = contentResolver.openInputStream(sourceUri)
                            BitmapFactory.decodeStream(stream)
                        }

                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                        out.flush()
                        out.close()
                        Log.d(TAG, "saveFile: File Saved")
                        jobFinished(params, false)
                    } catch (e: Exception) {
                        Log.d(TAG, "saveFile: ${e.localizedMessage}")
                        e.printStackTrace()
                    }
                }
            }*/
}
package com.wedoapps.cricketLiveLine.ui

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.tabs.TabLayout
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE
import com.google.android.play.core.install.model.UpdateAvailability.UPDATE_AVAILABLE
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.JsonObject
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.DexterError
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.wedoapps.cricketLiveLine.BuildConfig
import com.wedoapps.cricketLiveLine.R
import com.wedoapps.cricketLiveLine.databinding.ActivityMainBinding
import com.wedoapps.cricketLiveLine.db.CricketGuruDatabase
import com.wedoapps.cricketLiveLine.repository.CricketGuruRepository
import com.wedoapps.cricketLiveLine.ui.fragments.Live.LiveFragment
import com.wedoapps.cricketLiveLine.ui.fragments.home.HomeFragment
import com.wedoapps.cricketLiveLine.ui.fragments.more.MoreFragment
import com.wedoapps.cricketLiveLine.ui.fragments.recent.RecentFragment
import com.wedoapps.cricketLiveLine.ui.fragments.upcoming.UpComingFragment
import com.wedoapps.cricketLiveLine.utils.*
import com.wedoapps.cricketLiveLine.utils.Constants.TAG
import com.wedoapps.cricketLiveLine.utils.Constants.bannerAdsFilePath
import cricket.t20.api.RetrofitService
import kotlinx.coroutines.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : AppCompatActivity(){

    private var preference: PreferenceManager? = null
    private lateinit var mBinding: ActivityMainBinding
    private var isPermissionGranted = false
    private var keyFireBaseToken = ""
    private var apps: Apps? = null


    lateinit var viewModel: CricketGuruViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        ReusedMethod.setStatusBarGradiant(this)
        setContentView(mBinding.root)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val toolbar = mBinding.toolbar.toolbarTop
        mBinding.toolbar.backBtn.visibility = View.INVISIBLE

        mBinding.toolbar.shareBtn.setOnClickListener {
            shareApp()
        }

        val retrofitService = RetrofitService.getInstance()
        val repository = CricketGuruRepository(
            application, CricketGuruDatabase(this@MainActivity), retrofitService
        )
        val viewModelProvider = ViewModelProviderFactory(application, repository)
        viewModel = ViewModelProvider(this, viewModelProvider)[CricketGuruViewModel::class.java]

        preference = PreferenceManager(this@MainActivity)

        askMultiplePermission()

        lifecycleScope.launch {
            delay(50)
            checkAppUpdate()
            if (!TextUtils.isEmpty(Constants.FIRE_BASE_TOKEN)) {
                ShowLogToast.showLog(TAG, Constants.FIRE_BASE_TOKEN)
                verifyPushToken(Constants.FIRE_BASE_TOKEN)

            } else {
                ShowLogToast.showLog(TAG, Constants.FIRE_BASE_TOKEN)
                getFireBaseToken()
            }
            setViewPager()
            setupTabIcons()
            subscribeTopicForFireBaseMsg()
        }

        mBinding.linearAdsHome.setOnClickListener {
            ReusedMethod.gotoAdsContact(this)
        }
    }

    private fun shareApp() {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            var shareMessage = "Check out the Fastest Cricket Score App of the world - "
            shareMessage =
                "${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}".trimIndent()
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, "choose one"))
        } catch (e: Exception) {
            e.toString()
        }
    }

    private fun handleFullBannerUrl(url: String) {

    }

    private fun handleSmallBannerUrl(url: String) {

    }


    private fun checkNotificationPermission(requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_DENIED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    listOf(Manifest.permission.POST_NOTIFICATIONS).toTypedArray(),
                    requestCode
                )
            } else {
                callApi()
            }
        }
    }


    // Function to check and request permission.
    private fun askMultiplePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Dexter.withContext(this)
                .withPermissions(
                    Manifest.permission.POST_NOTIFICATIONS
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        /* ... */
                        if (report.areAllPermissionsGranted()) {
                            ShowLogToast.showLog("BetCalculator", "All permissions are granted!")
                            Constants.isPermissionReadGrant = true
                            callApi()
                        }
                        if (report.isAnyPermissionPermanentlyDenied) {
                            if (!isPermissionGranted) {
                                Constants.isPermissionReadGrant = false
                                showSettingsDialog()
                            }
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: List<PermissionRequest>,
                        token: PermissionToken,
                    ) {
                        /* ... */
                        token.continuePermissionRequest()
                    }
                }).withErrorListener { error: DexterError ->
                    ShowLogToast.showLog("Dexter", "There was an error: $error")
                }.onSameThread().check()
        } else {
            Dexter.withContext(this)
                .withPermissions(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        /* ... */
                        if (report.areAllPermissionsGranted()) {
                            ShowLogToast.showLog("BetCalculator", "All permissions are granted!")
                            Constants.isPermissionReadGrant = true
                            callApi()
                        }
                        if (report.isAnyPermissionPermanentlyDenied) {
                            if (!isPermissionGranted) {
                                Constants.isPermissionReadGrant = false
                                showSettingsDialog()
                            }
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: List<PermissionRequest>,
                        token: PermissionToken,
                    ) {
                        /* ... */
                        token.continuePermissionRequest()
                    }
                }).withErrorListener { error: DexterError ->
                    ShowLogToast.showLog("Dexter", "There was an error: $error")
                }.onSameThread().check()
        }


    }

    private fun showSettingsDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.alert_title_need_permission))
        builder.setMessage(resources.getString(R.string.alert_title_permission_settings))
        builder.setPositiveButton(
            resources.getString(R.string.ok)
        ) { dialog: DialogInterface, _: Int ->
            dialog.cancel()
            openSettings()
        }
        builder.setNegativeButton(
            resources.getString(R.string.cancel)
        ) { dialog: DialogInterface, _: Int -> dialog.cancel() }

        // Create the alert dialog
        val dialog = builder.create()

        // Finally, display the alert dialog
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()

        // Get the alert dialog buttons reference
        val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        positiveButton.setTextColor(resources.getColor(R.color.black, this.theme))
        positiveButton.setBackgroundColor(resources.getColor(R.color.white, this.theme))
        negativeButton.setTextColor(resources.getColor(R.color.colorRed, this.theme))
        negativeButton.setBackgroundColor(resources.getColor(R.color.white, this.theme))

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 20, 0)
        negativeButton.layoutParams = params
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // If request is cancelled, the result arrays are empty.
        if (requestCode == Constants.READ_STORAGE_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    askMultiplePermission()
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return
                }
                callApi()
            } else {
                Toast.makeText(
                    this,
                    "Permission denied Please Open in AppSetting screen.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    private fun openSettings() {
        isPermissionGranted = true
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        @Suppress("DEPRECATION")
        startActivityForResult(intent, Constants.READ_STORAGE_REQUEST)
    }


    private fun setupTabIcons() {
        mBinding.tabsHome.getTabAt(0)!!.icon =
            ContextCompat.getDrawable(this@MainActivity, R.drawable.selector_home_tab)

        mBinding.tabsHome.getTabAt(1)!!.icon =
            ContextCompat.getDrawable(this@MainActivity, R.drawable.selector_live_tab)

        mBinding.tabsHome.getTabAt(2)!!.icon =
            ContextCompat.getDrawable(this@MainActivity, R.drawable.selector_upcoming_tab)

        mBinding.tabsHome.getTabAt(3)!!.icon =
            ContextCompat.getDrawable(this@MainActivity, R.drawable.selector_recent_tab)

        mBinding.tabsHome.getTabAt(4)!!.icon =
            ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_more)


        /*  mBinding.tabsHome.getTabAt(3)!!.icon =
              ContextCompat.getDrawable(this@MainActivity, R.drawable.selector_calculator_tab)*/

        when {
            mBinding.tabsHome.getTabAt(0)!!.isSelected -> {
                mBinding.toolbar.txtToolbarTitle.text = resources.getString(R.string.app_name)
            }
            mBinding.tabsHome.getTabAt(1)!!.isSelected -> {
                mBinding.toolbar.txtToolbarTitle.text = resources.getString(R.string.app_name)
            }
            mBinding.tabsHome.getTabAt(2)!!.isSelected -> {
                mBinding.toolbar.txtToolbarTitle.text = resources.getString(R.string.app_name)
            }
            mBinding.tabsHome.getTabAt(3)!!.isSelected -> {
                mBinding.toolbar.txtToolbarTitle.text = resources.getString(R.string.app_name)
            }
            mBinding.tabsHome.getTabAt(4)!!.isSelected -> {
                mBinding.toolbar.txtToolbarTitle.text = resources.getString(R.string.app_name)
            }
            else -> {
//                mBinding.toolbar.txtToolbarTitle.text = resources.getString(R.string.calculator)
            }
        }


        mBinding.tabsHome.selectTab(mBinding.tabsHome.getTabAt(0))

    }

    private fun setViewPager() {
        setupViewPager(mBinding.viewpagerHome)

        mBinding.viewpagerHome.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int, positionOffset: Float, positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                /*if (position == 0) {
                    mBinding.txtTitleHome.text = resources.getString(R.string.cricket_khai_lagai)
                } else {
                    mBinding.txtTitleHome.text = resources.getString(R.string.khai_lagai_calculator)
                }*/
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        mBinding.tabsHome.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {
                        //
                        mBinding.toolbar.txtToolbarTitle.text = resources.getText(R.string.app_name)
                    }

                    1 -> {
                        //
                        mBinding.toolbar.txtToolbarTitle.text = resources.getText(R.string.app_name)
                    }

                    2 -> {
                        //
                        mBinding.toolbar.txtToolbarTitle.text = resources.getText(R.string.app_name)
                    }
                    3 -> {
                        //
                        mBinding.toolbar.txtToolbarTitle.text = resources.getText(R.string.app_name)
                    }
                    4 -> {
                        mBinding.toolbar.txtToolbarTitle.text = resources.getText(R.string.app_name)
                    }
                    /* 3 -> {
                         //
                         mBinding.toolbar.txtToolbarTitle.text =
                             resources.getText(R.string.calculator)
                     }*/
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        mBinding.tabsHome.setupWithViewPager(mBinding.viewpagerHome)

    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        val mHomeFragment = HomeFragment()
        val mLiveFragment = LiveFragment()
        val mUpComingFragment = UpComingFragment()
//        val mSquadListFragment = SquadListFragment()
        val mRecentFragment = RecentFragment()
        val mMoreFragment = MoreFragment()

//        val mBetCalculatorFragment = BetCalculatorFragment()

        adapter.addFragment(mHomeFragment, resources.getString(R.string.home))
        adapter.addFragment(mLiveFragment, resources.getString(R.string.live))
        adapter.addFragment(mUpComingFragment, resources.getString(R.string.upcoming))
        adapter.addFragment(mRecentFragment, resources.getString(R.string.recent))
        adapter.addFragment(mMoreFragment, resources.getString(R.string.more))
//        adapter.addFragment(mBetCalculatorFragment, resources.getString(R.string.calculator))
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 4
    }

    /*private fun startWorker() {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)

        val oneTimeWorkRequest = OneTimeWorkRequest.Builder(DownloadImageWorker::class.java)
            .setConstraints(constraints.build())
            .setInitialDelay(10, TimeUnit.SECONDS)
            .addTag(Constants.TAG)
            .build()


//        Toast.makeText(requireContext(), "Starting worker", Toast.LENGTH_SHORT).show()

        val workManager = WorkManager.getInstance(this@MainActivity)
            .enqueueUniqueWork(
                Constants.TAG,
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                oneTimeWorkRequest
            )

        ShowLogToast.showLog("WorkManger Result ", workManager.result.toString())
        WorkManager.getInstance(this@MainActivity)
            .getWorkInfoByIdLiveData(oneTimeWorkRequest.id).observe(this, Observer { workInfo ->
                val wasSuccess: Boolean
                if (workInfo != null && workInfo.state == WorkInfo.State.SUCCEEDED) {
                    wasSuccess = true
                    workInfo.outputData.getBoolean("is_success", false)
                } else {
                    wasSuccess = false
                }
                viewModel.getImageDownloadHandle(wasSuccess)
                //ShowLogToast.showToast(this, "Work was success: $wasSuccess", Toast.LENGTH_LONG)
            })

    }*/

    inner class ViewPagerAdapter(manager: FragmentManager?) : FragmentStatePagerAdapter(
        (manager)!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {
        private val mFragmentList: MutableList<Fragment> = ArrayList()
        private val mFragmentTitleList: MutableList<String> = ArrayList()


        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence {
            return mFragmentTitleList[position]
        }
    }

    private fun checkAppUpdate() {
        // Creates instance of the manager.
        val appUpdateManager = AppUpdateManagerFactory.create(this)

        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UPDATE_AVAILABLE
                // For a flexible update, use AppUpdateType.FLEXIBLE
                && appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)
            ) {
                // Request the update.

                appUpdateManager.startUpdateFlowForResult(
                    // Pass the intent that is returned by 'getAppUpdateInfo()'.
                    appUpdateInfo,
                    // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                    IMMEDIATE,
                    // The current activity making the update request.
                    this,
                    // Include a request code to later monitor this update request.
                    Constants.MY_REQUEST_CODE_APP_UPDATE
                )


            }
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.READ_STORAGE_REQUEST) {
                Constants.isPermissionReadGrant = true
                callApi()
                //getIsActive();
            }
            // for app Update
            else if (requestCode == Constants.MY_REQUEST_CODE_APP_UPDATE) {
                if (resultCode != RESULT_OK) {
                    // If the update is cancelled or fails,
                    // you can request to start the update again.
                    checkAppUpdate()
                }
            }

        }
    }

    private fun subscribeTopicForFireBaseMsg() {
        FirebaseInstallations.getInstance().getToken(true).addOnCompleteListener { task ->
            if (task.isSuccessful && task.result != null) {
                Log.d(
                    TAG, "Installation auth token: ${task.result!!.token}"
                )
                Constants.sendToServer(task.result!!.token)

            } else {
                Log.d(TAG, "Installations Unable to get Installation auth token")
            }
        }

        FirebaseMessaging.getInstance().subscribeToTopic(Constants.TOPIC_ALL)
        FirebaseMessaging.getInstance().subscribeToTopic(Constants.TOPIC_T20_CRICKET)

    }

    private fun getFireBaseToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                ShowLogToast.showLog(
                    TAG + task.exception, "Fetching FCM registration token failed"
                )
                return@OnCompleteListener
            } else {
                ShowLogToast.showLog("Token getting Success on FCM", task.result.toString())
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            val msg = token.toString()
            ShowLogToast.showLog(TAG, "Token Refresh:$msg")
            verifyPushToken(msg)
            FirebaseMessaging.getInstance().isAutoInitEnabled = true

        })

    }

    private fun verifyPushToken(msg: String) {
        if (!TextUtils.isEmpty(msg)) {

            if (preference == null) {
                preference = PreferenceManager(this@MainActivity)
            }

            val preferenceToken: String =
                preference!!.getString(preference!!.keyFireBaseToken, "").toString()

            if (TextUtils.isEmpty(preferenceToken) or !preferenceToken.contentEquals(msg)) {
                ShowLogToast.showLog(TAG + "Preference Token->", preferenceToken)
                ShowLogToast.showLog(TAG + "Token->", msg)
                sendTokenToServer(msg)
            }

        }
    }

    private fun sendTokenToServer(pushToken: String) {
        if (ReusedMethod.isNetworkConnected(this@MainActivity)) {
            //val appId: String = applicationContext.packageName
            val appId: String = "6"
            val modelName: String = Build.MODEL
            val os: String = Build.VERSION.RELEASE
            val osVersion: String = Build.VERSION.SDK_INT.toString()
            val deviceName: String = Build.DEVICE

            ShowLogToast.showLog(TAG, "Fcm Token->$pushToken")
            println(
                "Test Notification Data-->" + " App Id" + appId + "Model Name" + modelName + "Device Name " + deviceName + " os" + os + "osVersion" + osVersion + "FcmToken" + pushToken

            )

            val fcmInJson = JsonObject()
            fcmInJson.addProperty("app_id", appId)
            fcmInJson.addProperty("model_name", modelName)
            fcmInJson.addProperty("os", os)
            fcmInJson.addProperty("os_version", osVersion)
            fcmInJson.addProperty("push_token", pushToken)

            Log.d(TAG, "sendTokenToServer: $fcmInJson")
            viewModel.sendApiNotificationData(fcmInJson)

            viewModel.statusVal.observe(this) {

            }

            viewModel.apiNotificationData.observe(this) {

                preference!!.putString(
                    preference!!.keyFireBaseToken, it?.token?.pushToken
                )
            }


        } else {
            Log.d(TAG, "sendTokenToServer: Something Went Wrong}")
//            displayMessage(getString(R.string.internet_msg))
        }
    }

    private fun callApi() {
        if (ReusedMethod.isNetworkConnected(this)) {
            viewModel.applicationList.observe(this) { it ->
                Log.d(TAG, "ApplicationList: $it")
                for (i in it.appsArrayList.indices) {

                    if (it.appsArrayList[i].name == TAG) {
                        apps = it.appsArrayList[i]
                    }

                }

                apps?.let {
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("name", apps!!.name)
                    Log.d(TAG, "ApplicationList: $jsonObject")
                    viewModel.getApplicationData(jsonObject)
                }
            }

            viewModel.applicationData.observe(this) { item ->

                Log.d(TAG, "callApi: $item")
                //  ShowLogToast.showToast(this@MainActivity,"test AppName",1)
//                ShowLogToast.showLog("test AppName-->", it.apps.name)
//                ShowLogToast.showLog("test bannerAds-->", it.apps.banner_ads)
//                ShowLogToast.showLog("test isBannerAdsVisible-->",
//                    it.apps.is_banner_ads_visible.toString())
//                ShowLogToast.showLog("test is bigAdsVisible", it.apps.is_big_ads_visible.toString())
//                ShowLogToast.showLog("test isUrl-->", it.apps.is_url.toString())
//                ShowLogToast.showLog("test BigAds->", it.apps.big_ads.toString())
//                ShowLogToast.showLog("test phone-->", it.apps.phone.toString())
//                ShowLogToast.showLog("test telegram-->", it.apps.telegram.toString())
//                ShowLogToast.showLog("test url_name-->", it.apps.url_name.toString())

                Constants.adsMsg = item.apps.message.toString()
                Constants.mobileNo = item.apps.phone.toString()

                Constants.isAdsVisible = item.apps.is_banner_ads_visible == 1

                preference!!.setAdsVisible(Constants.isAdsVisible)
                Constants.isAdsVisible = preference!!.getAdsVisible()


                Constants.isFullAddsShow = item.apps.is_big_ads_visible == 1
                preference!!.setFullAdsVisible(Constants.isFullAddsShow)
                Constants.isFullAddsShow = preference!!.getFullAdsVisible()


                Constants.bigBannerAdsName = item.apps.big_ads.toString()
                Constants.smallBannerAdsName = item.apps.banner_ads.toString()
                Constants.telegramLink = item.apps.telegram.toString()
                Constants.isUrl = item.apps.is_url == 1
                Constants.ADS_URL = item.apps.url_name.toString()
                Constants.disclaimerMsg = item.apps.url_name.toString()
                Constants.contactUsUrl = item.apps.contactUs.toString()
                val smallBannerUrl = item.apps.banner_ads.toString()
                val preferenceSmallBannerUrl =
                    preference!!.getString(preference!!.keyBannerAdsUrl, "")


                if (item.apps.is_banner_ads_visible == 1) {
                    if (TextUtils.isEmpty(preferenceSmallBannerUrl)) {
                        Log.d(TAG, "callApiBannerAd: Check")
                        storeBannerFileToDevice(smallBannerUrl)
                    } else {
                        if (preferenceSmallBannerUrl.contentEquals(smallBannerUrl)) {
                            bannerAdsFilePath =
                                preference!!.getString(preference!!.keyBannerAdsPath, "")
                            Log.d(TAG, "callApiBannerAd: Check")
                            setSmallBannerAdvertiseView()
                        } else {
                            Log.d(TAG, "callApiBannerAd: Check")
                            storeBannerFileToDevice(smallBannerUrl)
                        }
                    }
                }

                val alertDialogBannerUrl = item.apps.big_ads.toString()
                val preferenceAlertBannerUrl =
                    preference!!.getString(preference!!.keyFullBannerAdsUrl, "")
                if (item.apps.is_big_ads_visible == 1) {
                    if (TextUtils.isEmpty(preferenceAlertBannerUrl)) {
                        storeBigBannerFileToDevice(alertDialogBannerUrl)
                    } else {
                        if (preferenceAlertBannerUrl.contentEquals(alertDialogBannerUrl)) {
                            Constants.alertDialogAdsFilePath = preference!!.getString(
                                preference!!.keyFullBannerAdsPath,
                                ""
                            )
                        } else {
                            storeBigBannerFileToDevice(alertDialogBannerUrl)
                        }
                    }
                }
            }


            viewModel.errorMessage.observe(this) {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }

            viewModel.statusVal.observe(this) {
                ShowLogToast.showLog(TAG, it.toString())
            }

            //call for all application data
            viewModel.getAllApplicationList()
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
            bannerAdsFilePath = file1.absolutePath
            preference!!.putString(preference!!.keyBannerAdsPath, bannerAdsFilePath)

            Log.d(TAG, "storeBannerFileToDevice: $bannerAdsFilePath")
            setSmallBannerAdvertiseView()
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

            Constants.FULL_BANNER_URL = imgUrl
            Constants.bitmapFullBannerAds = bitmap
            Constants.alertDialogAdsFilePath = file1.absolutePath
            preference!!.putString(
                preference!!.keyFullBannerAdsPath,
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

    fun setSmallBannerAdvertiseView() {
        Log.d(TAG, "setSmallBannerAdvertiseView: $bannerAdsFilePath")
        if (Constants.isAdsVisible && !TextUtils.isEmpty(bannerAdsFilePath)) {
            try {
                val bitmap: Bitmap = BitmapFactory.decodeFile(bannerAdsFilePath)!!
                val mDrawableAdsBigBanner = BitmapDrawable(resources, bitmap)
                mBinding.imgSmallBannerHome.setImageDrawable(mDrawableAdsBigBanner)
                mBinding.linearAdsHome.visibility = View.VISIBLE
            } catch (e: Exception) {
                mBinding.linearAdsHome.visibility = View.GONE
                Log.d(TAG, "setSmallBannerAdvertiseView: ${e.message}")
            }

        } else {
            mBinding.linearAdsHome.visibility = View.GONE
        }


    }

    override fun onResume() {
        super.onResume()
        if (isPermissionGranted) {
            askMultiplePermission()
        }
    }

}
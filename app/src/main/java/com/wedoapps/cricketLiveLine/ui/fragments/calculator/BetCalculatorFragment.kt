package com.wedoapps.cricketLiveLine.ui.fragments.calculator

import android.content.Context
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.wedoapps.cricketLiveLine.R
import com.wedoapps.cricketLiveLine.adapter.HomeCardAdapter
import com.wedoapps.cricketLiveLine.databinding.FragmentBetCalculatorBinding
import com.wedoapps.cricketLiveLine.ui.CricketGuruViewModel
import com.wedoapps.cricketLiveLine.ui.MainActivity
import com.wedoapps.cricketLiveLine.utils.*
import com.wedoapps.cricketLiveLine.utils.Constants.TAG
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException


class BetCalculatorFragment : Fragment(R.layout.fragment_bet_calculator),View.OnClickListener , AdsVisibleReceiver.ConnectivityReceiverListener{

    private lateinit var mBinding: FragmentBetCalculatorBinding
    private lateinit var cardAdapter: HomeCardAdapter
    private lateinit var viewModel: CricketGuruViewModel
    private var connectivityReceiver: AdsVisibleReceiver? = null

    private var team1 = ArrayList<String>()
    private var team2 = ArrayList<String>()
    private var mContext:Context?=null
    private var preference: PreferenceManager? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        preference = PreferenceManager(mContext!!)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = FragmentBetCalculatorBinding.bind(view)

        mBinding.linearAdsBetCal.setOnClickListener(this)

        Constants.bannerAdsFilePath = preference!!.getString(preference!!.keyBannerAdsPath, "")
        Constants.isAdsVisible= preference!!.getAdsVisible()
        setSmallBannerAdvertiseView()
        /*setImage(
            Environment.getRootDirectory()
                .toString() + "/Android/data/" + requireActivity().applicationContext.packageName + "/Files/Banner.jpeg"
        )*/


        /*mBinding.cardViewMatchBetCal.setOnClickListener(this)
        mBinding.cardViewPartyBetCal.setOnClickListener(this)
        mBinding.cardViewHawalaBetCal.setOnClickListener(this)
        mBinding.cardViewReportBetCal.setOnClickListener(this)*/

    }


    private fun setImage(imgPath: String) {
        try {
            val f = File(imgPath)

            val b = BitmapFactory.decodeStream(FileInputStream(f))
            mBinding.imgSmallBannerBetCal.setImageBitmap(b)
            Log.d(TAG, "setImage: DOne")

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            Log.d(TAG, "setImage: $e")
        }
    }

    private fun setSmallBannerAdvertiseView() {
        //Constants.isAdsVisible = true
        if (Constants.isAdsVisible && !TextUtils.isEmpty(Constants.bannerAdsFilePath)) {
            try {
                val bitmap = BitmapFactory.decodeFile(Constants.bannerAdsFilePath)
                val mDrawableAdsBigBanner: Drawable = BitmapDrawable(mContext!!.resources, bitmap)
                mBinding.imgSmallBannerBetCal.setImageDrawable(mDrawableAdsBigBanner)
            } catch (e: Exception) {
                ShowLogToast.showLog("BetCalc", e.localizedMessage)
            }
            mBinding.linearAdsBetCal.visibility = View.VISIBLE
        } else {
            mBinding.linearAdsBetCal.visibility = View.GONE
        }

        /*if (!Constants.isPaidVersion) {

        } else {
            mBinding.linearAdsHome.visibility = View.GONE
        }*/

    }

    override fun onStart() {
        super.onStart()
        setConnectivityAdsVisible()
        setSmallBannerAdvertiseView()
//        startWorker()
       /* setImage(
            Environment.getRootDirectory()
                .toString() + "/Android/data/" + requireActivity().applicationContext.packageName + "/Files/Banner.jpeg"
        )*/
    }

    public override fun onStop() {
        if (connectivityReceiver!!.isOrderedBroadcast) {
            mContext!!.unregisterReceiver(connectivityReceiver)
        }
        super.onStop()
    }


    override fun onClick(p0: View?) {
        when(p0!!.id){
                R.id.linearAdsBetCal->{
                    gotoAds()
                }

            /* R.id.cardViewMatchBetCal -> {
                gotoMatchActivity()
            }
            R.id.cardViewPartyBetCal -> {
                gotoPartyActivity()
            }
            R.id.cardViewHawalaBetCal -> {
                gotoHawalaActivity()
            }
            R.id.cardViewReportBetCal -> {
                gotoReport()
            } */

        }

    }

    private fun gotoAds(){
        ReusedMethod.gotoAdsContact(context!!)
    }
    private fun gotoMatchActivity() {

    }

    private fun gotoPartyActivity() {

    }

    private fun gotoHawalaActivity() {

    }

    private fun gotoReport() {

    }


    private fun setConnectivityAdsVisible() {
        val intentFilter = IntentFilter()
        @Suppress("DEPRECATION")
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        connectivityReceiver = AdsVisibleReceiver()
        mContext!!.registerReceiver(connectivityReceiver, intentFilter)

        // register connection status listener
        CricketGuruApplication.instance!!.setConnectivityListener(this)
    }
    override fun onAdsVisibilityNotifyChanged(isAdsVisibleReceiver: Boolean) {
        if(isAdsVisibleReceiver){
            setSmallBannerAdvertiseView()
        }
    }

}
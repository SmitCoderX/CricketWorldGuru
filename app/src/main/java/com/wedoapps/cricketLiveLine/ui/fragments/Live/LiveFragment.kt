package com.wedoapps.cricketLiveLine.ui.fragments.Live

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.wedoapps.cricketLiveLine.R
import com.wedoapps.cricketLiveLine.adapter.HomeCardAdapter
import com.wedoapps.cricketLiveLine.databinding.FragmentLiveBinding
import com.wedoapps.cricketLiveLine.model.HomeMatch
import com.wedoapps.cricketLiveLine.ui.CricketGuruViewModel
import com.wedoapps.cricketLiveLine.ui.MainActivity
import com.wedoapps.cricketLiveLine.ui.fragments.singleMatch.ViewPagerActivity
import com.wedoapps.cricketLiveLine.utils.Constants
import com.wedoapps.cricketLiveLine.utils.PreferenceManager
import com.wedoapps.cricketLiveLine.utils.ReusedMethod
import com.wedoapps.cricketLiveLine.utils.ShowLogToast
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException

class LiveFragment : Fragment(R.layout.fragment_live), View.OnClickListener,
    HomeCardAdapter.SetOnClick {

    private lateinit var mBinding: FragmentLiveBinding
    private lateinit var cardAdapter: HomeCardAdapter
    private lateinit var viewModel: CricketGuruViewModel
    private var team1 = ArrayList<String>()
    private var team2 = ArrayList<String>()
    private var mContext: Context? = null
    private var preference: PreferenceManager? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = FragmentLiveBinding.bind(view)

        preference = PreferenceManager(requireContext())

        mBinding.linearAdsLive.setOnClickListener(this)
        viewModel = (activity as MainActivity).viewModel

        cardAdapter = HomeCardAdapter(this)

        viewModel.getLiveMatch().observe(requireActivity()) {
            cardAdapter.differ.submitList(it)

            it.forEach { data ->
                team1.add(data.team1.toString())
                team2.add(data.team2.toString())
            }

            if (it.isNotEmpty()) {
                setRecyclerViewVisibility(true)
            } else {
                setRecyclerViewVisibility(false)
            }
            ShowLogToast.showLog("Test Upcoming Array Size", it.size.toString())
        }


        mBinding.recyclerViewLive.apply {
            setHasFixedSize(true)
            adapter = cardAdapter
        }
/*
        Constants.bannerAdsFilePath = preference?.getSmallBannerURI().toString()
        Constants.isAdsVisible = preference!!.getAdsVisible()

        setSmallBannerAdvertiseView()*/
    }

    private fun setRecyclerViewVisibility(b: Boolean) {
        if (b) {
            mBinding.txtNoMatchLive.visibility = View.GONE
            mBinding.recyclerViewLive.visibility = View.VISIBLE
        } else {
            mBinding.recyclerViewLive.visibility = View.GONE
            mBinding.txtNoMatchLive.visibility = View.VISIBLE
        }
    }

    override fun onClick(match: HomeMatch) {
        val intentUpComingFragment = Intent(requireActivity(), ViewPagerActivity::class.java)
        intentUpComingFragment.putExtra("match", match)
        startActivity(intentUpComingFragment)
    }

    private fun setSmallBannerAdvertiseView() {
        if (Constants.isAdsVisible && !TextUtils.isEmpty(Constants.bannerAdsFilePath)) {

            try {
                val bitmap: Bitmap = BitmapFactory.decodeFile(Constants.bannerAdsFilePath)!!
                val mDrawableAdsBigBanner = BitmapDrawable(resources, bitmap)
                mBinding.imgSmallBannerLive.setImageDrawable(mDrawableAdsBigBanner)
                mBinding.linearAdsLive.visibility = View.VISIBLE
            } catch (e: Exception) {
                mBinding.linearAdsLive.visibility = View.GONE
                Log.d(Constants.TAG, "setSmallBannerAdvertiseView: ${e.message}")
            }

        } else {
            mBinding.linearAdsLive.visibility = View.GONE
        }

    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.linearAdsLive -> {
                ReusedMethod.gotoAdsContact(mContext!!)
            }
        }
    }

    private fun setImage(imgPath: String) {
        try {
            val f = File(imgPath)

            val b = BitmapFactory.decodeStream(FileInputStream(f))
            mBinding.imgSmallBannerLive.setImageBitmap(b)
            Log.d(Constants.TAG, "setImage: DOne")
            mBinding.imgSmallBannerLive.visibility = View.VISIBLE
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            Log.d(Constants.TAG, "setImage: $e")
            mBinding.imgSmallBannerLive.visibility = View.GONE
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
//        setSmallBannerAdvertiseView()
        cardAdapter.notifyDataSetChanged()
    }

    override fun onStart() {
        super.onStart()
//        setSmallBannerAdvertiseView()
    }
}
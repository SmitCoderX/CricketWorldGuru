package com.wedoapps.cricketLiveLine.ui.fragments.upcoming

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
import com.wedoapps.cricketLiveLine.databinding.FragmentUpComingBinding
import com.wedoapps.cricketLiveLine.model.HomeMatch
import com.wedoapps.cricketLiveLine.ui.CricketGuruViewModel
import com.wedoapps.cricketLiveLine.ui.MainActivity
import com.wedoapps.cricketLiveLine.ui.fragments.singleMatch.ViewPagerActivity
import com.wedoapps.cricketLiveLine.utils.Constants
import com.wedoapps.cricketLiveLine.utils.Constants.TAG
import com.wedoapps.cricketLiveLine.utils.Constants.bannerAdsFilePath
import com.wedoapps.cricketLiveLine.utils.Constants.isAdsVisible
import com.wedoapps.cricketLiveLine.utils.PreferenceManager
import com.wedoapps.cricketLiveLine.utils.ReusedMethod
import com.wedoapps.cricketLiveLine.utils.ShowLogToast
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException

class UpComingFragment : Fragment(R.layout.fragment_up_coming),
    View.OnClickListener, HomeCardAdapter.SetOnClick {

    private lateinit var mBinding: FragmentUpComingBinding
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
        mBinding = FragmentUpComingBinding.bind(view)

        preference = PreferenceManager(requireContext())

        mBinding.linearAdsUpcoming.setOnClickListener(this)
        viewModel = (activity as MainActivity).viewModel

        cardAdapter = HomeCardAdapter(this)

        viewModel.getAllUpComingMatch().observe(requireActivity()) {
            cardAdapter.differ.submitList(it?.toMutableList())

            it.forEach { data ->
                team1.add(data.team1.toString())
                team2.add(data.team2.toString())
            }


            if (it.size > 0) {
                setRecyclerViewVisibility(true)
            } else {
                setRecyclerViewVisibility(false)
            }
            ShowLogToast.showLog("Test Upcoming Array Size", it.size.toString())
        }


        mBinding.recyclerViewUpcoming.apply {
            setHasFixedSize(true)
            adapter = cardAdapter
        }

      /*  bannerAdsFilePath = preference?.getSmallBannerURI().toString()
        isAdsVisible = preference!!.getAdsVisible()

        setSmallBannerAdvertiseView()*/
    }

    private fun setRecyclerViewVisibility(b: Boolean) {
        if (b) {
            mBinding.txtNoMatchUpcoming.visibility = View.GONE
            mBinding.recyclerViewUpcoming.visibility = View.VISIBLE
        } else {
            mBinding.recyclerViewUpcoming.visibility = View.GONE
            mBinding.txtNoMatchUpcoming.visibility = View.VISIBLE
        }
    }

    override fun onClick(match: HomeMatch) {
        val intentUpComingFragment = Intent(requireActivity(), ViewPagerActivity::class.java)
        intentUpComingFragment.putExtra("match", match)
        startActivity(intentUpComingFragment)
    }

    private fun setSmallBannerAdvertiseView() {
        if (isAdsVisible && !TextUtils.isEmpty(bannerAdsFilePath)) {

            try {
                val bitmap: Bitmap = BitmapFactory.decodeFile(bannerAdsFilePath)!!
                val mDrawableAdsBigBanner = BitmapDrawable(resources, bitmap)
                mBinding.imgSmallBannerUpcoming.setImageDrawable(mDrawableAdsBigBanner)
                mBinding.linearAdsUpcoming.visibility = View.VISIBLE
            } catch (e: Exception) {
                mBinding.linearAdsUpcoming.visibility = View.GONE
                Log.d(TAG, "setSmallBannerAdvertiseView: ${e.message}")
            }

        } else {
            mBinding.linearAdsUpcoming.visibility = View.GONE
        }

    }

    /*   private fun setSmallBannerAdvertiseView() {
           ShowLogToast.showLog("Test isAds Visible and path", "$isAdsVisible-$bannerAdsFilePath")
           if (isAdsVisible && !TextUtils.isEmpty(bannerAdsFilePath)) {
               try {
                   val bitmap = BitmapFactory.decodeFile(bannerAdsFilePath)
                   val mDrawableAdsBigBanner: Drawable = BitmapDrawable(mContext!!.resources, bitmap)
                   mBinding.imgSmallBannerUpcoming.setImageDrawable(mDrawableAdsBigBanner)
                   *//*Glide.with(requireContext())
                    .load(bannerAdsFilePath)
                    .centerCrop()
                    .into(mBinding.imgSmallBannerUpcoming)*//*
            } catch (e: Exception) {
                Log.d(TAG, "error: ${e.message}")
//                ShowLogToast.showLog("Upcoming Fragment", e.localizedMessage)
            }
            mBinding.linearAdsUpcoming.visibility = View.VISIBLE
        } else {
            mBinding.linearAdsUpcoming.visibility = View.GONE
        }
    }
*/
    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.linearAdsUpcoming -> {
                ReusedMethod.gotoAdsContact(mContext!!)
            }
        }
    }

    private fun setImage(imgPath: String) {
        try {
            val f = File(imgPath)

            val b = BitmapFactory.decodeStream(FileInputStream(f))
            mBinding.imgSmallBannerUpcoming.setImageBitmap(b)
            Log.d(Constants.TAG, "setImage: DOne")
            mBinding.imgSmallBannerUpcoming.visibility = View.VISIBLE
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            Log.d(Constants.TAG, "setImage: $e")
            mBinding.imgSmallBannerUpcoming.visibility = View.GONE
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
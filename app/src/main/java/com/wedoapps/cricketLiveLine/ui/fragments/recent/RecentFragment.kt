package com.wedoapps.cricketLiveLine.ui.fragments.recent

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
import com.wedoapps.cricketLiveLine.databinding.FragmentRecentBinding
import com.wedoapps.cricketLiveLine.model.HomeMatch
import com.wedoapps.cricketLiveLine.ui.CricketGuruViewModel
import com.wedoapps.cricketLiveLine.ui.MainActivity
import com.wedoapps.cricketLiveLine.ui.fragments.recent.adapter.RecentCardAdapter
import com.wedoapps.cricketLiveLine.ui.fragments.singleMatch.ViewPagerActivity
import com.wedoapps.cricketLiveLine.utils.Constants
import com.wedoapps.cricketLiveLine.utils.Constants.TAG
import com.wedoapps.cricketLiveLine.utils.Constants.bannerAdsFilePath
import com.wedoapps.cricketLiveLine.utils.PreferenceManager
import com.wedoapps.cricketLiveLine.utils.ReusedMethod
import com.wedoapps.cricketLiveLine.utils.ShowLogToast
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException


class RecentFragment : Fragment(R.layout.fragment_recent),
    View.OnClickListener, HomeCardAdapter.SetOnClick {

    private lateinit var mBinding: FragmentRecentBinding
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
        mBinding = FragmentRecentBinding.bind(view)

        preference = PreferenceManager(requireContext())
        viewModel = (activity as MainActivity).viewModel

        cardAdapter = HomeCardAdapter(this)
        mBinding.linearAdsRecent.setOnClickListener(this)
        mBinding.recyclerViewRecent.apply {
            setHasFixedSize(true)
            adapter = cardAdapter
        }

        viewModel.getAllRecentMatch().observe(requireActivity()) {
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

            ShowLogToast.showLog("Test Recent Array Size", it.size.toString())
        }
   /*     bannerAdsFilePath = preference?.getSmallBannerURI().toString()
        Constants.isAdsVisible = preference!!.getAdsVisible()

        setSmallBannerAdvertiseView()*/
    }

    private fun setRecyclerViewVisibility(b: Boolean) {
        if (b) {
            mBinding.txtNoMatchRecent.visibility = View.GONE
            mBinding.recyclerViewRecent.visibility = View.VISIBLE
        } else {
            mBinding.recyclerViewRecent.visibility = View.GONE
            mBinding.txtNoMatchRecent.visibility = View.VISIBLE
        }
    }

    override fun onClick(match: HomeMatch) {
        val intentRecentFragment = Intent(requireActivity(), ViewPagerActivity::class.java)
        intentRecentFragment.putExtra("match", match)
        startActivity(intentRecentFragment)

    }

    private fun setImage(imgPath: String) {
        try {
            val f = File(imgPath)

            val b = BitmapFactory.decodeStream(FileInputStream(f))
            mBinding.imgSmallBannerRecent.setImageBitmap(b)
            Log.d(TAG, "setImage: DOne")
            mBinding.imgSmallBannerRecent.visibility = View.VISIBLE
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            Log.d(TAG, "setImage: $e")
            mBinding.imgSmallBannerRecent.visibility = View.GONE
        }
    }

    private fun setSmallBannerAdvertiseView() {
        if (Constants.isAdsVisible && !TextUtils.isEmpty(bannerAdsFilePath)) {

            try {
                val bitmap: Bitmap = BitmapFactory.decodeFile(bannerAdsFilePath)!!
                val mDrawableAdsBigBanner = BitmapDrawable(resources, bitmap)
                mBinding.imgSmallBannerRecent.setImageDrawable(mDrawableAdsBigBanner)
                mBinding.linearAdsRecent.visibility = View.VISIBLE
            } catch (e: Exception) {
                mBinding.linearAdsRecent.visibility = View.GONE
                Log.d(TAG, "setSmallBannerAdvertiseView: ${e.message}")
            }

        } else {
            mBinding.linearAdsRecent.visibility = View.GONE
        }

    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.linearAdsRecent -> {
                ReusedMethod.gotoAdsContact(mContext!!)
            }
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
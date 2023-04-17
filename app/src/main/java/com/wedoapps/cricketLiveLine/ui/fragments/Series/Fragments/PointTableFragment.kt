package com.wedoapps.cricketLiveLine.ui.fragments.Series.Fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.seriespanel.model.PointTableModel
import com.seriespanel.model.SeriesModel
import com.wedoapps.cricketLiveLine.R
import com.wedoapps.cricketLiveLine.adapter.PointsTableRecyclerAdapter
import com.wedoapps.cricketLiveLine.databinding.FragmentPointTableBinding
import com.wedoapps.cricketLiveLine.utils.Constants

class PointTableFragment: Fragment(R.layout.fragment_point_table) {

    private lateinit var binding: FragmentPointTableBinding
    private lateinit var pointsAdapter: PointsTableRecyclerAdapter
    private var seriesModel: SeriesModel? = SeriesModel()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPointTableBinding.bind(view)

        pointsAdapter = PointsTableRecyclerAdapter()
        seriesModel = arguments?.getParcelable(Constants.SERIES_DATA)

        val pointsList = arrayListOf<PointTableModel?>()
        seriesModel?.pointTbl?.forEach { it ->
            pointsList.add(it.value)
        }

        if(pointsList.isEmpty()) {
            binding.rvPt.visibility = View.GONE
            binding.tvNoData.visibility = View.VISIBLE
        } else {
            binding.tvNoData.visibility = View.GONE
            binding.rvPt.visibility = View.VISIBLE
            pointsList.sortByDescending { it?.matchPTS }
            pointsList.add(0, null)
            pointsAdapter.differ.submitList(pointsList)

            binding.rvPt.apply {
                setHasFixedSize(false)
                adapter = pointsAdapter
            }
        }

        setSmallBannerAdvertiseView()

    }

    fun newInstance(data: SeriesModel?): PointTableFragment {
        val fragment = PointTableFragment()
        val args = Bundle()
        args.putParcelable(Constants.SERIES_DATA, data)
        fragment.arguments = args
        return fragment
    }

    private fun setSmallBannerAdvertiseView() {
        if (Constants.isAdsVisible && !TextUtils.isEmpty(Constants.bannerAdsFilePath)) {

            try {
                val bitmap: Bitmap = BitmapFactory.decodeFile(Constants.bannerAdsFilePath)!!
                val mDrawableAdsBigBanner = BitmapDrawable(resources, bitmap)
                binding.imgSmallBannerLive.setImageDrawable(mDrawableAdsBigBanner)
                binding.linearAdsLive.visibility = View.VISIBLE
            } catch (e: Exception) {
                binding.linearAdsLive.visibility = View.GONE
                Log.d(Constants.TAG, "setSmallBannerAdvertiseView: ${e.message}")
            }

        } else {
            binding.linearAdsLive.visibility = View.GONE
        }

    }


}
package com.wedoapps.cricketLiveLine.ui.fragments.Series.Fragments

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.wedoapps.cricketLiveLine.model.Series.MatchModel
import com.seriespanel.model.SeriesModel
import com.wedoapps.cricketLiveLine.R
import com.wedoapps.cricketLiveLine.adapter.FixtureAdapter
import com.wedoapps.cricketLiveLine.databinding.FragmentFixturesBinding
import com.wedoapps.cricketLiveLine.ui.CricketGuruViewModel
import com.wedoapps.cricketLiveLine.ui.fragments.Series.SeriesDetailsActivity
import com.wedoapps.cricketLiveLine.utils.Constants
import com.wedoapps.cricketLiveLine.utils.Constants.SERIES_DATA
import com.wedoapps.cricketLiveLine.utils.Constants.TAG
import com.wedoapps.cricketLiveLine.utils.LinearLayoutManagerWrapper
import com.wedoapps.cricketLiveLine.utils.PreferenceManager
class FixturesFragment : Fragment(R.layout.fragment_fixtures), FixtureAdapter.SetOnClick {

    private lateinit var binding: FragmentFixturesBinding
    private var fixtureAdapter : FixtureAdapter? = null
    private var seriesModel: SeriesModel? = SeriesModel()
//    val matchList = ArrayList<MatchModel?>()
    private var mContext: Context? = null
    private var preference: PreferenceManager? = null
    private lateinit var viewModel: CricketGuruViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        preference = PreferenceManager(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFixturesBinding.bind(view)

        viewModel = (activity as SeriesDetailsActivity).viewModel

        seriesModel = arguments?.getParcelable(SERIES_DATA)

        Log.d(TAG, "ID: ${seriesModel?.seriesId}")

        viewModel.getFixtures(seriesModel?.seriesId.toString()).observe(requireActivity()) { data ->
            Log.d(TAG, "onViewCreatedF: $data")
            if (data.isNullOrEmpty()) {
                binding.rvFixtures.visibility = View.GONE
                binding.tvNoData.visibility = View.VISIBLE
            } else {
                Log.d(TAG, "MatchListD: $data")
                Log.d(TAG, "MatchList: ${data.size}")
                binding.tvNoData.visibility = View.GONE
                binding.rvFixtures.visibility = View.VISIBLE
//                data.sortedByDescending { it?.matchDate }.sortedBy { it?.matchTime }
//            fixtureAdapter.differ.submitList(matchList.sortedBy { it?.matchDate })
//            fixtureAdapter.differ.submitList(matchList)
                fixtureAdapter = FixtureAdapter(this, data, requireContext())
                val layout = LinearLayoutManagerWrapper(requireContext())
                binding.rvFixtures.apply {
                    layoutManager = layout
                    setHasFixedSize(true)
                    adapter = fixtureAdapter
                }
            }
        }

        Constants.bannerAdsFilePath = preference?.getSmallBannerURI().toString()
        Constants.isAdsVisible = preference!!.getAdsVisible()

        setSmallBannerAdvertiseView()
    }

    fun newInstance(data: SeriesModel?): FixturesFragment {
        val fragment = FixturesFragment()
        val args = Bundle()
        args.putParcelable(SERIES_DATA, data)
        fragment.arguments = args
        return fragment
    }

    override fun onClick(match: MatchModel) {
    /*    val homeMatch = HomeMatch()
        homeMatch.id = match.seriesId
        homeMatch.MatchDetail = match.matchDate.toString()
        homeMatch.CurrentDate = "${match.matchType} - ${match.matchTime}"
        homeMatch.MatchStatus = match.matchStatus
        homeMatch.Team2 = match.team2FullName
        homeMatch.Team1 = match.team1FullName
        homeMatch.CodeTeam1 = match.team1Code
        homeMatch.CodeTeam2 = match.team2Code
        homeMatch.MatchResult = match.matchResult
        val intentHomeFragment = Intent(requireActivity(), ViewPagerActivity::class.java)
        intentHomeFragment.putExtra("match", homeMatch)
        startActivity(intentHomeFragment)*/
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

    override fun onDestroy() {
        super.onDestroy()
//        fixtureAdapter!!.onDetachedFromRecyclerView(binding.rvFixtures)
    }
    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        setSmallBannerAdvertiseView()
//        fixtureAdapter?.notifyDataSetChanged()
    }
}
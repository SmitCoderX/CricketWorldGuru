package com.wedoapps.cricketLiveLine.ui.fragments.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.PagerSnapHelper
import com.wedoapps.cricketLiveLine.model.Series.SeriesModel
import com.wedoapps.cricketLiveLine.R
import com.wedoapps.cricketLiveLine.adapter.HomeCardAdapter
import com.wedoapps.cricketLiveLine.adapter.TrendingSeriesAdapter
import com.wedoapps.cricketLiveLine.databinding.FragmentHomeBinding
import com.wedoapps.cricketLiveLine.model.HomeMatch
import com.wedoapps.cricketLiveLine.ui.CricketGuruViewModel
import com.wedoapps.cricketLiveLine.ui.MainActivity
import com.wedoapps.cricketLiveLine.ui.fragments.Series.SeriesDetailsActivity
import com.wedoapps.cricketLiveLine.ui.fragments.singleMatch.ViewPagerActivity
import com.wedoapps.cricketLiveLine.utils.Constants.SERIES_DATA
import com.wedoapps.cricketLiveLine.utils.Constants.TAG
import com.wedoapps.cricketLiveLine.utils.PreferenceManager
import com.wedoapps.cricketLiveLine.utils.ReusedMethod


class HomeFragment : Fragment(R.layout.fragment_home), HomeCardAdapter.SetOnClick,
    View.OnClickListener, TrendingSeriesAdapter.OnSeriesClick {

    private lateinit var mBinding: FragmentHomeBinding
    private lateinit var cardAdapter: HomeCardAdapter
    private lateinit var seriesAdapter: TrendingSeriesAdapter
    private lateinit var viewModel: CricketGuruViewModel
    private var team1 = ArrayList<String>()
    private var team2 = ArrayList<String>()
    private var mContext: Context? = null
    private var preference: PreferenceManager? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        preference = PreferenceManager(mContext!!)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = FragmentHomeBinding.bind(view)

        mBinding.linearAdsHome.setOnClickListener(this)

        viewModel = (activity as MainActivity).viewModel

        cardAdapter = HomeCardAdapter(this)
        seriesAdapter = TrendingSeriesAdapter(this)
        viewModel.getAllMatch().observe(requireActivity()) {

            cardAdapter.differ.submitList(it)
            it.forEach { data ->
                team1.add(data.team1.toString())
                team2.add(data.team2.toString())
            }
        }

        viewModel.getSeries().observe(requireActivity()) {
            if(it.isNullOrEmpty()) {
                mBinding.tvSeriesTitle.visibility = View.GONE
                mBinding.rvSeries.visibility = View.GONE
                mBinding.rvIndicator.visibility = View.GONE
            } else {
                mBinding.tvSeriesTitle.visibility = View.VISIBLE
                mBinding.rvSeries.visibility = View.VISIBLE

                Log.d(TAG, "SeriesList: $it")
                seriesAdapter.differ.submitList(it)

                mBinding.rvSeries.apply {
                    setHasFixedSize(true)
                    adapter = seriesAdapter
                }
                mBinding.rvIndicator.visibility = View.VISIBLE
            }
        }


        mBinding.rvHome.apply {
            setHasFixedSize(true)
            adapter = cardAdapter
        }

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(mBinding.rvHome)
        mBinding.rvIndicator.attachToRecyclerView(mBinding.rvHome)
    }

    override fun onClick(match: HomeMatch) {
        val intentHomeFragment = Intent(requireActivity(), ViewPagerActivity::class.java)
        intentHomeFragment.putExtra("match", match)
        startActivity(intentHomeFragment)

    }


    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.linearAdsHome -> {
                ReusedMethod.gotoAdsContact(mContext!!)
            }
        }
    }

    override fun onSeriesItemClick(seriesModel: SeriesModel) {
        val intent = Intent(requireActivity(), SeriesDetailsActivity::class.java)
        intent.putExtra(SERIES_DATA, seriesModel)
        startActivity(intent)

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        cardAdapter.notifyDataSetChanged()
    }
}
package com.wedoapps.cricketLiveLine.ui.fragments.Series

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.seriespanel.model.SeriesModel
import com.wedoapps.cricketLiveLine.BuildConfig
import com.wedoapps.cricketLiveLine.R
import com.wedoapps.cricketLiveLine.adapter.TrendingSeriesAdapter
import com.wedoapps.cricketLiveLine.databinding.ActivityAllSeriesBinding
import com.wedoapps.cricketLiveLine.db.CricketGuruDatabase
import com.wedoapps.cricketLiveLine.repository.CricketGuruRepository
import com.wedoapps.cricketLiveLine.ui.CricketGuruViewModel
import com.wedoapps.cricketLiveLine.ui.MainActivity
import com.wedoapps.cricketLiveLine.utils.Constants
import com.wedoapps.cricketLiveLine.utils.ReusedMethod
import com.wedoapps.cricketLiveLine.utils.ViewModelProviderFactory
import cricket.t20.api.RetrofitService

class AllSeriesActivity : AppCompatActivity(), TrendingSeriesAdapter.OnSeriesClick {

    private lateinit var binding: ActivityAllSeriesBinding
    private lateinit var seriesAdapter: TrendingSeriesAdapter
    private lateinit var viewModel: CricketGuruViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllSeriesBinding.inflate(layoutInflater)
        ReusedMethod.setStatusBarGradiant(this)
        setContentView(binding.root)


        val retrofitService = RetrofitService.getInstance()
        val repository = CricketGuruRepository(application, CricketGuruDatabase(this), retrofitService)
        val viewModelProvider = ViewModelProviderFactory(application, repository)
        viewModel = ViewModelProvider(this, viewModelProvider)[CricketGuruViewModel::class.java]

        binding.toolbar.txtToolbarTitle.text = "Series"
        binding.toolbar.backBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.toolbar.shareBtn.setOnClickListener {
            shareApp()
        }

        seriesAdapter = TrendingSeriesAdapter(this)
        
        viewModel.getAllSeries().observe(this) {
            if(it.isNullOrEmpty()) {
                binding.tvNoData.visibility = View.VISIBLE
                binding.rvAllSeries.visibility = View.GONE
            } else {
                binding.rvAllSeries.visibility = View.VISIBLE
                binding.tvNoData.visibility = View.GONE
                Log.d(Constants.TAG, "SeriesList: $it")
                seriesAdapter.differ.submitList(it)

                binding.rvAllSeries.apply {
                    setHasFixedSize(true)
                    adapter = seriesAdapter
                }
            }
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

    override fun onSeriesItemClick(seriesModel: SeriesModel) {
        val intent = Intent(this, SeriesDetailsActivity::class.java)
        intent.putExtra(Constants.SERIES_DATA, seriesModel)
        startActivity(intent)
    }
}
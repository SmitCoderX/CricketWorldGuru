package com.wedoapps.cricketLiveLine.ui.fragments.Series

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.seriespanel.model.SeriesModel
import com.wedoapps.cricketLiveLine.BuildConfig
import com.wedoapps.cricketLiveLine.R
import com.wedoapps.cricketLiveLine.adapter.ViewPagerAdapter
import com.wedoapps.cricketLiveLine.databinding.ActivitySeriesDetailsBinding
import com.wedoapps.cricketLiveLine.db.CricketGuruDatabase
import com.wedoapps.cricketLiveLine.repository.CricketGuruRepository
import com.wedoapps.cricketLiveLine.ui.CricketGuruViewModel
import com.wedoapps.cricketLiveLine.ui.MainActivity
import com.wedoapps.cricketLiveLine.ui.fragments.Series.Fragments.FixturesFragment
import com.wedoapps.cricketLiveLine.ui.fragments.Series.Fragments.PointTableFragment
import com.wedoapps.cricketLiveLine.utils.Constants
import com.wedoapps.cricketLiveLine.utils.ReusedMethod
import com.wedoapps.cricketLiveLine.utils.ViewModelProviderFactory
import cricket.t20.api.RetrofitService

class SeriesDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySeriesDetailsBinding
    private var seriesModel: SeriesModel? = SeriesModel()
    lateinit var viewModel: CricketGuruViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeriesDetailsBinding.inflate(layoutInflater)
        ReusedMethod.setStatusBarGradiant(this)
        setContentView(binding.root)

        val retrofitService = RetrofitService.getInstance()
        val repository = CricketGuruRepository(application, CricketGuruDatabase(this),retrofitService)
        val viewModelProvider = ViewModelProviderFactory(application, repository)
        viewModel = ViewModelProvider(this, viewModelProvider)[CricketGuruViewModel::class.java]

        seriesModel = intent.getParcelableExtra(Constants.SERIES_DATA)

        binding.toolbar.txtToolbarTitle.text = seriesModel?.seriesName?.trim()
        binding.toolbar.backBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.toolbar.shareBtn.setOnClickListener {
            shareApp()
        }

        val fragments = arrayListOf(
            FixturesFragment().newInstance(seriesModel),
            PointTableFragment().newInstance(seriesModel)
        )

        val adapter = ViewPagerAdapter(
            fragments,
            supportFragmentManager,
            lifecycle
        )

        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.fixtures)
                1 -> tab.text = getString(R.string.point_table)
            }
        }.attach()
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


}
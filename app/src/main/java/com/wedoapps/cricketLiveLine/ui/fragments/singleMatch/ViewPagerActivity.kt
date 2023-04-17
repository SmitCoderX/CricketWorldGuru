package com.wedoapps.cricketLiveLine.ui.fragments.singleMatch

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.wedoapps.cricketLiveLine.BuildConfig
import com.wedoapps.cricketLiveLine.R
import com.wedoapps.cricketLiveLine.adapter.ViewPagerAdapter
import com.wedoapps.cricketLiveLine.databinding.ActivityViewPagerBinding
import com.wedoapps.cricketLiveLine.db.CricketGuruDatabase
import com.wedoapps.cricketLiveLine.model.HomeMatch
import com.wedoapps.cricketLiveLine.repository.CricketGuruRepository
import com.wedoapps.cricketLiveLine.ui.CricketGuruViewModel
import com.wedoapps.cricketLiveLine.ui.MainActivity
import com.wedoapps.cricketLiveLine.ui.fragments.singleMatch.pagerFragments.*
import com.wedoapps.cricketLiveLine.utils.ReusedMethod
import com.wedoapps.cricketLiveLine.utils.ViewModelProviderFactory
import cricket.t20.api.RetrofitService

class ViewPagerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewPagerBinding
    lateinit var viewModel: CricketGuruViewModel

    private lateinit var matchModel: HomeMatch

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewPagerBinding.inflate(layoutInflater)
        ReusedMethod.setStatusBarGradiant(this)
        setContentView(binding.root)

        val intent: Intent = intent
        matchModel = intent.getParcelableExtra("match")!!

        val toolbar = binding.toolbar.toolbarTop
        binding.toolbar.txtToolbarTitle.text =
            "${if (matchModel.codeTeam1.isNullOrEmpty()) matchModel.team1?.trim() else matchModel.codeTeam1!!.trim()} VS ${if (matchModel.codeTeam2.isNullOrEmpty()) matchModel.team2?.trim() else matchModel.codeTeam2!!.trim()}"
        binding.toolbar.backBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.toolbar.shareBtn.setOnClickListener {
            shareApp()
        }



        val retrofitService = RetrofitService.getInstance()
        val repository = CricketGuruRepository(application, CricketGuruDatabase(this),retrofitService)
        val viewModelProvider = ViewModelProviderFactory(application, repository)
        viewModel = ViewModelProvider(this, viewModelProvider)[CricketGuruViewModel::class.java]




        val fragmentList = arrayListOf(
            LiveLineFragment().newInstance(matchModel),
            ScorecardFragment().newInstance(matchModel),
//            BetFragment().newInstance(matchModel),
            SessionFragment().newInstance(matchModel),
            InfoFragment().newInstance(matchModel)
        )

        val adapter = ViewPagerAdapter(
            fragmentList,
            supportFragmentManager,
            lifecycle
        )

        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.live_line)
                1 -> tab.text = getString(R.string.scorecard)
//                2 -> tab.text = getString(R.string.bet)
                2 -> tab.text = getString(R.string.session)
                3 -> tab.text = getString(R.string.info)
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
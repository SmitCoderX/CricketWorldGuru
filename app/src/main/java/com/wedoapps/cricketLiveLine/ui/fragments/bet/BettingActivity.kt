package com.wedoapps.cricketLiveLine.ui.fragments.bet

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import com.wedoapps.cricketLiveLine.R
import com.wedoapps.cricketLiveLine.adapter.ViewPagerAdapter
import com.wedoapps.cricketLiveLine.databinding.ActivityBettingBinding
import com.wedoapps.cricketLiveLine.db.CricketGuruDatabase
import com.wedoapps.cricketLiveLine.repository.CricketGuruRepository
import com.wedoapps.cricketLiveLine.ui.CricketGuruViewModel
import com.wedoapps.cricketLiveLine.ui.bottomSheets.DeclareAlertDialogFragment
import com.wedoapps.cricketLiveLine.ui.fragments.bet.match.ActiveMatchFragment
import com.wedoapps.cricketLiveLine.ui.fragments.bet.party.SummaryFragment
import com.wedoapps.cricketLiveLine.ui.fragments.bet.session.MainSessionFragment
import com.wedoapps.cricketLiveLine.utils.Constants.ID
import com.wedoapps.cricketLiveLine.utils.ViewModelProviderFactory
import cricket.t20.api.RetrofitService


class BettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBettingBinding
    lateinit var viewModel: CricketGuruViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbar.toolbarTop
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val intent = intent
        val id = intent.getStringExtra(ID)!!


        val fragmentList = arrayListOf(
            ActiveMatchFragment().newInstance(id),
            MainSessionFragment().newInstance(id),
            SummaryFragment()
        )

        val adapter = ViewPagerAdapter(
            fragmentList,
            supportFragmentManager,
            lifecycle
        )

        binding.viewPagerBetting.adapter = adapter

        binding.toolbar.txtDeclare.setOnClickListener {
            val declareDialog = DeclareAlertDialogFragment()
            val bundle = Bundle()
            bundle.putString(ID, id)
            declareDialog.arguments = bundle
//            declareDialog.setTargetFragment(declareDialog, 1)
            declareDialog.show(supportFragmentManager, declareDialog.tag)
        }


        TabLayoutMediator(binding.tabLayoutBetting, binding.viewPagerBetting) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.ActiveMatch)
                1 -> tab.text = getString(R.string.CurrentSession)
                2 -> tab.text = getString(R.string.party_info)
            }
        }.attach()

        val retrofitService = RetrofitService.getInstance()
        val repository = CricketGuruRepository(application, CricketGuruDatabase(this),retrofitService)
        val viewModelProvider = ViewModelProviderFactory(application, repository)
        viewModel = ViewModelProvider(this, viewModelProvider)[CricketGuruViewModel::class.java]

    }

    override fun onResume() {
        super.onResume()

        binding.tabLayoutBetting.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> binding.toolbar.txtDeclare.visibility = View.VISIBLE
                    1 -> binding.toolbar.txtDeclare.visibility = View.GONE
                    2 -> binding.toolbar.txtDeclare.visibility = View.GONE
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

}
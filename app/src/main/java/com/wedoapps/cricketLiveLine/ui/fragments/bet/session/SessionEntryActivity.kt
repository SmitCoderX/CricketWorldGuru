package com.wedoapps.cricketLiveLine.ui.fragments.bet.session

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.wedoapps.cricketLiveLine.adapter.ViewPagerAdapter
import com.wedoapps.cricketLiveLine.db.CricketGuruDatabase
import com.wedoapps.cricketLiveLine.R
import com.wedoapps.cricketLiveLine.repository.CricketGuruRepository
import com.wedoapps.cricketLiveLine.ui.CricketGuruViewModel
import com.wedoapps.cricketLiveLine.utils.Constants.ID
import com.wedoapps.cricketLiveLine.utils.Constants.SESSION_ID
import com.wedoapps.cricketLiveLine.utils.Constants.SESSION_NAME
import com.wedoapps.cricketLiveLine.utils.ViewModelProviderFactory
import com.wedoapps.cricketLiveLine.databinding.FragmentCurrentSessionBinding
import com.wedoapps.cricketLiveLine.ui.DeclareSessionActivity
import cricket.t20.api.RetrofitService

class SessionEntryActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: FragmentCurrentSessionBinding
    lateinit var viewModel: CricketGuruViewModel

    var id:String =""
    var sessionID:Int =0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentCurrentSessionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val policy: StrictMode.ThreadPolicy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val sessionName = intent.getStringExtra(SESSION_NAME)!!

        id = intent.getStringExtra(ID)!!
        sessionID = intent.getIntExtra(SESSION_ID, 0)

        val toolbar = binding.toolbar.toolbarTop
        binding.toolbar.tvTitle.text = "$sessionName Overs"
        setSupportActionBar(toolbar)
        binding.toolbar.txtDeclare.setOnClickListener {
//            Toast.makeText(this, "Declare Result", Toast.LENGTH_SHORT).show()
            Log.d("Test Session",sessionID.toString())
            val intentDeclareSession = Intent(this,DeclareSessionActivity::class.java)
            intentDeclareSession.putExtra("id",id)
            intentDeclareSession.putExtra("sessionID",sessionID)
            intentDeclareSession.putExtra("sessionModel",sessionID)
            startActivity(intentDeclareSession)
        }

        val fragmentList = arrayListOf(
            SessionEntryFragment().newInstance(id, sessionID.toString()),
            CheckGraphFragment().newInstance(id, sessionID.toString())
        )

        val adapter = ViewPagerAdapter(
            fragmentList,
            supportFragmentManager,
            lifecycle
        )

        binding.viewPagerSession.adapter = adapter

        TabLayoutMediator(binding.tabLayoutSession, binding.viewPagerSession) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.SessionEntry)
                1 -> tab.text = getString(R.string.CheckGraph)
            }
        }.attach()

        val retrofitService = RetrofitService.getInstance()
        val repository = CricketGuruRepository(application, CricketGuruDatabase(this),retrofitService)
        val viewModelProvider = ViewModelProviderFactory(application, repository)
        viewModel = ViewModelProvider(this, viewModelProvider)[CricketGuruViewModel::class.java]

    }

    override fun onClick(v: View?) {

        when(v!!.id){
            R.id.txtDeclare->{
                val intentDeclareSession = Intent(this,DeclareSessionActivity::class.java)
                intentDeclareSession.putExtra("id",id)
                intentDeclareSession.putExtra("sessionID",sessionID)
                startActivity(intentDeclareSession)
            }
        }
    }
}
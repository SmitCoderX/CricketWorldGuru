package com.wedoapps.cricketLiveLine.ui.fragments.singleMatch.pagerFragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.wedoapps.cricketLiveLine.R
import com.wedoapps.cricketLiveLine.adapter.SessionAdapter
import com.wedoapps.cricketLiveLine.databinding.FragmentSessionBinding
import com.wedoapps.cricketLiveLine.model.HomeMatch
import com.wedoapps.cricketLiveLine.ui.CricketGuruViewModel
import com.wedoapps.cricketLiveLine.ui.fragments.singleMatch.ViewPagerActivity

class SessionFragment : Fragment(R.layout.fragment_session) {

    private lateinit var binding: FragmentSessionBinding
    private lateinit var viewModel: CricketGuruViewModel
    private lateinit var id: String
    private var matchModel: HomeMatch? = null

    fun newInstance(matchModel: HomeMatch): SessionFragment {
        val myFragment = SessionFragment()
        val args = Bundle()
        args.putParcelable("match", matchModel)
        myFragment.arguments = args
        return myFragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSessionBinding.bind(view)

        matchModel = arguments?.getParcelable("match")
        id = matchModel!!.id.toString()

        viewModel = (activity as ViewPagerActivity).viewModel

        val sessionAdapter1 = SessionAdapter()
        val sessionAdapter2 = SessionAdapter()

        viewModel.getSpecificIdDetail(id).observe(requireActivity()) {

            if (it?.sessionHistoryInfo1.isNullOrEmpty() && it?.sessionHistoryInfo2.isNullOrEmpty()) {
                binding.sessionNested.visibility = View.GONE
                binding.tvNoHistory.visibility = View.VISIBLE
            } else if (it?.sessionHistoryInfo1.isNullOrEmpty()) {
                binding.apply {
                    tvNoHistory.visibility = View.GONE
                    sessionNested.visibility = View.VISIBLE
                    rvSession1.visibility = View.GONE
                    tvSession1.visibility = View.GONE
                    tvSession2.text = it?.team2
                    sessionAdapter2.differ.submitList(it?.sessionHistoryInfo2)
                }
            } else if (it?.sessionHistoryInfo2.isNullOrEmpty()) {
                binding.apply {
                    tvNoHistory.visibility = View.GONE
                    sessionNested.visibility = View.VISIBLE
                    rvSession2.visibility = View.GONE
                    tvSession1.text = it?.team1
                    tvSession2.visibility = View.GONE
                    sessionAdapter1.differ.submitList(it?.sessionHistoryInfo1)
                }
            } else {
                binding.apply {
                    tvNoHistory.visibility = View.GONE
                    sessionNested.visibility = View.VISIBLE
                    tvSession1.text = it?.team1
                    tvSession2.text = it?.team2
                    sessionAdapter1.differ.submitList(it?.sessionHistoryInfo1)
                    sessionAdapter2.differ.submitList(it?.sessionHistoryInfo2)
                }
            }
        }

        binding.rvSession1.apply {
            setHasFixedSize(true)
            adapter = sessionAdapter1
        }

        binding.rvSession2.apply {
            setHasFixedSize(true)
            adapter = sessionAdapter2
        }

    }




}
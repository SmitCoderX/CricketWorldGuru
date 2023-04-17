package com.wedoapps.cricketLiveLine.ui.fragments.bet.match

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.wedoapps.cricketLiveLine.adapter.MatchDataAdapter
import com.wedoapps.cricketLiveLine.model.matchBet.MatchBet
import com.wedoapps.cricketLiveLine.model.matchBet.MatchData
import com.wedoapps.cricketLiveLine.R
import com.wedoapps.cricketLiveLine.ui.bottomSheets.MatchBottomSheetFragment
import com.wedoapps.cricketLiveLine.utils.Constants.ID
import com.wedoapps.cricketLiveLine.utils.Constants.PID
import com.wedoapps.cricketLiveLine.utils.Constants.TAG
import com.wedoapps.cricketLiveLine.databinding.FragmentActiveMatchBinding
import com.wedoapps.cricketLiveLine.ui.CricketGuruViewModel
import com.wedoapps.cricketLiveLine.ui.fragments.bet.BettingActivity


class ActiveMatchFragment : Fragment(R.layout.fragment_active_match),
    MatchDataAdapter.SetOn, MatchBottomSheetFragment.OnMatchBetListener {

    private lateinit var binding: FragmentActiveMatchBinding
    private lateinit var viewModel: CricketGuruViewModel
    private lateinit var matchAdapter: MatchDataAdapter
    private lateinit var id: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentActiveMatchBinding.bind(view)

        id = arguments?.getString(ID).toString()
        viewModel = (activity as BettingActivity).viewModel

        matchAdapter = MatchDataAdapter(this)
        viewModel.getSpecificIdDetail(id).observe(requireActivity()) {
            binding.apply {
                tvTeam1.text = it?.team1
                tvTeam2.text = it?.team2
            }
        }

        viewModel.getAllMatchBet(id).observe(requireActivity()) {
            if (it.isNullOrEmpty()) {
                binding.tvNoActiveMatch.visibility = View.VISIBLE
                binding.rvActiveMatch.visibility = View.GONE
            } else {
                val matchBetArrayList = arrayListOf<MatchBet>()
                val matchDataArrayList = arrayListOf<MatchData>()
                val partyIDList = hashSetOf<String>()
                matchBetArrayList.addAll(it)
                binding.tvNoActiveMatch.visibility = View.GONE
                binding.rvActiveMatch.visibility = View.VISIBLE
                for (i in matchBetArrayList.indices) {
                    val matchBet = matchBetArrayList[i]
                    partyIDList.add(matchBet.playerName.toString())
                }
                val arrSet = ArrayList(partyIDList)
                for (i in arrSet.indices) {
                    val setValue = arrSet[i]
                    val matchData = MatchData()
                    for (j in 0 until matchBetArrayList.size) {
                        val matchBet = matchBetArrayList[j]
                        if (setValue == matchBet.playerName.toString()) {
                            matchData.matchId = matchBet.matchID
                            matchData.playerName = matchBet.playerName
                            matchData.matchBet.add(matchBet)
                        }
                    }
                    matchDataArrayList.add(matchData)
                    Log.d(TAG, "OnMatchEntryFragment: $matchDataArrayList")
                }
                matchAdapter.differ.submitList(matchDataArrayList)
            }
        }



        binding.rvActiveMatch.apply {
            setHasFixedSize(true)
            adapter = matchAdapter
        }



        binding.fabAddMatch.setOnClickListener {
            val matchSheet = MatchBottomSheetFragment()
            val bundle = Bundle()
            bundle.putString(ID, id)
            bundle.putInt("requestCode",1)
            matchSheet.arguments = bundle
            matchSheet.setTargetFragment(this, 1)
            matchSheet.show(parentFragmentManager, matchSheet.tag)
//            parentFragmentManager.putFragment(bundle,"matchSheet",matchSheet)
        }
    }


    override fun onResume() {
        super.onResume()
        viewModel.getAllMatchBet(id).observe(requireActivity()) {
            var temp = 0
            var temp1 = 0
            if (it.isNullOrEmpty()) {
                binding.tvTeam1Total.text = temp.toString()
                binding.tvTeam2Total.text = temp1.toString()
            } else {
                it.forEach { data1 ->
                    temp += data1.team1Value!!
                    temp1 += data1.team2Value!!
                    binding.tvTeam1Total.text = temp.toString()
                    binding.tvTeam2Total.text = temp1.toString()
                }
            }
        }
    }

    override fun onSheetClose() {
        viewModel.getAllMatchBet(id).observe(requireActivity()) {
            var temp = 0
            var temp1 = 0
            if (it.isNullOrEmpty()) {
                binding.tvTeam1Total.text = temp.toString()
                binding.tvTeam2Total.text = temp1.toString()
            } else {
                it.forEach { data1 ->
                    temp += data1.team1Value!!
                    temp1 += data1.team2Value!!
                    binding.tvTeam1Total.text = temp.toString()
                    binding.tvTeam2Total.text = temp1.toString()
                }
            }
        }
    }

    fun newInstance(myString: String?): ActiveMatchFragment {
        val myFragment = ActiveMatchFragment()
        val args = Bundle()
        args.putString(ID, myString)
        myFragment.arguments = args
        return myFragment
    }

    override fun onDeleteMatchBet(matchBet: MatchBet) {
        viewModel.deleteMatchBet(matchBet)
    }

    override fun onEditMatchBet(matchBet: MatchBet) {
        val matchSheet = MatchBottomSheetFragment()
        val bundle = Bundle()
        bundle.putParcelable(PID, matchBet)
        bundle.putInt("requestCode",1)
        matchSheet.arguments = bundle
        matchSheet.setTargetFragment(this, 1)
        matchSheet.show(parentFragmentManager, matchSheet.tag)
//        parentFragmentManager.putFragment(bundle,"matchSheet",matchSheet)
    }

}
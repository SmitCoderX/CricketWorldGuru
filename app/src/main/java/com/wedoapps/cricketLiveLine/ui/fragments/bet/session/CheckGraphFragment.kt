package com.wedoapps.cricketLiveLine.ui.fragments.bet.session

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.wedoapps.cricketLiveLine.adapter.CheckGraphAdapter
import com.wedoapps.cricketLiveLine.model.sessionBet.SessionBet
import com.wedoapps.cricketLiveLine.model.sessionBet.SessionBookShowModel
import com.wedoapps.cricketLiveLine.R
import com.wedoapps.cricketLiveLine.ui.CricketGuruViewModel
import com.wedoapps.cricketLiveLine.utils.Constants.ID
import com.wedoapps.cricketLiveLine.utils.Constants.SESSION_ID
import com.wedoapps.cricketLiveLine.utils.Constants.TAG
import com.wedoapps.cricketLiveLine.databinding.FragmentCheckGraphBinding

class CheckGraphFragment : Fragment(R.layout.fragment_check_graph) {

    private lateinit var binding: FragmentCheckGraphBinding
    lateinit var viewModel: CricketGuruViewModel
    private lateinit var id: String
    private lateinit var sessionID: String
    private lateinit var checkGraphAdapter: CheckGraphAdapter
    private val sessionList = arrayListOf<SessionBet>()
    private var minMaxArrayList = arrayListOf<Int>()
    private val sessionBookShowModelArrayList = arrayListOf<SessionBookShowModel>()
    private var sessionBet = SessionBet()

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCheckGraphBinding.bind(view)

        id = arguments?.getString(ID).toString()
        sessionID = arguments?.getString(SESSION_ID).toString()

        viewModel = (activity as SessionEntryActivity).viewModel

        checkGraphAdapter = CheckGraphAdapter(requireContext())

        viewModel.getAllSessionsList(sessionID).observe(requireActivity(), {
            if (it.isNullOrEmpty()) {
                binding.tvNoCG.visibility = View.VISIBLE
                binding.rvCheckGraph.visibility = View.GONE
            } else {
                sessionList.addAll(it)
                binding.tvNoCG.visibility = View.GONE
                binding.rvCheckGraph.visibility = View.VISIBLE
                minMaxArrayList = viewModel.minMax(sessionID)
                Log.d(TAG, "mmlist: $minMaxArrayList")
                for (i in sessionList.indices) {
                    sessionBet = sessionList[i]
                }
                calculateArray()
                checkGraphAdapter.differ.submitList(sessionBookShowModelArrayList)
            }
        })

        checkGraphAdapter.notifyDataSetChanged()

        binding.rvCheckGraph.apply {
            setHasFixedSize(true)
            adapter = checkGraphAdapter
        }

    }

    private fun calculateArray() {
        sessionBookShowModelArrayList.clear()
        var maxRun = minMaxArrayList[0]
        var minRun = minMaxArrayList[1]

        maxRun += 4
        minRun -= 4

        val length = maxRun - minRun + 1
        for (i in 0 until length) {
            val bookShowModel = SessionBookShowModel()
            bookShowModel.run = minRun + i
            if (bookShowModel.run == sessionBet.actualScore) {
                bookShowModel.isLastRecord = true
            }
            bookShowModel.amount = calculateAmount(bookShowModel.run!!)
            sessionBookShowModelArrayList.add(bookShowModel)
        }
//        checkGraphAdapter.differ.submitList(sessionBookShowModelArrayList)
    }

    private fun calculateAmount(runScore: Int): Double {
        var cal_Amount = 0.0
        if (sessionList.isNotEmpty()) {
            for (i in sessionList.indices) {
                val sessionBet = sessionList[i]

                cal_Amount = if (sessionBet.YorN == 1) {
                    if (runScore >= sessionBet.actualScore!!) {
                        cal_Amount + sessionBet.rate!! * sessionBet.amount!! + sessionBet.amount!! * sessionBet.commission!! / 100
                    } else {
                        cal_Amount + sessionBet.amount!! * sessionBet.commission!! / 100 - sessionBet.amount!!
                    }
                } else {
                    if (runScore >= sessionBet.actualScore!!) {
                        cal_Amount + sessionBet.amount!! * sessionBet.commission!! / 100 - sessionBet.amount!! * sessionBet.rate!!
                    } else {
                        cal_Amount + sessionBet.amount!! * sessionBet.commission!! / 100 + sessionBet.amount!!
                    }
                }
            }
        }
        Log.d(TAG, "Final Calculated Amount: $cal_Amount")
        return cal_Amount
    }

    fun newInstance(myString: String?, sessionID: String?): CheckGraphFragment {
        val myFragment = CheckGraphFragment()
        val args = Bundle()
        args.putString(ID, myString)
        args.putString(SESSION_ID, sessionID)
        myFragment.arguments = args
        return myFragment
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        checkGraphAdapter.notifyDataSetChanged()
    }
}
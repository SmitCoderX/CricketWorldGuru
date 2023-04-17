package com.wedoapps.cricketLiveLine.ui.fragments.singleMatch.pagerFragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.wedoapps.cricketLiveLine.adapter.BowlerListAdapter
import com.wedoapps.cricketLiveLine.adapter.ScoreCardBattingAdapter
import com.wedoapps.cricketLiveLine.adapter.WicketListAdapter
import com.wedoapps.cricketLiveLine.model.AllWicketList
import com.wedoapps.cricketLiveLine.model.BowlerList
import com.wedoapps.cricketLiveLine.model.PlayerScore
import com.wedoapps.cricketLiveLine.R
import com.wedoapps.cricketLiveLine.ui.CricketGuruViewModel
import com.wedoapps.cricketLiveLine.ui.fragments.singleMatch.ViewPagerActivity
import com.wedoapps.cricketLiveLine.utils.Constants.TAG
import com.wedoapps.cricketLiveLine.databinding.FragmentScorecardBinding
import com.wedoapps.cricketLiveLine.model.HomeMatch

class ScorecardFragment : Fragment(R.layout.fragment_scorecard) {

    private lateinit var binding: FragmentScorecardBinding
    private lateinit var viewModel: CricketGuruViewModel
    private lateinit var scoreCardBattingAdapter: ScoreCardBattingAdapter
    private lateinit var scoreCardBattingAdapter2: ScoreCardBattingAdapter
    private lateinit var bowlerListAdapter: BowlerListAdapter
    private lateinit var bowlerListAdapter2: BowlerListAdapter
    private lateinit var wicketListAdapter: WicketListAdapter
    private lateinit var wicketListAdapter2: WicketListAdapter
    private lateinit var id: String
    private val score1 = mutableListOf<PlayerScore>()
    private val score2 = mutableListOf<PlayerScore>()
    private val bowler1 = mutableListOf<BowlerList>()
    private val bowler2 = mutableListOf<BowlerList>()
    private val wicket1 = mutableListOf<AllWicketList>()
    private val wicket2 = mutableListOf<AllWicketList>()
    private var matchModel:HomeMatch?= null

    fun newInstance(matchModel: HomeMatch): ScorecardFragment {
        val myFragment = ScorecardFragment()
        val args = Bundle()
        args.putParcelable("match",matchModel)
        myFragment.arguments = args
        return myFragment
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentScorecardBinding.bind(view)

        matchModel = arguments?.getParcelable("match")
        id = matchModel!!.id.toString()

        viewModel = (activity as ViewPagerActivity).viewModel

        scoreCardBattingAdapter = ScoreCardBattingAdapter()
        scoreCardBattingAdapter2 = ScoreCardBattingAdapter()
        bowlerListAdapter = BowlerListAdapter()
        bowlerListAdapter2 = BowlerListAdapter()
        wicketListAdapter = WicketListAdapter()
        wicketListAdapter2 = WicketListAdapter()

        binding.linearLayout1.setOnClickListener {
            if (binding.expandable1.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(binding.linearLayout1, AutoTransition())
                binding.expandable1.visibility = View.VISIBLE
                binding.ivScoreTeam1Arrow.rotationX = 180F
            } else {
                TransitionManager.beginDelayedTransition(binding.linearLayout1, AutoTransition())
                binding.expandable1.visibility = View.GONE
                binding.ivScoreTeam1Arrow.rotationX = 0F
            }
        }

        binding.linearLayout2.setOnClickListener {
            if (binding.expandable2.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(binding.linearLayout2, AutoTransition())
                binding.expandable2.visibility = View.VISIBLE
                binding.ivScoreTeam2Arrow.rotationX = 180F
            } else {
                TransitionManager.beginDelayedTransition(binding.linearLayout2, AutoTransition())
                binding.expandable2.visibility = View.GONE
                binding.ivScoreTeam2Arrow.rotationX = 0F
            }
        }

        viewModel.apply {

            getAllTeam1(id).observe(requireActivity()) {
                binding.tvScoreTeam1Score.text = it?.score + " ( ${it?.over} )"
            }

            getAllTeam2(id).observe(requireActivity()) {
                binding.tvScoreTeam2Score.text = it?.score + " ( ${it?.over} )"
            }

            getSpecificIdDetail(id).observe(requireActivity()) {
                Log.d(TAG, "onViewCreated Specific Data: $it")
                binding.tvScoreTeam1Name.text = if(it?.codeTeam1.isNullOrEmpty()) it?.team1?.trim() else it?.codeTeam1!!.trim()
                binding.tvScoreTeam2Name.text = if(it?.codeTeam2.isNullOrEmpty()) it?.team2?.trim() else it?.codeTeam2!!.trim()

            }

            getTeam1Extras(id).observe(requireActivity()) {
                Log.d(TAG, "extras 1: $it")
                if (it.trim().isNullOrEmpty()) {
                    binding.tv1Extras.visibility = View.GONE
                } else {
                    binding.tv1Extras.visibility = View.VISIBLE
                    binding.tv1Extras.text = "Extras: $it"
                }
            }

            getTeam2Extras(id).observe(requireActivity()) {
                Log.d(TAG, "extras 2: $it")
                if (it.trim().isNullOrEmpty()) {
                    binding.tv2Extras.visibility = View.GONE
                } else {
                    binding.tv2Extras.visibility = View.VISIBLE
                    binding.tv2Extras.text = "Extras: $it"
                }
            }

            getScoreDetails1(id).observe(requireActivity()) {
                Log.d(TAG, "onViewCreated ScoreCardTeam1:$id $it")

                score1.addAll(it)

                if (it == null) {
                    binding.team1ScoreHeader.root.visibility = View.GONE
                    binding.rv1Team1.visibility = View.GONE
                } else {
                    binding.rv1Team1.visibility = View.VISIBLE
                    binding.team1ScoreHeader.root.visibility = View.VISIBLE
                    scoreCardBattingAdapter.differ.submitList(it)
                }


            }

            getScoreDetails2(id).observe(requireActivity()) {
                Log.d(TAG, "onViewCreated ScoreCardTeam2:$id $it")

                score2.addAll(it)

                if (it == null) {
                    binding.team2ScoreHeader.root.visibility = View.GONE
                    binding.rv1Team2.visibility = View.GONE
                } else {
                    binding.rv1Team2.visibility = View.VISIBLE
                    binding.team2ScoreHeader.root.visibility = View.VISIBLE
                    scoreCardBattingAdapter2.differ.submitList(it)
                }


            }

            getBowlerList2(id).observe(requireActivity()) {
                Log.d(TAG, "onViewCreated Bowler List 2: ${it.team2List}")

                bowler2.add(it)

                if (it == null) {
                    binding.rvBowlingTeam2.visibility = View.GONE
                    binding.team2BowlerHeader.root.visibility = View.GONE
                    binding.textView10.visibility = View.GONE
                    binding.ivBall2Img.visibility = View.GONE
                } else {
                    binding.ivBall2Img.visibility = View.VISIBLE
                    binding.textView10.visibility = View.VISIBLE
                    binding.team2BowlerHeader.root.visibility = View.VISIBLE
                    binding.rvBowlingTeam2.visibility = View.VISIBLE
                    bowlerListAdapter2.differ.submitList(it.team2List)
                }

            }

            getBowlerList1(id).observe(requireActivity()) {
                Log.d(TAG, "onViewCreated Bowler List 1: ${it.team1List}")

                bowler1.add(it)

                if (it == null) {
                    binding.rvBowlingTeam1.visibility = View.GONE
                    binding.team1BowlerHeader.root.visibility = View.GONE
                    binding.ivBowlingImg.visibility = View.GONE
                    binding.textView9.visibility = View.GONE
                } else {
                    binding.textView9.visibility = View.VISIBLE
                    binding.ivBowlingImg.visibility = View.VISIBLE
                    binding.rvBowlingTeam1.visibility = View.VISIBLE
                    binding.team1BowlerHeader.root.visibility = View.VISIBLE
                    bowlerListAdapter.differ.submitList(it.team1List)
                }
            }

            getWicketList1(id).observe(requireActivity()) {
                Log.d(TAG, "onViewCreated WIcket List 1: ${it.wicketList1}")

                wicket1.add(it)

                if (it == null) {
                    binding.rvWicketTeam1.visibility = View.GONE
                    binding.team1FowHeader.root.visibility = View.GONE
                    binding.ivWicketImg.visibility = View.GONE
                    binding.textView11.visibility = View.GONE
                } else {
                    binding.textView11.visibility = View.VISIBLE
                    binding.ivWicketImg.visibility = View.VISIBLE
                    binding.team1FowHeader.root.visibility = View.VISIBLE
                    binding.rvWicketTeam1.visibility = View.VISIBLE
                    wicketListAdapter.differ.submitList(it.wicketList1)
                }
            }

            getWicketList2(id).observe(requireActivity()) {
                Log.d(TAG, "onViewCreated Wicket List 2: ${it.wicketList2}")

                wicket2.add(it)

                if (it == null) {
                    binding.rvWicketTeam2.visibility = View.GONE
                    binding.team2FowHeader.root.visibility = View.GONE
                    binding.ivWicketImg2.visibility = View.GONE
                    binding.textView12.visibility = View.GONE
                } else {
                    binding.textView12.visibility = View.VISIBLE
                    binding.ivWicketImg2.visibility = View.VISIBLE
                    binding.team2FowHeader.root.visibility = View.VISIBLE
                    binding.rvWicketTeam2.visibility = View.VISIBLE
                    wicketListAdapter2.differ.submitList(it.wicketList2)
                }
            }
        }

        binding.rv1Team1.apply {
            setHasFixedSize(true)
            adapter = scoreCardBattingAdapter
        }

        binding.rv1Team2.apply {
            setHasFixedSize(true)
            adapter = scoreCardBattingAdapter2
        }

        binding.rvBowlingTeam1.apply {
            setHasFixedSize(true)
            adapter = bowlerListAdapter
        }

        binding.rvBowlingTeam2.apply {
            setHasFixedSize(true)
            adapter = bowlerListAdapter2
        }

        binding.rvWicketTeam1.apply {
            setHasFixedSize(true)
            adapter = wicketListAdapter
        }

        binding.rvWicketTeam2.apply {
            setHasFixedSize(true)
            adapter = wicketListAdapter2
        }

    }


    override fun onResume() {
        super.onResume()

        if (score1.isNullOrEmpty() && score2.isNullOrEmpty() && bowler1.isNullOrEmpty() && bowler2.isNullOrEmpty() && wicket1.isNullOrEmpty() && wicket2.isNullOrEmpty()) {
            binding.tvNoData.visibility = View.VISIBLE
            binding.linearLayout1.visibility = View.GONE
            binding.linearLayout2.visibility = View.GONE
        } else {
            binding.tvNoData.visibility = View.GONE
            binding.linearLayout1.visibility = View.VISIBLE
            binding.linearLayout2.visibility = View.VISIBLE
        }
    }


}
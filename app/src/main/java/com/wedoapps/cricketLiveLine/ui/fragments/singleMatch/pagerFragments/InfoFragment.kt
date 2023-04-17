package com.wedoapps.cricketLiveLine.ui.fragments.singleMatch.pagerFragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.wedoapps.cricketLiveLine.R
import com.wedoapps.cricketLiveLine.adapter.PlayerListAdapter
import com.wedoapps.cricketLiveLine.databinding.FragmentInfoBinding
import com.wedoapps.cricketLiveLine.model.HomeMatch
import com.wedoapps.cricketLiveLine.ui.CricketGuruViewModel
import com.wedoapps.cricketLiveLine.ui.fragments.singleMatch.ViewPagerActivity
import com.wedoapps.cricketLiveLine.utils.Constants.TAG

class InfoFragment : Fragment(R.layout.fragment_info) {

    private lateinit var binding: FragmentInfoBinding
    private lateinit var viewModel: CricketGuruViewModel
    private var isExpanded = false
    private lateinit var id: String
    private var matchModel: HomeMatch? = null

    fun newInstance(matchModel: HomeMatch): InfoFragment {
        val myFragment = InfoFragment()
        val args = Bundle()
        args.putParcelable("match", matchModel)
        myFragment.arguments = args
        return myFragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentInfoBinding.bind(view)

        matchModel = arguments?.getParcelable("match")
        id = matchModel!!.id.toString()

        viewModel = (activity as ViewPagerActivity).viewModel

        viewModel.getInfo(id).observe(requireActivity()) {
            Log.d(TAG, "onViewCreated GetINFO: $it ")

            if (it?.teamSquade?.team1.isNullOrBlank() && it?.teamSquade?.team2.isNullOrBlank()) {
                binding.apply {
                    tvNoSquade.visibility = View.VISIBLE
                    infoTeam1.visibility = View.INVISIBLE
                    infoTeam2.visibility = View.INVISIBLE
                }
            } else {
                binding.apply {
                    tvNoSquade.visibility = View.GONE
                    infoTeam1.visibility = View.VISIBLE
                    infoTeam2.visibility = View.VISIBLE
                }
                val filterTeam1: List<String> =
                    it?.teamSquade!!.team1!!.split(",").map { playerName ->
                        playerName.trim()
                    }

                val filterTeam2: List<String> =
                    it.teamSquade!!.team2!!.split(",").map { playerName ->
                        playerName.trim()
                    }

                val playerAdapter = PlayerListAdapter(filterTeam1)
                val playerAdapter2 = PlayerListAdapter(filterTeam2)

                binding.rvPlayerList1.apply {
                    setHasFixedSize(true)
                    adapter = playerAdapter
                }

                binding.rvPlayerList2.apply {
                    setHasFixedSize(true)
                    adapter = playerAdapter2
                }
            }

            binding.apply {

                tvInfoTeam1Name.text = it?.team1
                tvInfoTeam2Name.text = it?.team2


                infoTeam1.setOnClickListener {
                    if (!isExpanded) {
                        ivInfoTeam1Arrow.rotationX = 180F
                        rvPlayerList1.visibility = View.VISIBLE
                        isExpanded = true
                    } else {
                        ivInfoTeam1Arrow.rotationX = 0F
                        rvPlayerList1.visibility = View.GONE
                        isExpanded = false
                    }
                }

                infoTeam2.setOnClickListener {
                    if (!isExpanded) {
                        ivInfoTeam2Arrow.rotationX = 180F
                        rvPlayerList2.visibility = View.VISIBLE
                        isExpanded = true
                    } else {
                        ivInfoTeam2Arrow.rotationX = 0F
                        rvPlayerList2.visibility = View.GONE
                        isExpanded = false
                    }
                }

                tvSeries.text = it?.series
                tvMatch.text = it?.noOfMatch
                tvDate.text = it?.currentDate
                tvVenue.text = it?.venue
                tvToss.text = it?.tossInfo
                tvFirstInning.text = it?.avg1stInnings
                tvSecondInning.text = it?.avg2ndInnings
                tvHighestTotal.text = it?.highestTotal
                tvLowestTotal.text = it?.lowestTotal
                tvHighestChased.text = it?.highestChased
                tvLowestDefending.text = it?.lowestDefended
                tvHead.text = it?.head2Head
                tvTeamFirstNameShort.text = it?.teamForm?.nameTeam1 ?: ""
                tvTeamTwoNameShort.text = it?.teamForm?.nameTeam2 ?: ""
                tvUmpire.text = it?.umpire
                tvThirdUmpire.text = it?.thirdUmpire
                tvRefree.text = it?.referee
                tvMom.text = it?.mom

                val one = binding.form1Team1
                val two = binding.form1Team2
                val three = binding.form1Team3
                val four = binding.form1Team4
                val five = binding.form1Team5
                val one2 = binding.form2Team1
                val two2 = binding.form2Team2
                val three2 = binding.form2Team3
                val four2 = binding.form2Team4
                val five2 = binding.form2Team5

                val textViews = ArrayList<TextView>(listOf(one, two, three, four, five))
                val textViews2 = ArrayList<TextView>(listOf(one2, two2, three2, four2, five2))

                if (it?.teamForm?.formTeam1.isNullOrEmpty() && it?.teamForm?.formTeam2.isNullOrEmpty()) {
                    binding.apply {
                        tvTeamFormTitle.visibility = View.GONE
                        teamForm1.visibility = View.GONE
                        teamForm2.visibility = View.GONE
                    }
                } else {
                    tvTeamFormTitle.visibility = View.VISIBLE
                    teamForm1.visibility = View.VISIBLE
                    teamForm2.visibility = View.VISIBLE
                    try {
                        val form1 = it?.teamForm?.formTeam1?.split(",")
                        for (i in 0..4) {
                                textViews[i].text = if (form1?.get(i).isNullOrEmpty()) "" else form1?.get(i)?.trim()
                        }
                    } catch (e: Exception) {
                        Log.d(TAG, "out")
                    }

                    try {
                        val form2 = it?.teamForm?.formTeam2?.split(",")
                        for (i in 0..4) {
                            textViews2[i].text = if (form2?.get(i).isNullOrEmpty()) "" else form2?.get(i)?.trim()
                        }
                    } catch (e: Exception) {
                        Log.d(TAG, "out")
                    }
                }
            }
        }
    }

}

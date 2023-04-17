package com.wedoapps.cricketLiveLine.ui.fragments.singleMatch.pagerFragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.wedoapps.cricketLiveLine.R
import com.wedoapps.cricketLiveLine.databinding.FragmentBetBinding
import com.wedoapps.cricketLiveLine.model.HomeMatch
import com.wedoapps.cricketLiveLine.ui.CricketGuruViewModel
import com.wedoapps.cricketLiveLine.ui.fragments.bet.BettingActivity
import com.wedoapps.cricketLiveLine.ui.fragments.singleMatch.ViewPagerActivity
import com.wedoapps.cricketLiveLine.utils.Constants.ID

class BetFragment : Fragment(R.layout.fragment_bet) {

    private lateinit var binding: FragmentBetBinding
    private lateinit var viewModel: CricketGuruViewModel
    private var isSelected = false
    private lateinit var id: String

    private var matchModel:HomeMatch?= null

    fun newInstance(matchModel: HomeMatch): BetFragment {
        val myFragment = BetFragment()
        val args = Bundle()
        args.putParcelable("match",matchModel)
        myFragment.arguments = args
        return myFragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBetBinding.bind(view)

        matchModel = arguments?.getParcelable("match")
        id = matchModel!!.id.toString()

        viewModel = (activity as ViewPagerActivity).viewModel

        /*viewModel.getInfo(id).observe(requireActivity()) {
            binding.apply {
                etTeam1.setText(String.format(it.Team1.toString()))
                etTeam2.setText(String.format(it.Team2.toString()))
            }
        }


        binding.apply {
            tvTest.setOnClickListener {
                isSelected = true
                tvOdi.background = null
                tvOdi.setTextColor(Color.BLACK)
                tvTest.setTextColor(Color.WHITE)
                tvTest.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.reverse_select_bg)
            }

            binding.apply {
                tvOdi.setOnClickListener {
                    isSelected = false
                    tvTest.background = null
                    tvTest.setTextColor(Color.BLACK)
                    tvOdi.setTextColor(Color.WHITE)
                    tvOdi.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.select_bg)
                }
            }
        }*/

      /*  binding.btnPlay.setOnClickListener {
            if (isSelected) {
                Toast.makeText(requireContext(), "Test", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Odi / T-20", Toast.LENGTH_SHORT).show()
            }
            val intent = Intent(requireContext(), BettingActivity::class.java)
            intent.putExtra(ID, id)
            startActivity(intent)
        }*/

    }



}
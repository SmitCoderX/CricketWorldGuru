package com.wedoapps.cricketLiveLine.ui.bottomSheets

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.wedoapps.cricketLiveLine.R
import com.wedoapps.cricketLiveLine.ui.CricketGuruViewModel
import com.wedoapps.cricketLiveLine.ui.fragments.bet.BettingActivity
import com.wedoapps.cricketLiveLine.utils.Constants.ID
import com.wedoapps.cricketLiveLine.databinding.DeclareAlertDialogFragmentBinding

class DeclareAlertDialogFragment : DialogFragment() {

    private lateinit var binding: DeclareAlertDialogFragmentBinding
    private lateinit var id: String
    private var isSelected = false
    private lateinit var viewModel: CricketGuruViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.declare_alert_dialog_fragment, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DeclareAlertDialogFragmentBinding.bind(view)

        id = arguments?.getString(ID).toString()
        viewModel = (activity as BettingActivity).viewModel



        viewModel.getAllMatchBet(id).observe(requireActivity(), {
            var temp = 0
            var temp1 = 0
            if (it.isNullOrEmpty()) {
                dismiss()
            } else {
                it.forEach { data1 ->
                    for (i in data1.allTeams!!.indices) {
                        binding.team1Name.text = data1.allTeams!![0]
                        binding.team2Name.text = data1.allTeams!![1]
                    }
                    temp += data1.team1Value!!
                    temp1 += data1.team2Value!!

                    binding.tvSelfAmount.text = "SELF = $temp"

                    binding.apply {
                        team1Name.setOnClickListener {
                            isSelected = false
                            team1Selected()
                            binding.tvSelfAmount.text = "SELF = $temp"
                        }

                        team2Name.setOnClickListener {
                            isSelected = true
                            team2Selected()
                            binding.tvSelfAmount.text = "SELF = $temp1"
                        }
                    }
                }
            }
        })

        binding.ivCancelDeclare.setOnClickListener {
            dismiss()
        }

    }

    private fun team1Selected() {
        binding.apply {
            team2Name.background = null
            team2Name.setTextColor(Color.BLACK)
            team1Name.setTextColor(Color.WHITE)
            team1Name.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.select_bg)
        }
    }

    private fun team2Selected() {
        binding.apply {
            team1Name.background = null
            team1Name.setTextColor(Color.BLACK)
            team2Name.setTextColor(Color.WHITE)
            team2Name.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.reverse_select_bg)
        }
    }


    override fun onResume() {
        super.onResume()
        val params: ViewGroup.LayoutParams = dialog?.window!!.attributes
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

}
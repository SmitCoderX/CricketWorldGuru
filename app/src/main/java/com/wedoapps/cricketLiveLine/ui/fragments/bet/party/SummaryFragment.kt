package com.wedoapps.cricketLiveLine.ui.fragments.bet.party

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.wedoapps.cricketLiveLine.R
import com.wedoapps.cricketLiveLine.databinding.FragmentPartyInfoBinding

class SummaryFragment : Fragment(R.layout.fragment_party_info) {

    private lateinit var binding: FragmentPartyInfoBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPartyInfoBinding.bind(view)


        /*  binding.fabAddMatch.setOnClickListener {
              val partyInfo = PartyBottomFragment()
              partyInfo.show(childFragmentManager, partyInfo.tag)
          }*/
    }

}
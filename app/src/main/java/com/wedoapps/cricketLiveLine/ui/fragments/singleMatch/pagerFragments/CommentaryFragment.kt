package com.wedoapps.cricketLiveLine.ui.fragments.singleMatch.pagerFragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.wedoapps.cricketLiveLine.R
import com.wedoapps.cricketLiveLine.utils.Constants
import com.wedoapps.cricketLiveLine.utils.Constants.ID
import com.wedoapps.cricketLiveLine.databinding.FragmentCommentaryBinding

class CommentaryFragment : Fragment(R.layout.fragment_commentary) {

    private lateinit var binding: FragmentCommentaryBinding
    private lateinit var id: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCommentaryBinding.bind(view)

        id = arguments?.getString(ID).toString()
    }


    fun newInstance(myString: String?): CommentaryFragment {
        val myFragment = CommentaryFragment()
        val args = Bundle()
        args.putString(Constants.ID, myString)
        myFragment.arguments = args
        return myFragment
    }

}
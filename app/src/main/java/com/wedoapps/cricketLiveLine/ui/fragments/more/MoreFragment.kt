package com.wedoapps.cricketLiveLine.ui.fragments.more

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.wedoapps.cricketLiveLine.BuildConfig
import com.wedoapps.cricketLiveLine.R
import com.wedoapps.cricketLiveLine.adapter.MoreAdapter
import com.wedoapps.cricketLiveLine.databinding.FragmentMoreBinding
import com.wedoapps.cricketLiveLine.model.MoreData
import com.wedoapps.cricketLiveLine.utils.Constants
import com.wedoapps.cricketLiveLine.utils.PreferenceManager
import com.wedoapps.cricketLiveLine.utils.ReusedMethod

class MoreFragment : Fragment(R.layout.fragment_more), MoreAdapter.OnSetClick {

    private lateinit var binding: FragmentMoreBinding
    private var mContext: Context? = null
    private var preference: PreferenceManager? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        preference = PreferenceManager(mContext!!)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMoreBinding.bind(view)

        val item = listOf(
            MoreData(1, R.drawable.ic_star, "Rate Us"),
            MoreData(2, R.drawable.ic_share, "Share"),
            MoreData(3, R.drawable.ic_privacy, "Privacy Policy"),
            MoreData(4, R.drawable.ic_contact, "Contact Us"),
        )

        val moreAdapter = MoreAdapter(this)

        moreAdapter.differ.submitList(item)

        binding.rvMore.apply {
            setHasFixedSize(true)
            adapter = moreAdapter
        }

       /* Constants.bannerAdsFilePath = preference?.getSmallBannerURI().toString()
        Constants.isAdsVisible = preference!!.getAdsVisible()
        setSmallBannerAdvertiseView()*/
    }

    override fun onClick(data: MoreData) {
        when (data.id) {
            1 -> {
                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}".trimIndent())
                        )
                    )
                } catch (e: ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}\".trimIndent()")
                        )
                    )
                }
            }

            2 -> {
                shareApp()
            }

            3 -> {
                startActivity(Intent(requireActivity(), PrivacyPolicyActivity::class.java))
            }

            4 -> {
                ReusedMethod.gotoAdsContact(requireContext())
            }
        }
    }

    private fun setSmallBannerAdvertiseView() {
        if (Constants.isAdsVisible && !TextUtils.isEmpty(Constants.bannerAdsFilePath)) {

            try {
                val bitmap: Bitmap = BitmapFactory.decodeFile(Constants.bannerAdsFilePath)!!
                val mDrawableAdsBigBanner = BitmapDrawable(resources, bitmap)
                binding.imgSmallBannerLive.setImageDrawable(mDrawableAdsBigBanner)
                binding.linearAdsLive.visibility = View.VISIBLE
            } catch (e: Exception) {
                binding.linearAdsLive.visibility = View.GONE
                Log.d(Constants.TAG, "setSmallBannerAdvertiseView: ${e.message}")
            }

        } else {
            binding.linearAdsLive.visibility = View.GONE
        }

    }

    private fun shareApp() {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            var shareMessage = "Check out the Fastest Cricket Score App of the world - "
            shareMessage =
                "${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}".trimIndent()
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, "choose one"))
        } catch (e: Exception) {
            e.toString()
        }
    }

    override fun onResume() {
        super.onResume()
//        setSmallBannerAdvertiseView()
    }

}
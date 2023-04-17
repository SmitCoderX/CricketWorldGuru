package com.wedoapps.cricketLiveLine.ui.fragments.more

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.wedoapps.cricketLiveLine.databinding.ActivityPrivacyPolicyBinding
import com.wedoapps.cricketLiveLine.ui.MainActivity
import com.wedoapps.cricketLiveLine.utils.Constants
import com.wedoapps.cricketLiveLine.utils.ReusedMethod

class PrivacyPolicyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrivacyPolicyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyPolicyBinding.inflate(layoutInflater)
        ReusedMethod.setStatusBarGradiant(this)
        setContentView(binding.root)

        binding.toolbar.txtToolbarTitle.text = "Privacy Policy"
        binding.toolbar.backBtn.setOnClickListener {
          onBackPressed()
            finish()
        }

        binding.tvPolicy.text = HtmlCompat.fromHtml(
            "<b>Privacy Policy</b>" + "<p>Our extensive knowledge about the\n" +
                    "system and corresponding analysis\n" +
                    "enables us to provide an approximate of\n" +
                    "the match winning chances. ScoreStream\n" +
                    "by no means supports betting and we are\n" +
                    "not responsible for any unlawful action\n" +
                    "taken on the part of the user. The app is\n" +
                    "meant purely for entertainment purposes.\n" +
                    "The user is solely responsible for any\n" +
                    "illegal activates done by them.</p>" + "<b>User Information:</b>" + "<p>\n" +
                    "\n" +
                    "The information provided by the users\n" +
                    "enables us to improve our application and\n" +
                    "provide you the most user-friendly\n" +
                    "experience. All required information is\n" +
                    "service dependent and we may use the\n" +
                    "above-said user information to, maintain,\n" +
                    "protect and improve services and for\n" +
                    "developing new services\n" +
                    "\n" +
                    "\n</p>" + "<b>Information security:</b>" + "<p>\n" +
                    "All the information gathered on our App\n" +
                    "is securely stored within our controlled\n" +
                    "database. The database is stored on the\n" +
                    "servers is password protected and is\n" +
                    "strictly limited. However, as effective as\n" +
                    "our security measures are, no security\n" +
                    "system is impenetrable. We cannot\n" +
                    "guarantee the security of our database,\n" +
                    "nor can we guarantee that information\n" +
                    "provided by you will not be intercepted\n" +
                    "while being transmitted to us over the\n" +
                    "internet. We may change our privacy\n" +
                    "policy from time to time to incorporate\n" +
                    "necessary future changes.</p>", HtmlCompat.FROM_HTML_MODE_LEGACY
        )

        binding.tvPolicy.movementMethod = ScrollingMovementMethod()
        setSmallBannerAdvertiseView()
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

}
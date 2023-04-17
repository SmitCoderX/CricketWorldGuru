package com.wedoapps.cricketLiveLine.ui.fragments.singleMatch.pagerFragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import com.wedoapps.cricketLiveLine.BuildConfig
import com.wedoapps.cricketLiveLine.R
import com.wedoapps.cricketLiveLine.databinding.FragmentLiveLineBinding
import com.wedoapps.cricketLiveLine.model.HomeMatch
import com.wedoapps.cricketLiveLine.model.Score
import com.wedoapps.cricketLiveLine.ui.CricketGuruViewModel
import com.wedoapps.cricketLiveLine.ui.fragments.singleMatch.ViewPagerActivity
import com.wedoapps.cricketLiveLine.utils.Constants
import com.wedoapps.cricketLiveLine.utils.Constants.TAG
import com.wedoapps.cricketLiveLine.utils.PreferenceManager
import com.wedoapps.cricketLiveLine.utils.ReusedMethod
import com.wedoapps.cricketLiveLine.utils.ShowLogToast
import java.io.File
import java.io.FileNotFoundException
import java.util.*

class LiveLineFragment : Fragment(R.layout.fragment_live_line) {

    private lateinit var binding: FragmentLiveLineBinding
    private lateinit var viewModel: CricketGuruViewModel
    private var textToSpeech: TextToSpeech? = null
    private var isVolumeOn: Boolean = false
    private var isFirstInning: Boolean = true
    private var isMatchLive: Boolean = true
    private var team1Name: String? = ""

    //    private var scoreTeam1: String? = ""
//    private var overTeam1: String? = ""
    private var team2Name: String? = ""
    private var score1: Score = Score()
    private var score2: Score = Score()

    //    private var scoreTeam2: String? = ""
//    private var overTeam2: String? = ""
    private var ballByBallSpeech: String? = ""
    private lateinit var id: String
    private var alertDialogFile: File? = null
    private var bitmapFile: Bitmap? = null
    private var matchModel: HomeMatch? = null
    private var mContext: Context? = null
    private var preferenceManager: PreferenceManager? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        preferenceManager = PreferenceManager(mContext!!)
    }

    fun newInstance(matchModel: HomeMatch): LiveLineFragment {
        val myFragment = LiveLineFragment()
        val args = Bundle()
        args.putParcelable("match", matchModel)
        myFragment.arguments = args
        return myFragment
    }

    @SuppressLint("SetTextI18n", "NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLiveLineBinding.bind(view)

        matchModel = arguments?.getParcelable("match")
        id = matchModel!!.id.toString()

        isVolumeOn = preferenceManager!!.getVolumeOn()

        try {
            /*val imgPath = Environment.getRootDirectory()
                .toString() + "/Android/data/" + requireActivity().applicationContext.packageName + "/Files/FullAd.jpeg"*/

            val imgPath = preferenceManager?.getDialogAdURI().toString()
            Log.d(TAG, "ALERTPATH: $imgPath")
            ShowLogToast.showLog("Test AlertDialog Path", Constants.alertDialogAdsFilePath)
//            val bitmap: Bitmap = BitmapFactory.decodeFile(Constants.bannerAdsFilePath)
//            val mDrawableAdsBigBanner = BitmapDrawable(resources, bitmap)
//            alertDialogFile = File(imgPath)
//            bitmapFile = BitmapFactory.decodeStream(FileInputStream(alertDialogFile))
            Log.d(TAG, "setImage: Done")
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            Log.d(TAG, "setImage: $e")
        }

        viewModel = (activity as ViewPagerActivity).viewModel

        val card1 = binding.card1
        val card2 = binding.card2
        val card3 = binding.card3
        val card4 = binding.card4
        val card5 = binding.card5
        val card6 = binding.card6
        val card7 = binding.card7

        setTextToSpeechListener()

        Log.d(TAG, "DialogPathB: ${Constants.bannerAdsFilePath}")
        Log.d(TAG, "DialogPathB: ${Constants.alertDialogAdsFilePath}")
        Log.d(TAG, "isAdsVisibleB: ${Constants.isAdsVisible}")

//        Constants.bannerAdsFilePath = preferenceManager?.getSmallBannerURI().toString()
        Constants.isAdsVisible = preferenceManager!!.getAdsVisible()

        setSmallBannerAdvertiseView()

        Log.d(TAG, "BannerPathA: ${Constants.bannerAdsFilePath}")
        Log.d(TAG, "DialogPathA: ${Constants.alertDialogAdsFilePath}")
        Log.d(TAG, "isAdsVisibleA: ${Constants.isAdsVisible}")
        viewModel.apply {


            getSpecificIdDetail(id).observe(requireActivity(), {
                val status = it?.matchStatus
                binding.tvStatus.text = status

                team1Name =
                    if (it?.codeTeam1.isNullOrEmpty()) it?.team1?.trim() else it?.codeTeam1!!.trim()
                team2Name =
                    if (it?.codeTeam2.isNullOrEmpty()) it?.team2?.trim() else it?.codeTeam2!!.trim()
//                binding.tvTeamName.text = "${if(it.CodeTeam1.isNullOrEmpty()) it.Team1?.trim() else it.CodeTeam1.trim()} "
//                binding.tvTeamNameOpp.text = "${if(it.CodeTeam2.isNullOrEmpty()) it.Team2?.trim() else it.CodeTeam2.trim()} "

                if (status.equals("LIVE")) {
                    isMatchLive = true
                } else if (status.equals("UPCOMING")) {
                    isMatchLive = false
                } else {
                    isMatchLive = false
                }

            })



            getFirstInnings(id).observe(requireActivity(), {
                Log.d(TAG, "First Innings $it")
                val value = it.substring(1, it.length - 1)
                val keyValuePair = value.split(",")
                val map = hashMapOf<String, String>()

                keyValuePair.forEach { text ->
                    val entry = text.split("=")
                    map[entry[0].trim()] = entry[1].trim()
                }

                isFirstInning = map["IsFirstInning"].toBoolean()

                getAllTeam1(id).observe(requireActivity(), { t1 ->
                    if (t1 != null) {
                        score1 = t1
                        if (isFirstInning) {
                            binding.tvTeamName.text = team1Name
                            binding.tvPlayScore.text = "${score1.score} ( ${score1.over} )"
                        } else {
                            binding.tvTeamNameOpp.text = team1Name
                            binding.tvOppScore.text = "${score1.score} ( ${score1.over} )"
                        }
                    } else {
                        score1.score = ""
                        score1.over = ""
                    }
                })

                getAllTeam2(id).observe(requireActivity(), { t2 ->
                    if (t2 != null) {
                        score2 = t2
                        if (isFirstInning) {
                            binding.tvTeamNameOpp.text = team2Name
                            binding.tvOppScore.text = "${score2.score} ( ${score2.over} )"
                        } else {
                            binding.tvTeamName.text = team2Name
                            binding.tvPlayScore.text = "${score2.score} ( ${score2.over} )"
                        }
                    } else {
                        score2.score = ""
                        score2.over = ""
                    }
                })
            })


            getRunRate(id).observe(requireActivity(), {
                Log.d(TAG, " RunRate: $it")
                val value = it.substring(1, it.length - 1)
                val keyValuePair = value.split(",")
                val map = hashMapOf<String, String>()

                keyValuePair.forEach { text ->
                    val entry = text.split("=")
                    map[entry[0].trim()] = entry[1].trim()
                }

                card1.apply {
                    tvCrr.text = map["CRR"]
                    tvMessage.text = map["OtherInfo"]
                    tvRrr.text = map["RRR"]
                }
            })

            getBatsman1(id).observe(requireActivity(), {
                Log.d(TAG, "Batsman1: $it")
                val value = it.substring(1, it.length - 1)
                val keyValuePair = value.split(",")
                val map = hashMapOf<String, String>()

                keyValuePair.forEach { text ->
                    val entry = text.split("=")
                    map[entry[0].trim()] = entry[1].trim()
                }

                card3.apply {
                    tvFirstBatsman.text = map["BatsmanName"]
                    tvFirstRun.text = map["Run"]
                    tvFirstBalls.text = map["Ball"]
                    tvFirst4s.text = map["4s"]
                    tvFirst6s.text = map["6s"]
                    tvFirstStrikeRate.text = map["SR"]
                }

            })

            getBatsman2(id).observe(requireActivity(), {
                Log.d(TAG, "Batsman2: $it")
                val value = it.substring(1, it.length - 1)
                val keyValuePair = value.split(",")
                val map = hashMapOf<String, String>()

                keyValuePair.forEach { text ->
                    val entry = text.split("=")
                    map[entry[0].trim()] = entry[1].trim()
                }

                card3.apply {
                    tvSecondBatsman.text = map["BatsmanName"]
                    tvSecondRun.text = map["Run"] ?: ""
                    tvSecondBalls.text = map["Ball"] ?: ""
                    tvSecond4s.text = map["4s"] ?: ""
                    tvSecond6s.text = map["6s"] ?: ""
                    tvSecondStrikeRate.text = map["SR"] ?: ""
                }
            })

            getBowlerInfo(id).observe(requireActivity(), {
                Log.d(TAG, " Bowler Info: $it")
                val value = it.substring(1, it.length - 1)
                val keyValuePair = value.split(",")
                val map = hashMapOf<String, String>()

                keyValuePair.forEach { text ->
                    val entry = text.split("=")
                    map[entry[0].trim()] = entry[1].trim()
                }
                binding.card3.tvBowlerName.text = map["BowlerName"].toString()
                binding.card3.tvBowlerOver.text = map["Over"].toString()
                binding.card3.tvBowlerMaiden.text = map["Maiden"].toString()
                binding.card3.tvBowlerRun.text = map["Run"].toString()
                binding.card3.tvBowlerWicket.text = map["Wicket"].toString()
                binding.card3.tvBowlerEco.text = map["Eco"].toString()
            })

            getPartnership(id).observe(requireActivity(), {
                Log.d(TAG, " Partnership: $it")
                val value = it.substring(1, it.length - 1)
                val keyValuePair = value.split(",")
                val map = hashMapOf<String, String>()

                keyValuePair.forEach { text ->
                    val entry = text.split("=")
                    map[entry[0].trim()] = entry[1].trim()
                }
                card3.tvPartnership.text = map["Partnership"] ?: ""
            })

            getBallXRun(id).observe(requireActivity(), {
                Log.d(TAG, " BallXRun: $it")


                val value = it.substring(1, it.length - 1)
                val keyValuePair = value.split(",")
                val map = hashMapOf<String, String>()

                keyValuePair.forEach { text ->
                    val entry = text.split("=")
                    map[entry[0].trim()] = entry[1].trim()
                }

                if (map["Ball"].toString().isNullOrEmpty()) {
                    card2.root.visibility = View.GONE
                } else {
                    card2.root.visibility = View.VISIBLE
                    card2.tvSecondBall.text = map["Ball"]
                    card2.tvSecondRun.text = map["Run"]
                }
            })

            getSessionLambi(id).observe(requireActivity(), {
                Log.d(TAG, " SessionLambi: $it")


                val value = it.substring(1, it.length - 1)
                val keyValuePair = value.split(",")
                val map = hashMapOf<String, String>()

                keyValuePair.forEach { text ->
                    val entry = text.split("=")
                    map[entry[0].trim()] = entry[1].trim()
                }

                card6.apply {
                    tvSessionOver.text = map["LambiName"] + " Over Runs: "
                    tvFirstNo.text = map["Lambi1"]
                    tvFirstYes.text = map["Lambi2"]
                    tvOpn.text = map["OpenLambi"]
                    tvMin.text = map["MinLambi"]
                    tvMax.text = map["MaxLambi"]
                }
            })

            getLambiBallXRun(id).observe(requireActivity(), {
                Log.d(TAG, " LambiBallXRun: $it")


                val value = it.substring(1, it.length - 1)
                val keyValuePair = value.split(",")
                val map = hashMapOf<String, String>()

                keyValuePair.forEach { text ->
                    val entry = text.split("=")
                    map[entry[0].trim()] = entry[1].trim()
                }

                Log.d(TAG, "BallValueofLive: ${map["Ball"].toString().isNullOrEmpty()}")
                if (map["Ball"].toString().isNullOrEmpty()) {
                    card6.root.visibility = View.GONE
                } else {
                    card6.root.visibility = View.VISIBLE
                    card6.tvSecondBall.text = map["Ball"]
                    card6.tvSecondRun.text = map["Run"]
                }
            })

            getLiveMatch(id).observe(requireActivity(), {
                Log.d(TAG, " LiveMatch: $it")
            })

            getSession(id).observe(requireActivity(), {
                Log.d(TAG, " SessionFragment: $it")


                val value = it.substring(1, it.length - 1)
                val keyValuePair = value.split(",")
                val map = hashMapOf<String, String>()

                keyValuePair.forEach { text ->
                    val entry = text.split("=")
                    map[entry[0].trim()] = entry[1].trim()
                }

                card2.apply {
                    tvSessionOver.text = map["SessionName"] + " Over Runs:"
                    tvFirstNo.text = map["Rate1"]
                    tvFirstYes.text = map["Rate2"]
                    tvOpn.text = map["OpenSession"]
                    tvMin.text = map["MinSession"]
                    tvMax.text = map["MaxSession"]
                }

            })

            getLastBall(id).observe(requireActivity(), {
                Log.d(TAG, " LastBall: $it")
                val value = it.substring(1, it.length - 1)
                val keyValuePair = value.split(",")
                val map = hashMapOf<String, String>()

                keyValuePair.forEach { text ->
                    val entry = text.split("=")
                    map[entry[0].trim()] = entry[1].trim()
                }

                card4.apply {
                    tvBall1.text = map["Ball1"]
                    tvBall2.text = map["Ball2"]
                    tvBall3.text = map["Ball3"]
                    tvBall4.text = map["Ball4"]
                    tvBall5.text = map["Ball5"]
                    tvBall6.text = map["Ball6"]
                }

            })

            getBallByBall(id).observe(requireActivity(), {
                val value = it.substring(1, it.length - 1)
                val keyValuePair = value.split(",")
                val map = hashMapOf<String, String>()

                keyValuePair.forEach { text ->
                    val entry = text.split("=")
                    map[entry[0].trim()] = entry[1].trim()
                }
                binding.tvDay.text = map["BallByBall"]
                ballByBallSpeech = binding.tvDay.text.toString()

                when {
                    ballByBallSpeech!!.contentEquals("BALL") -> {
                        ballByBallSpeech = "Ball Start Ball"
                    }
                    ballByBallSpeech!!.contentEquals("0 Run") -> {
                        binding.tvDay.text = "0"
                        ballByBallSpeech = "dot ball"
                    }
                    ballByBallSpeech!!.contentEquals("1 Run") -> {
                        binding.tvDay.text = "1"
                        ballByBallSpeech = "One Run One"
                    }
                    ballByBallSpeech!!.contentEquals("2 Run") -> {
                        binding.tvDay.text = "2"
                        ballByBallSpeech = "Two Run Two"
                    }
                    ballByBallSpeech!!.contentEquals("3 Run") -> {
                        binding.tvDay.text = "3"
                        ballByBallSpeech = "Three Run"
                    }
                    ballByBallSpeech!!.contentEquals("It's 6") -> {
                        binding.tvDay.text = "6-6-6"
//                        binding.tvDay.text = "6"
                        ballByBallSpeech = " Six Six Six"
                    }
                    ballByBallSpeech!!.contentEquals("It's 4") -> {
                        binding.tvDay.text = "4-4-4"
//                        binding.tvDay.text = "4"
                        ballByBallSpeech = " Four Four Four"
                    }
                    ballByBallSpeech!!.contentEquals("WD + 1") -> {
                        binding.tvDay.text = "WD1"
                        ballByBallSpeech = "Wide Plus one"
                    }
                    ballByBallSpeech!!.contentEquals("WD + 2") -> {
                        binding.tvDay.text = "WD2"
                        ballByBallSpeech = "Wide Plus Two"
                    }
                    ballByBallSpeech!!.contentEquals("WD + 4") -> {
                        ballByBallSpeech = "Wide Plus Four"
                    }
                    ballByBallSpeech!!.contentEquals("No + 1") -> {
                        ballByBallSpeech = "No Ball Plus One"
                    }
                    ballByBallSpeech!!.contentEquals("No + 2") -> {
                        ballByBallSpeech = "No Ball Plus Two"
                    }
                    ballByBallSpeech!!.contentEquals("No + 4") -> {
                        ballByBallSpeech = "No Ball Plus Four"
                    }
                    ballByBallSpeech!!.contentEquals("No + 6") -> {
                        ballByBallSpeech = "No Ball Plus Six"
                    }
                    ballByBallSpeech!!.contentEquals("It's Wicket!!!") -> {
                        ballByBallSpeech = "It's Wicket"
                    }
                    ballByBallSpeech!!.contentEquals("1 + Wicket") -> {
                        ballByBallSpeech = "One Plus Wicket"
                    }

                    ballByBallSpeech!!.trim().contentEquals("Over Complete") -> {
                        getRunAndScoreVoice()
                    }

                    ballByBallSpeech!!.trim().contentEquals("Over") -> {
                        getRunAndScoreVoice()
                    }

                }

                if (isVolumeOn) {
                    val speechStatus = textToSpeech!!.speak(
                        ballByBallSpeech!!,
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        null
                    )
                    if (speechStatus == TextToSpeech.ERROR) {
                        Log.d(
                            "TTS",
                            "Error in converting Text to Speech!"
                        )
                    }

                }

                TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                    binding.tvDay,
                    20,
                    35,
                    20,
                    1
                )
            })

            getOtherMessage(id).observe(requireActivity(), {
                Log.d(TAG, "Other Message: $it")
                val value = it.substring(1, it.length - 1)
                val keyValuePair = value.split(",")
                val map = hashMapOf<String, String>()

                keyValuePair.forEach { text ->
                    val entry = text.split("=")
                    map[entry[0].trim()] = entry[1].trim()
                }
                card3.tvLastWicket.text = map["Message"]
            })

            getMatchRate(id).observe(requireActivity(), {
                Log.d(TAG, "match rate: $it")
                val value = it.substring(1, it.length - 1)
                val keyValuePair = value.split(",")
                val map = hashMapOf<String, String>()

                keyValuePair.forEach { text ->
                    val entry = text.split("=")
                    map[entry[0].trim()] = entry[1].trim()
                }
                card7.apply {
                    tvFavTeam.text = map["FavTeam"]
                    tvRate1.text = map["Rate1"]
                    tvRate2.text = map["Rate2"]
                }
            })
        }

        card5.root.setOnClickListener {
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

        if (isVolumeOn) {
            binding.ivVolume.visibility = View.VISIBLE
            binding.ivMute.visibility = View.GONE

        } else {
            binding.ivVolume.visibility = View.GONE
            binding.ivMute.visibility = View.VISIBLE

        }

        binding.ivVolume.setOnClickListener {
            preferenceManager!!.setVolumeOn(false)
            isVolumeOn = preferenceManager!!.getVolumeOn()
            binding.ivVolume.visibility = View.GONE
            binding.ivMute.visibility = View.VISIBLE
        }

        binding.ivMute.setOnClickListener {
            preferenceManager!!.setVolumeOn(true)
            isVolumeOn = preferenceManager!!.getVolumeOn()
            binding.ivVolume.visibility = View.VISIBLE
            binding.ivMute.visibility = View.GONE
        }
    }

    private fun setTextToSpeechListener() {
        if (isAdded && textToSpeech == null) {
            textToSpeech = TextToSpeech(requireContext()) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    val ttsLang = textToSpeech!!.setLanguage(Locale.US)
                    if (ttsLang == TextToSpeech.LANG_MISSING_DATA
                        || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED
                    ) {
                        Log.d("TTS", "The Language is not supported!")
                    } else {
                        Log.d("TTS", "Language Supported.")
                    }
                    Log.d("TTS", "Initialization success.")
                } else {
                    Log.d("TTS", "Initialization Failed!.")
                }
            }
        }
    }

    private fun getRunAndScoreVoice() {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            var ballBy = ""
            if (isMatchLive) {
                if (isFirstInning) {
                    if (!TextUtils.isEmpty(score1.score) && !TextUtils.isEmpty(score1.over)) {
                        val score = score1.score!!.split("-".toRegex()).toTypedArray()
                        val over = score1.over!!.split("\\.".toRegex()).toTypedArray()
                        ballBy =
                            score[0] + " runs for " + score[1] + " Wickets in " + over[0] + " Overs"
                    }
                } else {
                    if (!TextUtils.isEmpty(score2.score) && !TextUtils.isEmpty(score2.over)) {
                        val score = score2.score!!.split("-".toRegex()).toTypedArray()
                        val over = score2.over!!.split("\\.".toRegex()).toTypedArray()
                        ballBy =
                            score[0] + " runs for " + score[1] + " Wickets in " + over[0] + " Overs"
                    }
                }
            }
            if (isVolumeOn) {
                setTextToSpeechListener()
                val speechStatus = textToSpeech!!.speak(
                    ballBy,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    null
                )
                if (speechStatus == TextToSpeech.ERROR) {
                    Log.d("TTS", "Error in converting Text to Speech!")
                }

            }
        }, 3000)
    }

    private fun createAdDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.fragment_dialog_ad)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val adIv = dialog.findViewById<ImageView>(R.id.iv_full_ad)
        val close = dialog.findViewById<ImageView>(R.id.iv_close_ad)
        if (Constants.alertDialogAdsFilePath.isNullOrEmpty()) {
            Log.d(TAG, "createAdDialog:")
        } else {
            val bitmap: Bitmap = BitmapFactory.decodeFile(Constants.alertDialogAdsFilePath)
            val mDrawableAdsBigBanner = BitmapDrawable(resources, bitmap)
            adIv.setImageDrawable(mDrawableAdsBigBanner)
        }

        close.setOnClickListener {
            dialog.dismiss()
        }

        adIv.setOnClickListener(View.OnClickListener {
            ReusedMethod.gotoAdsContact(
                requireContext()
            )
        })
        dialog.show()
    }

    override fun onPause() {
        super.onPause()
        if (isVolumeOn) {
            if (textToSpeech != null) {
                textToSpeech!!.stop()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (textToSpeech != null) {
            textToSpeech!!.stop()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "Volume: $isVolumeOn")
        if (isVolumeOn) {
            setTextToSpeechListener()
        }

//        val preferenceManager = PreferenceManager(requireContext())
        /* if (alertDialogFile!!.exists()) {
             createAdDialog()
         }*/
        if (Constants.isFullAddsShow && Constants.isAdsVisible && Constants.bannerAdsFilePath.isNotEmpty()) {

            if (Constants.counterAdsShow % 3 == 0) {
                createAdDialog()
            }

            Constants.counterAdsShow += 1
        }


        setSmallBannerAdvertiseView()

    }

    override fun onStart() {
        super.onStart()
        if (isVolumeOn) {
            setTextToSpeechListener()
        }
    }

    /*   private fun setSmallBannerAdvertiseView() {
           if (Constants.isAdsVisible && !TextUtils.isEmpty(Constants.bannerAdsFilePath)) {
               try {
                   val bitmap = BitmapFactory.decodeFile(Constants.bannerAdsFilePath)
                   val mDrawableAdsBigBanner: Drawable = BitmapDrawable(mContext!!.resources, bitmap)
                   binding.imgSmallBannerLiveLine.setImageDrawable(mDrawableAdsBigBanner)
               } catch (e: Exception) {
                   e.printStackTrace()
                   ShowLogToast.showLog("Live Line Fragment", e.localizedMessage)
               }
               binding.linearAdsLiveLine.visibility = View.VISIBLE

           } else {
               binding.linearAdsLiveLine.visibility = View.GONE
           }
           *//*if (!Constants.isPaidVersion) {

        } else {
            mBinding.linearAdsHome.visibility = View.GONE
        }*//*
    }*/

    private fun setSmallBannerAdvertiseView() {
        if (Constants.isAdsVisible && Constants.bannerAdsFilePath.isNotEmpty()) {
            val bitmap: Bitmap = BitmapFactory.decodeFile(Constants.bannerAdsFilePath)
            val mDrawableAdsBigBanner = BitmapDrawable(resources, bitmap)
            binding.imgSmallBannerLiveLine.setImageDrawable(mDrawableAdsBigBanner)
            binding.linearAdsLiveLine.visibility = View.VISIBLE
            binding.linearAdsLiveLine.setOnClickListener {
                ReusedMethod.gotoAdsContact(mContext!!)
            }
        } else {
            binding.linearAdsLiveLine.visibility = View.GONE
        }

    }


}

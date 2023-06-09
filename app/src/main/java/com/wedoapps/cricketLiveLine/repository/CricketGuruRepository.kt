package com.wedoapps.cricketLiveLine.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.JsonObject
import com.wedoapps.cricketLiveLine.model.Series.MatchModel
import com.wedoapps.cricketLiveLine.model.Series.PointTableModel
import com.wedoapps.cricketLiveLine.model.Series.SeriesModel
import com.wedoapps.cricketLiveLine.db.CricketGuruDatabase
import com.wedoapps.cricketLiveLine.model.*
import com.wedoapps.cricketLiveLine.model.info.Info
import com.wedoapps.cricketLiveLine.model.matchBet.MatchBet
import com.wedoapps.cricketLiveLine.model.sessionBet.MainSession
import com.wedoapps.cricketLiveLine.model.sessionBet.SessionBet
import com.wedoapps.cricketLiveLine.model.sessionBet.declaresession.AccountModel
import com.wedoapps.cricketLiveLine.utils.Constants
import com.wedoapps.cricketLiveLine.utils.Constants.TAG
import com.wedoapps.cricketLiveLine.utils.PreferenceManager
import com.wedoapps.cricketLiveLine.utils.ShowLogToast
import cricket.t20.api.RetrofitService
import java.util.*
import kotlin.collections.ArrayList

class CricketGuruRepository(app: Application, private val db: CricketGuruDatabase, private val retrofitService: RetrofitService) {
    private var firestore = FirebaseFirestore.getInstance()
    private var database = FirebaseDatabase.getInstance().reference
    private var mDatabase = FirebaseDatabase.getInstance().getReference("series")
    private var mutableLangName = MutableLiveData<MutableList<HomeMatch>>()
    private var mutableUpcoming = MutableLiveData<MutableList<HomeMatch>>()
    private var mutableRecent = MutableLiveData<MutableList<HomeMatch>>()
    private var mutableData = MutableLiveData<HomeMatch?>()
    private var _team1 = MutableLiveData<Score?>()
    private var _team2 = MutableLiveData<Score?>()
    private var _teamExtras = MutableLiveData<String>()
    private var _teamExtras2 = MutableLiveData<String>()
    private var _runRate = MutableLiveData<String>()
    private var _batman1Info = MutableLiveData<String>()
    private var _batman2Info = MutableLiveData<String>()
    private var _bowlerInfo = MutableLiveData<String>()
    private var _partnershipInfo: MutableLiveData<String> = MutableLiveData<String>()
    private var _ballXrun: MutableLiveData<String> = MutableLiveData<String>()
    private var _sessionLambi: MutableLiveData<String> = MutableLiveData<String>()
    private var _lambiBallXrun: MutableLiveData<String> = MutableLiveData<String>()
    private var _liveMatch: MutableLiveData<String> = MutableLiveData<String>()
    private var _session: MutableLiveData<String> = MutableLiveData<String>()
    private var _lastball: MutableLiveData<String> = MutableLiveData<String>()
    private var _ballByBall: MutableLiveData<String> = MutableLiveData<String>()
    private var _otherMessage: MutableLiveData<String> = MutableLiveData<String>()
    private var _matchRate: MutableLiveData<String> = MutableLiveData<String>()
    private var _firstInnings: MutableLiveData<String> = MutableLiveData<String>()
    private var _matchInfo: MutableLiveData<Info?> = MutableLiveData<Info?>()
    private var _teamScore = MutableLiveData<MutableList<PlayerScore>>()
    private var _teamScore2 = MutableLiveData<MutableList<PlayerScore>>()
    private var _bowlerlist1: MutableLiveData<BowlerList> = MutableLiveData<BowlerList>()
    private var _bowlerlist2: MutableLiveData<BowlerList> = MutableLiveData<BowlerList>()
    private var _wicketList1: MutableLiveData<AllWicketList> = MutableLiveData<AllWicketList>()
    private var _wicketList2 = MutableLiveData<AllWicketList>()
    private var series = MutableLiveData<MutableList<SeriesModel?>>()
    private var fixtures = MutableLiveData<ArrayList<MatchModel?>>()
    private val firestoreRef = firestore.collection("MatchList")
    private var scoreDataModelArrayList1 = ArrayList<PlayerScore>()
    private val scoreDataModelArrayList2 = ArrayList<PlayerScore>()
    private val bowlerDataModelArrayList1 = ArrayList<Bowlers>()
    private val bowlerDataModelArrayList2 = ArrayList<Bowlers>()
    private val wicketDataModelArrayList1 = ArrayList<WicketFall>()
    private val wicketDataModelArrayList2 = ArrayList<WicketFall>()
    private var playerScore1 = ArrayList<PlayerScore>()
    private var playerScore2 = ArrayList<PlayerScore>()
    private val bowlerList = BowlerList()
    private var homeMatch = HomeMatch()
    private var wicketList = AllWicketList()
    private val seriesList = arrayListOf<SeriesModel?>()
//    private val arrayListMatchModel = arrayListOf<MatchModel?>()
    private val storageRef = FirebaseStorage.getInstance().reference
    private val prefs = PreferenceManager(app)
    private val data = hashMapOf<String, Any>()

    fun getAds() {
        firestore.collection(Constants.COLLECTION_PATH_LIVE_MATCH)
            .document(Constants.showAdMessageView)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(TAG, "Listen Failed", error)
                    return@addSnapshotListener
                }

                if (value != null) {
                    Log.d(TAG, "doWork Data: ${value.data}")
                    if (value.data?.containsKey("AdsVisible") == true) {
                        Constants.isAdsVisible = value.getBoolean("AdsVisible")!!
                        prefs.setAdsVisible(Constants.isAdsVisible)
                        Constants.isAdsVisible = prefs.getAdsVisible()
                    } else {
                        Constants.isAdsVisible = false
                        prefs.setAdsVisible(Constants.isAdsVisible)
                        Constants.isAdsVisible = prefs.getAdsVisible()
                    }

                    if (value.data?.containsKey("IsFullAdsShow") == true) {
                        Constants.isFullAddsShow = value.getBoolean("IsFullAdsShow")!!
                        prefs.setFullAdsVisible(Constants.isFullAddsShow)
                        Constants.isFullAddsShow = prefs.getFullAdsVisible()
                    }

                    if (value.data?.containsKey("FullAdsName") == true) {
                        Constants.FULL_BANNER_ADS_NAME =
                            value.getString("FullAdsName")!!
                        prefs.setFullAdName(value.getString("FullAdsName")!!)
                    }

                    if (value.data?.containsKey("BannerAdsBig") == true) {
                        Constants.BIG_BANNER_ADS_NAME =
                            value.getString("BannerAdsBig")!!
                    }

                    if (value.data?.containsKey("BannerAds") == true) {
                        Constants.SMALL_BANNER_ADS_NAME =
                            value.getString("BannerAds")!!
                        prefs.setSmallAdName(value.getString("BannerAds")!!)
                        Log.d(TAG, "doWork: VALUE GOT")
                        Log.d(TAG, "doWork:" + Constants.SMALL_BANNER_ADS_NAME)
                    } else {
                        Log.d(TAG, "doWork: NO value")
                    }

                    if (value.data?.containsKey("IsUrl") == true) {
                        Constants.isUrl = value.getBoolean("IsUrl") as Boolean
                    }

                    if (value.data?.containsKey("UrlName") == true) {
                        Constants.ADS_URL = value.data!!["UrlName"] as String?
                    }

                    if (value.data?.containsKey("Telegram") == true) {
                        Constants.teleGramChanelLink = value.data!!["Telegram"] as String
                    }

                    if (value.data?.containsKey("Phone") == true) {
                        Constants.adsMobileNumber = value.data!!["Phone"] as String
                    }

                    if (value.data?.containsKey("ContactUs") == true) {
                        Constants.ContactUs = value.data!!["ContactUs"] as String
                    }

                    if (value.data?.containsKey("Message") == true) {
                        Constants.adsMessage = value.data!!["Message"] as String
                    }
                }
            }
    }
    fun team1(id: String): MutableLiveData<Score?> {
        firestoreRef.document(id).collection(Constants.COLLECTION_PATH_LIVE_MATCH)
            .document("ScoreTeam1")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(TAG, "Listen Failed", error)
                    return@addSnapshotListener
                }

                if (value != null) {
                    //                    val allTeam1 = ArrayList<Score>()
                    val allTeam1 = value.toObject(Score::class.java)
                    if (allTeam1 != null) {
                        //                    allTeam1?.add(allTeam1)
                        _team1.value = allTeam1
                        Log.d(TAG, "team1: ${value.data}")
                    }
                } else {
                    Log.d(TAG, "No Data")
                }
            }
        return _team1
    }

    fun team2(id: String): MutableLiveData<Score?> {
        firestoreRef.document(id).collection(Constants.COLLECTION_PATH_LIVE_MATCH)
            .document("ScoreTeam2")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(TAG, "Listen Failed", error)
                    return@addSnapshotListener
                }

                if (value != null) {
                    val allTeam2 = value.toObject(Score::class.java)
                    if (allTeam2 != null) {
                        _team2.value = allTeam2
                        Log.d(TAG, "team2: ${value.data}")
                    }
                } else {
                    Log.d(TAG, "No Data")
                }
            }
        return _team2
    }


    fun getMatch(): MutableLiveData<MutableList<HomeMatch>> {


        firestore.collection(Constants.COLLECTION_PATH_MATCH_LISTING)
            .whereLessThan(Constants.MATCH_INDEX, 100)
            .orderBy(Constants.MATCH_INDEX, Query.Direction.ASCENDING)
            .limit(10)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(TAG, "Listen Failed", error)
                    return@addSnapshotListener
                }

                if (value != null) {
                    val allMatch = ArrayList<HomeMatch>()
                    val match = value.documents

                    match.forEach {
                        Log.d(TAG, "getMatch: ${it.data}")
                        homeMatch = it.toObject(HomeMatch::class.java)!!
                        Log.d(TAG, "homeMatch: $homeMatch")
                        homeMatch.id = it.id
//                        getScore(it.id)
                        allMatch.add(homeMatch)

                    }.also {
                        mutableLangName.value = allMatch
                    }
                } else {
                    Log.d(TAG, "No Data")
                }
            }
        return mutableLangName
    }

    fun getUpComingMatches(): MutableLiveData<MutableList<HomeMatch>> {

        firestore.collection(Constants.COLLECTION_PATH_MATCH_LISTING)
            .whereEqualTo(Constants.MatchStatus, Constants.STATUS_UPCOMING)
            .orderBy(Constants.MatchDate, Query.Direction.ASCENDING)
            .limit(10)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(TAG, "Listen Failed", error)
                    return@addSnapshotListener
                }

                if (value != null) {
                    val allMatch = ArrayList<HomeMatch>()
                    val match = value.documents
                    match.forEach {
                        Log.d(TAG, "getMatch: ${it.data}")
                        homeMatch = it.toObject(HomeMatch::class.java)!!
                        homeMatch.id = it.id
//                        getScore(it.id)
                        allMatch.add(homeMatch)
                    }
                    mutableUpcoming.value = allMatch

                    ShowLogToast.showLog("Test Upcoming list Repository ", allMatch.size.toString())
                } else {
                    Log.d(TAG, "No Data")
                }
            }
        return mutableUpcoming
    }


    fun getLiveMatches(): MutableLiveData<MutableList<HomeMatch>> {
        val mutableLive = MutableLiveData<MutableList<HomeMatch>>()

        firestore.collection(Constants.COLLECTION_PATH_MATCH_LISTING)
            .whereEqualTo(Constants.MatchStatus, Constants.STATUS_LIVE)
            .orderBy(Constants.MatchDate, Query.Direction.ASCENDING)
            .limit(10)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(TAG, "Listen Failed", error)
                    return@addSnapshotListener
                }

                if (value != null) {
                    val allMatch = ArrayList<HomeMatch>()
                    val match = value.documents
                    match.forEach {
                        Log.d(TAG, "getMatch: ${it.data}")
                        homeMatch = it.toObject(HomeMatch::class.java)!!
                        homeMatch.id = it.id
//                        getScore(it.id)
                        allMatch.add(homeMatch)
                    }
                    mutableLive.value = allMatch

                    ShowLogToast.showLog("Test Upcoming list Repository ", allMatch.size.toString())
                } else {
                    Log.d(TAG, "No Data")
                }
            }
        return mutableLive
    }

    fun getRecentMatches(): MutableLiveData<MutableList<HomeMatch>> {

        firestore.collection(Constants.COLLECTION_PATH_MATCH_LISTING)
            .whereEqualTo(Constants.MatchStatus, Constants.STATUS_COMPLETED)
            .orderBy(Constants.MatchDate, Query.Direction.DESCENDING)
            .limit(30)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(TAG, "Listen Failed", error)
                    return@addSnapshotListener
                }

                if (value != null) {
                    val allMatch = ArrayList<HomeMatch>()
                    val match = value.documents
                    match.forEach {
                        Log.d(TAG, "get RecentMatch: ${it.data}")
                        homeMatch = it.toObject(HomeMatch::class.java)!!
                        homeMatch.id = it.id
//                        getScore(it.id)
                        allMatch.add(homeMatch)
                    }
                    mutableRecent.value = allMatch
                } else {
                    Log.d(TAG, "No Data")
                }
            }
        return mutableRecent
    }

    fun extrasTeam1(id: String): MutableLiveData<String> {
        firestoreRef.document(id).collection("ScoreCard").document("Extra")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(TAG, "Listen Failed", error)
                    return@addSnapshotListener
                }

                if (value != null) {
                    val data = value.data
                    if (data != null) {
                        _teamExtras.value = data["Extra1"].toString()
                    }
                    Log.d(TAG, "extrasTeam1: $data")
                } else {
                    Log.d(TAG, "No Data")
                }
            }
        return _teamExtras
    }

    fun extrasTeam2(id: String): MutableLiveData<String> {
        firestoreRef.document(id).collection("ScoreCard").document("Extra")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(TAG, "Listen Failed", error)
                    return@addSnapshotListener
                }

                if (value != null) {
                    val data = value.data
                    if (data != null) {
                        _teamExtras2.value = data["Extra2"].toString()
                    }
                    Log.d(TAG, "extrasTeam1: $data")
                } else {
                    Log.d(TAG, "No Data")
                }
            }
        return _teamExtras2
    }

    fun getScore1(id: String): MutableLiveData<MutableList<PlayerScore>> {
        scoreDataModelArrayList1.clear()
        firestoreRef.document(id).collection("ScoreCard").document("ScoreTeam1")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(TAG, "Listen Failed", error)
                    return@addSnapshotListener
                }

                if (value != null) {
                    if (value.exists()) {
                        try {
                            if (Objects.requireNonNull(
                                    value.data
                                ).containsKey("ScoreTeam1")
                            ) {
                                val dataModelArrayList1: ArrayList<HashMap<String, String>>? =
                                    value["ScoreTeam1"] as ArrayList<HashMap<String, String>>?
                                scoreDataModelArrayList1.clear()
                                if (dataModelArrayList1 != null && dataModelArrayList1.size > 0) {
                                    for (i in dataModelArrayList1.indices) {
                                        val team1HashMap = dataModelArrayList1[i]
                                        val scoreDataModel = PlayerScore()
                                        scoreDataModel.name = team1HashMap["Name"]
                                        scoreDataModel.otherInfo = team1HashMap["OtherInfo"]
                                        scoreDataModel.ball = team1HashMap["Ball"]
                                        scoreDataModel.run = team1HashMap["Run"]
                                        scoreDataModel.fours = team1HashMap["4s"]
                                        scoreDataModel.sixes = team1HashMap["6s"]
                                        scoreDataModel.sr = team1HashMap["SR"]
                                        scoreDataModelArrayList1.add(scoreDataModel)
                                    }
                                }
                                playerScore1 = scoreDataModelArrayList1
                                Log.d(TAG, "getDetails1: ${value.data}")
                                _teamScore.value = playerScore1
                            }
                        } catch (index: IndexOutOfBoundsException) {
                            index.printStackTrace()
                        }
                    } else {
                        Log.d(TAG, "NO DATA")
                    }
                }
            }
        return _teamScore
    }

    fun getScore2(id: String): MutableLiveData<MutableList<PlayerScore>> {
        scoreDataModelArrayList2.clear()
        firestoreRef.document(id).collection("ScoreCard").document("ScoreTeam2")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(TAG, "Listen Failed", error)
                    return@addSnapshotListener
                }

                if (value != null) {
                    if (value.exists()) {
                        try {
                            if (Objects.requireNonNull(
                                    value.data
                                ).containsKey("ScoreTeam2")
                            ) {
                                val dataModelArrayList1: ArrayList<HashMap<String, String>>? =
                                    value["ScoreTeam2"] as ArrayList<HashMap<String, String>>?
                                scoreDataModelArrayList2.clear()
                                if (dataModelArrayList1 != null && dataModelArrayList1.size > 0) {
                                    for (i in dataModelArrayList1.indices) {
                                        val team1HashMap = dataModelArrayList1[i]
                                        val scoreDataModel2 = PlayerScore()
                                        scoreDataModel2.name = team1HashMap["Name"]
                                        scoreDataModel2.otherInfo = team1HashMap["OtherInfo"]
                                        scoreDataModel2.ball = team1HashMap["Ball"]
                                        scoreDataModel2.run = team1HashMap["Run"]
                                        scoreDataModel2.fours = team1HashMap["4s"]
                                        scoreDataModel2.sixes = team1HashMap["6s"]
                                        scoreDataModel2.sr = team1HashMap["SR"]
                                        scoreDataModelArrayList2.add(scoreDataModel2)
                                    }
                                    playerScore2 = scoreDataModelArrayList2
                                    Log.d(TAG, "getDetails2: ${value.data}")
                                    _teamScore2.value = playerScore2
                                }
                            }
                        } catch (index: IndexOutOfBoundsException) {
                            index.printStackTrace()
                        }
                    } else {
                        Log.d(TAG, "NO DATA")
                    }
                }
            }
        return _teamScore2
    }

    fun getSpecificMatchData(id: String): MutableLiveData<HomeMatch?> {

        firestore.collection("MatchList").document(id)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(TAG, "Listen Failed", error)
                    return@addSnapshotListener
                }

                if (value != null) {
                    Log.d(TAG, "getSpecificMatchData: ${value.data}")
                    val allMatch: HomeMatch? = value.toObject(HomeMatch::class.java)
                    mutableData.value = allMatch
                } else {
                    Log.d(TAG, "No Data")
                }
            }
        return mutableData
    }

    fun getBowlerList1(id: String): MutableLiveData<BowlerList> {
        bowlerDataModelArrayList1.clear()
        firestoreRef.document(id).collection("ScoreCard").document("BowlerList1")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(TAG, "Listen Failed", error)
                    return@addSnapshotListener
                }

                if (value != null) {
                    if (value.exists()) {
                        try {
                            if (Objects.requireNonNull(
                                    value.data
                                ).containsKey("BowlerList1")
                            ) {
                                val dataModelArrayList1: ArrayList<HashMap<String, String>>? =
                                    value["BowlerList1"] as ArrayList<HashMap<String, String>>?
                                bowlerDataModelArrayList1.clear()
                                if (dataModelArrayList1 != null && dataModelArrayList1.size > 0) {
                                    for (i in dataModelArrayList1.indices) {
                                        val bowler1HashMap = dataModelArrayList1[i]
                                        val bowler = Bowlers()
                                        bowler.name = bowler1HashMap["Name"]
                                        bowler.over = bowler1HashMap["Over"]
                                        bowler.wicket = bowler1HashMap["Wicket"]
                                        bowler.maiden = bowler1HashMap["Maiden"]
                                        bowler.run = bowler1HashMap["Run"]
                                        bowler.er = bowler1HashMap["ER"]
                                        bowlerDataModelArrayList1.add(bowler)
                                    }
                                }
                                bowlerList.team1List = bowlerDataModelArrayList1
                                Log.d(TAG, "getBowler List1: ${value.data}")
                                _bowlerlist1.value = bowlerList

                            }
                        } catch (index: IndexOutOfBoundsException) {
                            index.printStackTrace()
                        }
                    } else {
                        Log.d(TAG, "NO DATA")
                    }
                }
            }
        return _bowlerlist1
    }

    fun getBowlerList2(id: String): MutableLiveData<BowlerList> {
        bowlerDataModelArrayList2.clear()
        firestoreRef.document(id).collection("ScoreCard").document("BowlerList2")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(TAG, "Listen Failed", error)
                    return@addSnapshotListener
                }

                if (value != null) {
                    if (value.exists()) {
                        Log.d(TAG, "getBowlerList2 Outer: ${value.data}")
                        try {
                            if (Objects.requireNonNull(
                                    value.data
                                ).containsKey("BowlerList2")
                            ) {
                                val dataModelArrayList1: ArrayList<HashMap<String, String>>? =
                                    value["BowlerList2"] as ArrayList<HashMap<String, String>>?
                                bowlerDataModelArrayList2.clear()
                                if (dataModelArrayList1 != null && dataModelArrayList1.size > 0) {
                                    for (i in dataModelArrayList1.indices) {
                                        val bowler1HashMap = dataModelArrayList1[i]
                                        val bowler = Bowlers()
                                        bowler.name = bowler1HashMap["Name"]
                                        bowler.over = bowler1HashMap["Over"]
                                        bowler.wicket = bowler1HashMap["Wicket"]
                                        bowler.maiden = bowler1HashMap["Maiden"]
                                        bowler.run = bowler1HashMap["Run"]
                                        bowler.er = bowler1HashMap["ER"]
                                        bowlerDataModelArrayList2.add(bowler)
                                    }
                                }
                                bowlerList.team2List = bowlerDataModelArrayList2
                                Log.d(TAG, "getBowler List2: ${value.data}")
                                _bowlerlist2.value = bowlerList

                            }
                        } catch (index: IndexOutOfBoundsException) {
                            index.printStackTrace()
                        }
                    } else {
                        Log.d(TAG, "NO DATA")
                    }
                }
            }
        return _bowlerlist2
    }

    fun getWicketList1(id: String): MutableLiveData<AllWicketList> {
        wicketDataModelArrayList1.clear()
        firestoreRef.document(id).collection("ScoreCard").document("WicketList1")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(TAG, "Listen Failed", error)
                    return@addSnapshotListener
                }

                if (value != null) {
                    if (value.exists()) {
                        Log.d(TAG, "WicketList1 Outer: ${value.data}")
                        try {
                            if (Objects.requireNonNull(
                                    value.data
                                ).containsKey("WicketList1")
                            ) {
                                val dataModelArrayList1: ArrayList<HashMap<String, String>>? =
                                    value["WicketList1"] as ArrayList<HashMap<String, String>>?
                                wicketDataModelArrayList1.clear()
                                if (dataModelArrayList1 != null && dataModelArrayList1.size > 0) {
                                    for (i in dataModelArrayList1.indices) {
                                        val wicket1HashMap = dataModelArrayList1[i]
                                        val wicketFall = WicketFall()
                                        wicketFall.name = wicket1HashMap["Name"]
                                        wicketFall.over = wicket1HashMap["Over"]
                                        wicketFall.score = wicket1HashMap["Score"]
                                        wicketDataModelArrayList1.add(wicketFall)
                                    }
                                }
                                wicketList.wicketList1 = wicketDataModelArrayList1
                                Log.d(TAG, "WicketList1 List1: ${value.data}")
                                _wicketList1.value = wicketList

                            }
                        } catch (index: IndexOutOfBoundsException) {
                            index.printStackTrace()
                            index.printStackTrace()
                        }
                    } else {
                        Log.d(TAG, "NO DATA")
                    }
                }
            }
        return _wicketList1
    }


    fun getWicketList2(id: String): MutableLiveData<AllWicketList> {
        wicketDataModelArrayList2.clear()
        firestoreRef.document(id).collection("ScoreCard").document("WicketList2")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(TAG, "Listen Failed", error)
                    return@addSnapshotListener
                }

                if (value != null) {
                    if (value.exists()) {
                        Log.d(TAG, "WicketList2 Outer: ${value.data}")
                        try {
                            if (Objects.requireNonNull(
                                    value.data
                                ).containsKey("WicketList2")
                            ) {
                                val dataModelArrayList1: ArrayList<HashMap<String, String>>? =
                                    value["WicketList2"] as ArrayList<HashMap<String, String>>?
                                wicketDataModelArrayList2.clear()
                                if (dataModelArrayList1 != null && dataModelArrayList1.size > 0) {
                                    for (i in dataModelArrayList1.indices) {
                                        val wicket1HashMap = dataModelArrayList1[i]
                                        val wicketFall = WicketFall()
                                        wicketFall.name = wicket1HashMap["Name"]
                                        wicketFall.over = wicket1HashMap["Over"]
                                        wicketFall.score = wicket1HashMap["Score"]
                                        wicketDataModelArrayList2.add(wicketFall)
                                    }
                                }
                                wicketList.wicketList2 = wicketDataModelArrayList2
                                Log.d(TAG, "WicketList1 List1: ${value.data}")
                                _wicketList2.value = wicketList

                            }
                        } catch (index: IndexOutOfBoundsException) {
                            index.printStackTrace()
                        }
                    } else {
                        Log.d(TAG, "NO DATA")
                    }
                }
            }
        return _wicketList2
    }

    fun getInfo(id: String): MutableLiveData<Info?> {
        firestoreRef.document(id).addSnapshotListener { value, error ->
            if (error != null) {
                Log.w(TAG, "Listen Failed", error)
                return@addSnapshotListener
            }

            if (value != null) {

                val info: Info? = value.toObject(Info::class.java)
                Log.d(TAG, "getInfo: ${value.data}")
                _matchInfo.value = info
            } else {
                Log.d(TAG, "NO DATA")
            }
        }
        return _matchInfo
    }

    fun getRunRate(id: String): MutableLiveData<String> {
        firestoreRef.document(id).collection(Constants.COLLECTION_PATH_LIVE_MATCH)
            .document("RunRateInfo")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(TAG, "Listen Failed", error)
                    return@addSnapshotListener
                }

                if (value != null) {
                    val data = value.data
                    if (data != null) {
                        _runRate.value = data.toString()
                        Log.d(TAG, "getRunRate: $data")
                    }
                } else {
                    Log.d(TAG, "No Data")
                }
            }
        return _runRate
    }

    fun getDisplayBatsman1Info(id: String): MutableLiveData<String> {
        firestoreRef.document(id).collection(Constants.COLLECTION_PATH_LIVE_MATCH)
            .document("ScoreBatsman1")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(TAG, "Listen Failed", error)
                    return@addSnapshotListener
                }

                if (value != null) {
                    val data = value.data
                    if (data != null) {
                        _batman1Info.value = data.toString()
                        Log.d(TAG, "getBatsman1Info: $data")
                    }
                } else {
                    Log.d(TAG, "No Data")
                }
            }
        return _batman1Info
    }

    fun getDisplayBatsman2Info(id: String): MutableLiveData<String> {
        firestoreRef.document(id).collection(Constants.COLLECTION_PATH_LIVE_MATCH)
            .document("ScoreBatsman2")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(TAG, "Listen Failed", error)
                    return@addSnapshotListener
                }

                if (value != null) {
                    val data = value.data
                    if (data != null) {
                        _batman2Info.value = data.toString()
                        Log.d(TAG, "getBatsman2Info: $data")
                    }
                } else {
                    Log.d(TAG, "No Data")
                }
            }
        return _batman2Info
    }

    fun getBowlerInfo(id: String): MutableLiveData<String> {
        firestoreRef.document(id).collection(Constants.COLLECTION_PATH_LIVE_MATCH)
            .document("BowlerInfo")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(TAG, "Listen Failed", error)
                    return@addSnapshotListener
                }

                if (value != null) {
                    val data = value.data
                    if (data != null) {
                        _bowlerInfo.value = data.toString()
                        Log.d(TAG, "BowlerInfo: $data")
                    }
                } else {
                    Log.d(TAG, "No Data")
                }
            }
        return _bowlerInfo
    }

    /* fun getLiveInfo(id: String): MutableLiveData<String> {
         firestoreRef.document(id).collection(Constants.COLLECTION_PATH_LIVE_MATCH)
             .addSnapshotListener { value, error ->
                 if (error != null) {
                     Log.w(TAG, "Listen Failed", error)
                     return@addSnapshotListener
                 }

                  if (value != null) {
                     val docs = value.documents
                     for (item in docs) {
                         val data = item.data
                         if (data != null) {
                             _batman1Info.value = data.toString()
                             Log.d(TAG, "LiveData: $data")
                         }
                     }
                 } else {
                     Log.d(TAG, "No Data")
                 }
             }
         return _batman2Info
     }
 */

    fun getPartnershipInfo(id: String): MutableLiveData<String> {
        firestoreRef.document(id).collection(Constants.COLLECTION_PATH_LIVE_MATCH)
            .document("PartnershipInfo")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(TAG, "Listen Failed", error)
                    return@addSnapshotListener
                }

                if (value != null) {
                    val data = value.data
                    if (data != null) {
                        _partnershipInfo.value = data.toString()
                        Log.d(TAG, "PartnerShipInfo: $data")
                    }
                } else {
                    Log.d(TAG, "No Data")
                }
            }
        return _partnershipInfo
    }

    fun getBallXRunInfo(id: String): MutableLiveData<String> {
        firestoreRef.document(id).collection(Constants.COLLECTION_PATH_LIVE_MATCH)
            .document("BallXRun")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(TAG, "Listen Failed", error)
                    return@addSnapshotListener
                }

                if (value != null) {
                    val data = value.data
                    if (data != null) {
                        _ballXrun.value = data.toString()
                        Log.d(TAG, "BallXRun: $data")
                    }
                } else {
                    Log.d(TAG, "No Data")
                }
            }
        return _ballXrun
    }

    fun getSessionLambi(id: String): MutableLiveData<String> {
        firestoreRef.document(id).collection("SessionRate").document("Lambi")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(TAG, "Listen Failed", error)
                    return@addSnapshotListener
                }

                if (value != null) {
                    val data = value.data
                    if (data != null) {
                        _sessionLambi.value = data.toString()
                        Log.d(TAG, "Lambi: $data")
                    }
                } else {
                    Log.d(TAG, "No Data")
                }
            }
        return _sessionLambi
    }

    fun getLambiBallXRun(id: String): MutableLiveData<String> {
        firestoreRef.document(id).collection(Constants.COLLECTION_PATH_LIVE_MATCH)
            .document("BallXRunLambi")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(TAG, "Listen Failed", error)
                    return@addSnapshotListener
                }

                if (value != null) {
                    val data = value.data
                    if (data != null) {
                        _lambiBallXrun.value = data.toString()
                        Log.d(TAG, "Lambi BallXRun: $data")
                    }
                } else {
                    Log.d(TAG, "No Data")
                }
            }
        return _lambiBallXrun
    }

    fun getLiveMatch(id: String): MutableLiveData<String> {
        firestoreRef.document(id).collection(Constants.COLLECTION_PATH_LIVE_MATCH).document("LM")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(TAG, "Listen Failed", error)
                    return@addSnapshotListener
                }

                if (value != null) {
                    val data = value.data
                    if (data != null) {
                        _liveMatch.value = data.toString()
                        Log.d(TAG, "LM: $data")
                    }
                } else {
                    Log.d(TAG, "No Data")
                }
            }
        return _liveMatch
    }

    fun getSession(id: String): MutableLiveData<String> {
        firestoreRef.document(id).collection("SessionRate").document("Session")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(TAG, "Listen Failed", error)
                    return@addSnapshotListener
                }

                if (value != null) {
                    val data = value.data
                    if (data != null) {
                        _session.value = data.toString()
                        Log.d(TAG, "SessionFragment: $data")
                    }
                } else {
                    Log.d(TAG, "No Data")
                }
            }
        return _session
    }

    fun getLastBall(id: String): MutableLiveData<String> {
        firestoreRef.document(id).collection("LastBallInfo").document("LB")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(TAG, "Listen Failed", error)
                    return@addSnapshotListener
                }

                if (value != null) {
                    val data = value.data
                    if (data != null) {
                        _lastball.value = data.toString()
                        Log.d(TAG, "LB: $data")
                    }
                } else {
                    Log.d(TAG, "No Data")
                }
            }
        return _lastball
    }

    fun getBallByBall(id: String): MutableLiveData<String> {
        firestoreRef.document(id).collection("LastBallInfo").document("LBD")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(TAG, "Listen Failed", error)
                    return@addSnapshotListener
                }

                if (value != null) {
                    val data = value.data
                    if (data != null) {
                        _ballByBall.value = data.toString()
                        Log.d(TAG, "LBD: $data")
                    }
                } else {
                    Log.d(TAG, "No Data")
                }
            }
        return _ballByBall
    }

    fun getOtherMessage(id: String): MutableLiveData<String> {
        firestoreRef.document(id).collection(Constants.COLLECTION_PATH_LIVE_MATCH)
            .document("OtherMessage")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(TAG, "Listen Failed", error)
                    return@addSnapshotListener
                }

                if (value != null) {
                    val data = value.data
                    if (data != null) {
                        _otherMessage.value = data.toString()
                        Log.d(TAG, "oT: $data")
                    }
                } else {
                    Log.d(TAG, "No Data")
                }
            }
        return _otherMessage
    }

    fun getMatchRate(id: String): MutableLiveData<String> {
        firestoreRef.document(id).collection("MatchRate").document("Match")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(TAG, "Listen Failed", error)
                    return@addSnapshotListener
                }

                if (value != null) {
                    val data = value.data
                    if (data != null) {
                        _matchRate.value = data.toString()
                        Log.d(TAG, "mr: $data")
                    }
                } else {
                    Log.d(TAG, "No Data")
                }
            }
        return _matchRate
    }

    fun getFirstInnings(id: String): MutableLiveData<String> {
        firestoreRef.document(id).collection(Constants.COLLECTION_PATH_LIVE_MATCH)
            .document("IsFirstInnings")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(TAG, "Listen Failed", error)
                    return@addSnapshotListener
                }

                if (value != null) {
                    val data = value.data
                    if (data != null) {
                        _firstInnings.value = data.toString()
                        Log.d(TAG, "fi: $data")
                    }
                } else {
                    Log.d(TAG, "No Data")
                }
            }
        return _firstInnings
    }

    suspend fun insertMatch(matchBet: MatchBet) = db.getCricketGuruDao().insertMatch(matchBet)

    suspend fun updateMatch(matchBet: MatchBet) = db.getCricketGuruDao().updateMatch(matchBet)

    suspend fun deleteMatch(matchBet: MatchBet) = db.getCricketGuruDao().deleteMatch(matchBet)

    suspend fun updateSession(sessionBet: SessionBet) =
        db.getCricketGuruDao().updateSession(sessionBet)

    suspend fun deleteSession(sessionBet: SessionBet) =
        db.getCricketGuruDao().deleteSession(sessionBet)

    fun getAllMatchBet(matchid: String) = db.getCricketGuruDao().getAllMatches(matchid)

    fun matchBetNameList() = db.getCricketGuruDao().getMatchNameList()

    suspend fun insertMainSession(mainSession: MainSession) =
        db.getCricketGuruDao().insertMainSession(mainSession)

    suspend fun updateMainSession(mainSession: MainSession) =
        db.getCricketGuruDao().updateMainSession(mainSession)

    suspend fun deleteMainSession(mainSession: MainSession) =
        db.getCricketGuruDao().deleteMainSession(mainSession)

    fun getAllMainSession(matchid: String) =
        db.getCricketGuruDao().getMainSession(matchid)

    suspend fun insertSessionBet(sessionBet: SessionBet) =
        db.getCricketGuruDao().insertSessionBet(sessionBet)

    fun getAllSessionBetList(sessionID: String) =
        db.getCricketGuruDao().getSessionBetList(sessionID)

    fun sessionNameList() = db.getCricketGuruDao().getSessionNameList()
    //  fun sessionNameListAsc() = db.getCricketGuruDao().getSessionNameListAsc()

    fun getMinMaxSessionList(query: String): ArrayList<Int> {
        val sessionMinMaxList = arrayListOf<Int>()
        val database = db.openHelper.readableDatabase
        val c = database.query(query)
        try {
            while (c.moveToNext()) {
                sessionMinMaxList.add(0, c.getInt(0))   // Max Value
                sessionMinMaxList.add(1, c.getInt(1))  // Min Value
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (c != null && c.isClosed) {
                c.close()
                database.close()
            }
        }
        Log.d(TAG, "getMinMaxSessionList: $sessionMinMaxList")
        return sessionMinMaxList
    }

    fun insertAccountModel(accountModel: AccountModel) =
        db.getCricketGuruDao().insertAccountModel(accountModel)

    fun updateAccountModel(accountModel: AccountModel) =
        db.getCricketGuruDao().updateAccountModel(accountModel)

    fun getAccountModelBySession(sessionID: String) =
        db.getCricketGuruDao().getAccountListBySession(sessionID)

    fun getAccountModelByMatch(matchid: Int) =
        db.getCricketGuruDao().getAccountListByMatch(matchid)

    fun getSeries(): MutableLiveData<MutableList<SeriesModel?>> {
        val valueListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                seriesList.clear()
                snapshot.children.forEach {
                    val model = it.getValue(SeriesModel::class.java)
                    seriesList.add(model)
                    Log.d(TAG, "getSeries: $it")
                }
                series.value = seriesList
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled Series", error.toException())
            }
        }

        database.child("series").orderByChild("seriesPriority").startAt(1.00, "seriesPriority")
            .endAt(100.00, "seriesPriority").addValueEventListener(valueListener)
        return series
    }

    fun getFixtures(id: String):  MutableLiveData<ArrayList<MatchModel?>>{
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                println("MatchDataList ----> $snapshot")
                Log.d(TAG, "onDataChangeID: ${id}")
                val arrayListMatchModel: ArrayList<MatchModel?> = arrayListOf()
                for(matchSnap:DataSnapshot in snapshot.children){
                    val matchModel: MatchModel? = matchSnap.getValue(MatchModel::class.java)
                    matchModel?.let {
                        arrayListMatchModel.add(matchModel)
                    }
                }
                fixtures.value = arrayListMatchModel
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled Fixtures", error.toException())
            }
        }
        mDatabase.child(id).child("match").orderByChild("matchDate").addValueEventListener(valueEventListener)
        return fixtures
    }
    fun getAllSeries(): MutableLiveData<MutableList<SeriesModel?>> {
        val series = MutableLiveData<MutableList<SeriesModel?>>()
        val seriesList = arrayListOf<SeriesModel?>()
        val valueListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                snapshot.children.forEach {
                    val model = it.getValue(SeriesModel::class.java)
                    seriesList.add(model)
                    Log.d(TAG, "getAllSeries: $it")
                }
                series.value = seriesList
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled Series", error.toException())
            }
        }

        database.child("series").orderByChild("seriesPriority").startAt(1.00, "seriesPriority")
            .endAt(100.00, "seriesPriority").addValueEventListener(valueListener)
        return series
    }



    fun getPointTable(id: String) {
        val valueListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "onDataChange: ${snapshot.value}")
                snapshot.children.forEach {
                    val model = it.getValue(PointTableModel::class.java)
                    Log.d(TAG, "getPointTable: $model")
                }


            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled PointTable", error.toException())
            }
        }

        mDatabase.child(id).child("pointTbl").orderByChild("teamName")
            .addValueEventListener(valueListener)

    }


    suspend fun getAllAppsList() = retrofitService.getAllAppsList()
    fun getCurrentApp(jsonObject: JsonObject) = retrofitService.sendApplicationNameToServer(jsonObject)

    fun sendPushTokenApp(jsonObject: JsonObject) = retrofitService.sendPushTokenToServer(jsonObject)

}
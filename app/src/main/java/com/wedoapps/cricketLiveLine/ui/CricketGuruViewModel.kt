package com.wedoapps.cricketLiveLine.ui

import android.app.Application
import android.os.Looper
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.JsonObject
import com.wedoapps.cricketLiveLine.model.Series.MatchModel
import com.wedoapps.cricketLiveLine.model.*
import com.wedoapps.cricketLiveLine.model.info.Info
import com.wedoapps.cricketLiveLine.model.matchBet.MatchBet
import com.wedoapps.cricketLiveLine.model.sessionBet.MainSession
import com.wedoapps.cricketLiveLine.model.sessionBet.SessionBet
import com.wedoapps.cricketLiveLine.model.sessionBet.declaresession.AccountModel
import com.wedoapps.cricketLiveLine.repository.CricketGuruRepository
import com.wedoapps.cricketLiveLine.utils.CommonResponse
import com.wedoapps.cricketLiveLine.utils.Constants
import com.wedoapps.cricketLiveLine.utils.Constants.TAG
import com.wedoapps.cricketLiveLine.utils.GetApplicationList
import com.wedoapps.cricketLiveLine.utils.PreferenceManager
import com.wedoapps.cricketLiveLine.utils.ApplicationList
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CricketGuruViewModel(
    val app: Application,
    private val repository: CricketGuruRepository,
) : AndroidViewModel(app) {

    private val storageRef = FirebaseStorage.getInstance().reference
    private val prefs = PreferenceManager(app)
    private val _imageDownloadedLiveData = MutableLiveData<Boolean>()
    val imageDownloadLiveData: LiveData<Boolean>
        get() = _imageDownloadedLiveData

    private val _smallAdMutableLiveData = MutableLiveData<String>()
    val smallAdLiveData: LiveData<String>
        get() = _smallAdMutableLiveData

    private val _bigAdMutableLiveData = MutableLiveData<String>()
    val bigAdLiveData: LiveData<String>
        get() = _bigAdMutableLiveData


    fun getAllMatch(): LiveData<MutableList<HomeMatch>> {
        return repository.getMatch()
    }

    fun getAllUpComingMatch(): LiveData<MutableList<HomeMatch>> {
        return repository.getUpComingMatches()
    }

    fun getLiveMatch(): LiveData<MutableList<HomeMatch>> {
        return repository.getLiveMatches()
    }

    fun getAllRecentMatch(): LiveData<MutableList<HomeMatch>> {
        return repository.getRecentMatches()
    }

    fun getAllTeam1(id: String): LiveData<Score?> {
        return repository.team1(id)
    }

    fun getAllTeam2(id: String): LiveData<Score?> {
        return repository.team2(id)
    }

    fun getTeam1Extras(id: String): LiveData<String> {
        return repository.extrasTeam1(id)
    }

    fun getTeam2Extras(id: String): LiveData<String> {
        return repository.extrasTeam2(id)
    }

    fun getScoreDetails1(id: String): LiveData<MutableList<PlayerScore>> {
        return repository.getScore1(id)
    }

    fun getScoreDetails2(id: String): LiveData<MutableList<PlayerScore>> {
        return repository.getScore2(id)
    }

    fun getSpecificIdDetail(id: String): LiveData<HomeMatch?> {
        return repository.getSpecificMatchData(id)
    }

    fun getBowlerList1(id: String): LiveData<BowlerList> {
        return repository.getBowlerList1(id)
    }

    fun getBowlerList2(id: String): LiveData<BowlerList> {
        return repository.getBowlerList2(id)
    }

    fun getWicketList1(id: String): LiveData<AllWicketList> {
        return repository.getWicketList1(id)
    }

    fun getWicketList2(id: String): LiveData<AllWicketList> {
        return repository.getWicketList2(id)
    }

    fun getInfo(id: String): LiveData<Info?> {
        return repository.getInfo(id)
    }

    fun getRunRate(id: String): LiveData<String> {
        return repository.getRunRate(id)
    }

    fun getBatsman1(id: String): LiveData<String> {
        return repository.getDisplayBatsman1Info(id)
    }

    fun getBatsman2(id: String): LiveData<String> {
        return repository.getDisplayBatsman2Info(id)
    }

    fun getBowlerInfo(id: String): LiveData<String> {
        return repository.getBowlerInfo(id)
    }

    fun getPartnership(id: String): LiveData<String> {
        return repository.getPartnershipInfo(id)
    }

    fun getBallXRun(id: String): LiveData<String> {
        return repository.getBallXRunInfo(id)
    }

    fun getSessionLambi(id: String): LiveData<String> {
        return repository.getSessionLambi(id)
    }

    fun getLambiBallXRun(id: String): LiveData<String> {
        return repository.getLambiBallXRun(id)
    }

    fun getLiveMatch(id: String): LiveData<String> {
        return repository.getLiveMatch(id)
    }

    fun getSession(id: String): LiveData<String> {
        return repository.getSession(id)
    }

    fun getLastBall(id: String): LiveData<String> {
        return repository.getLastBall(id)
    }

    fun getBallByBall(id: String): LiveData<String> {
        return repository.getBallByBall(id)
    }

    fun getOtherMessage(id: String): LiveData<String> {
        return repository.getOtherMessage(id)
    }

    fun getMatchRate(id: String): LiveData<String> {
        return repository.getMatchRate(id)
    }

    fun getFirstInnings(id: String): LiveData<String> {
        return repository.getFirstInnings(id)
    }

    fun saveMatchBet(matchBet: MatchBet) = viewModelScope.launch {
        repository.insertMatch(matchBet)
    }

    fun updateMatchBet(
        id: Int,
        matchId: String,
        rate: Int,
        amount: Int,
        type: String,
        team: String,
        allTeam: ArrayList<String>,
        default: Boolean,
        playerName: String,
        team1Value: Int,
        team2Value: Int,
        drawValue: Int,
    ) = viewModelScope.launch {
        val matchBet = MatchBet(
            id,
            matchId,
            rate,
            amount,
            type,
            team,
            allTeam,
            default,
            playerName,
            team1Value,
            team2Value,
            drawValue
        )
        repository.updateMatch(matchBet)
    }

    fun deleteMatchBet(matchBet: MatchBet) = viewModelScope.launch {
        repository.deleteMatch(matchBet)
    }

    fun getAllMatchBet(matchId: String) = repository.getAllMatchBet(matchId)

    fun getAllSessionsList(sessionID: String) = repository.getAllSessionBetList(sessionID)

    fun matchBetNameList() = repository.matchBetNameList()

    fun sessionNameList() = repository.sessionNameList()

//    fun sessionNameListAsc() = repository.sessionNameListAsc()

    fun insertMainSession(
        matchId: String,
        sessionName: String,
        selectedTeamName: String,
    ) = viewModelScope.launch {
        val mainSession = MainSession(
            null,
            matchId,
            sessionName,
            selectedTeamName,
        )
        repository.insertMainSession(mainSession)
    }

    fun deleteMainSession(mainSession: MainSession) = viewModelScope.launch {
        repository.deleteMainSession(mainSession)
    }

    fun updateMainSession(
        id: Int,
        matchId: String,
        sessionName: String,
        selectedTeamName: String,
    ) = viewModelScope.launch {
        val mainSession = MainSession(
            id,
            matchId,
            sessionName,
            selectedTeamName,
        )
        repository.updateMainSession(mainSession)
    }

    fun getAllMainSession(matchid: String) = repository.getAllMainSession(matchid)

    fun updateSessionBet(sessionBet: SessionBet) = viewModelScope.launch {
        repository.updateSession(sessionBet)
    }

    fun updateSessionBet(
        id: Int,
        sessionID: String,
        amount: Int,
        inning: Int,
        over: String,
        fandp: Int,
        yorn: Int,
        actualScore: Int,
        playerName: String,
        commission: Double,
    ) = viewModelScope.launch {
        val sessionBet = SessionBet(
            id, sessionID, amount, inning, over, fandp, yorn, actualScore, playerName, commission
        )
        repository.updateSession(sessionBet)
    }

    fun deleteSessionBet(sessionBet: SessionBet) = viewModelScope.launch {
        repository.deleteSession(sessionBet)
    }

    fun completeSessionBet(sessionBet: SessionBet) = viewModelScope.launch {
        repository.insertSessionBet(sessionBet)
    }

    fun minMax(sessionID: String): ArrayList<Int> {
        var data = arrayListOf<Int>()
        viewModelScope.launch {
            val query =
                "SELECT MAX(actualScore), MIN(actualScore) FROM sessionBet WHERE sessionID = $sessionID"
            data = repository.getMinMaxSessionList(query)
        }
        Log.d(TAG, "minMax: $data")
        return data
    }

    fun insertAccountModel(accountModel: AccountModel) = viewModelScope.launch {
        repository.insertAccountModel(accountModel)
    }

    fun updateAccountModel(accountModel: AccountModel) = viewModelScope.launch {
        repository.updateAccountModel(accountModel)
    }

    fun getAccountModelBySession(sessionID: String) = repository.getAccountModelBySession(sessionID)

    fun getAccountModelByMatch(matchId: Int) = repository.getAccountModelByMatch(matchId)

    fun getImageDownloadHandle(check: Boolean) {
        safeHandleImageDownloadHandle(check)
    }

    private fun safeHandleImageDownloadHandle(check: Boolean) {
        _imageDownloadedLiveData.postValue(check)
    }

    fun getAds() = viewModelScope.launch {
        repository.getAds()
        android.os.Handler(Looper.getMainLooper()).postDelayed({
            getSmallAdUrl()
            getBigAdUrl()
        }, 1000L)
//        getSmallAdUrl()
//        getBigAdUrl()
    }

    private fun getSmallAdUrl() {
        storageRef.child(
            Constants.ADS_STORAGE_PATH + prefs.getSmallAdName()
        ).downloadUrl.addOnSuccessListener {
            Log.d(TAG, "getSmallAdUrl: $it")
            if (it != null) {
                _smallAdMutableLiveData.value = it.toString()
            }
        }
    }

    private fun getBigAdUrl() {
        storageRef.child(Constants.ADS_STORAGE_PATH + prefs.getFullAdName()).downloadUrl.addOnSuccessListener {
            Log.d(TAG, "getBigAdUrl: $it")
            if (it != null) {
                _bigAdMutableLiveData.value = it.toString()
            } else {
                Log.d(TAG, "getBigAdUrl: NO URL")
            }
        }
    }

    fun getSeries() = repository.getSeries()

    fun getFixtures(id: String): LiveData<ArrayList<MatchModel?>> {
        return repository.getFixtures(id)
    }

    fun getPointTable(id: String) = repository.getPointTable(id)

    fun getAllSeries() = repository.getAllSeries()


    val errorMessage = MutableLiveData<String>()
    val statusVal = MutableLiveData<Boolean>()
    val applicationList = MutableLiveData<GetApplicationList>()
    val applicationData = MutableLiveData<ApplicationList>()
    val apiNotificationData = MutableLiveData<CommonResponse>()

    var job: Job? = null
    var jobApplication: Job? = null
    var jobNotification: Job? = null


    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Api Exception handled: ${throwable.localizedMessage}")
    }

    val loading = MutableLiveData<Boolean>()

    @OptIn(DelicateCoroutinesApi::class)
    fun getAllApplicationList() {
        job = GlobalScope.launch {
            withContext(Dispatchers.Main + exceptionHandler) {
                val response = repository.getAllAppsList()
                Log.d(TAG, "TestApplicationLE: ${response.errorBody().toString()}")
                Log.d(TAG, "TestApplicationL1: ${response}")
                Log.d(TAG, "TestApplicationLB: ${response.body().toString()}")
                if (response.isSuccessful) {

                    onStatus(response.body()!!.status)
                    if (response.body()!!.status) {
                        GlobalScope.launch {
                            applicationList.postValue(response.body())

                        }
                    } else {
                        onError(response.body()!!.message)
                    }

                    loading.value = false
                } else {
                    Log.d(TAG, "TestApplicationLF: ${response.body()?.status}")
                    response.body()?.status?.let { onStatus(it) }
                    onError("Error : ${response.message()} ")
                }

            }
        }


    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getApplicationData(jsonObject: JsonObject) {
        jobApplication = GlobalScope.launch {

            val response: Call<ApplicationList> = repository.getCurrentApp(jsonObject)
            withContext(Dispatchers.Main + exceptionHandler) {

                response.enqueue(object : Callback<ApplicationList> {
                    override fun onResponse(
                        call: Call<ApplicationList>, response: Response<ApplicationList>
                    ) {
                        Log.d(TAG, "TestApplicationE: ${response.errorBody().toString()}")
                        Log.d(TAG, "TestApplication1: ${response}")
                        Log.d(TAG, "TestApplicationB: ${response.body().toString()}")
                        if (response.isSuccessful) {

                            onStatus(response.body()!!.status)
                            if (response.body()!!.status) {
                                applicationData.postValue(response.body())
                            } else {
                                onError(response.body()!!.message)
                            }

                            loading.value = false
                        } else {
                            response.body()?.status?.let { onStatus(it) }
                            onError("Error : ${response.message()} ")
                        }

                    }

                    override fun onFailure(call: Call<ApplicationList>, t: Throwable) {
                        onStatus(false)
                        Log.d(TAG, "TestApplicationF: ${t.localizedMessage}")

                        onError("Error : ${t.localizedMessage} ")
                    }

                })


            }
        }
    }


    @OptIn(DelicateCoroutinesApi::class)
    fun sendApiNotificationData(jsonObject: JsonObject) {
        jobNotification = GlobalScope.launch {

            val response: Call<CommonResponse> = repository.sendPushTokenApp(jsonObject)

            withContext(Dispatchers.Main + exceptionHandler) {

                response.enqueue(object : Callback<CommonResponse> {
                    override fun onResponse(
                        call: Call<CommonResponse>, response: Response<CommonResponse>
                    ) {
                        Log.d(TAG, "TestResponseE: ${response.errorBody().toString()}")
                        Log.d(TAG, "TestResponse1: ${response}")
                        Log.d(TAG, "TestResponseB: ${response.body().toString()}")
                        if (response.isSuccessful) {
                            response.body()?.status?.let { onStatus(it) }

                            if (response.body()?.status!!) {
                                apiNotificationData.postValue(response.body())

                            } else {
                                response.body()?.message?.let { onError(it) }
                            }

                            loading.value = false

                        } else {
                            response.body()?.status?.let { onStatus(it) }
                            onError("Error : ${response.message()} ")
                        }

                    }

                    override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                        onStatus(false)
                        onError("Error : ${t.localizedMessage} ")
                    }

                })


            }
        }
    }

    private fun onError(message: String) {
        errorMessage.value = message
        loading.value = false
    }

    private fun onStatus(status: Boolean) {
        statusVal.value = status
        loading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
        jobApplication?.cancel()
        jobNotification?.cancel()
    }

}


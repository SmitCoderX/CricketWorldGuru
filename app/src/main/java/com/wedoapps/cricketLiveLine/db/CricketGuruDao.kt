package com.wedoapps.cricketLiveLine.db

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.wedoapps.cricketLiveLine.model.matchBet.MatchBet
import com.wedoapps.cricketLiveLine.model.sessionBet.MainSession
import com.wedoapps.cricketLiveLine.model.sessionBet.SessionBet
import com.wedoapps.cricketLiveLine.model.sessionBet.declaresession.AccountModel

@Dao
interface CricketGuruDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMatch(match: MatchBet)

    @Update
    fun updateMatch(match: MatchBet)

    @Delete
    fun deleteMatch(match: MatchBet)

    @Query("SELECT * from matchbet WHERE matchID = :matchid")
    @JvmSuppressWildcards
    fun getAllMatches(matchid: String): LiveData<List<MatchBet>>

    @Query("SELECT playerName from matchbet")
    @JvmSuppressWildcards
    fun getMatchNameList(): LiveData<List<String>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSessionBet(sessionBet: SessionBet)

    @Query("SELECT * FROM sessionbet where sessionID = :sessionID")
    @JvmSuppressWildcards
    fun getSessionBetList(sessionID: String): LiveData<List<SessionBet>>

    @Query("SELECT playerName FROM sessionbet")
    @JvmSuppressWildcards
    fun getSessionNameList(): LiveData<List<String>>

   /* @Query("SELECT * FROM sessionbet ORDER BY playerName ASC")
    @JvmSuppressWildcards
    fun getSessionNameListAsc(): LiveData<List<String>>*/

    @RawQuery
    fun getMinMaxSessionList(query: SupportSQLiteQuery): Cursor

    @Update
    fun updateSession(sessionBet: SessionBet)

    @Delete
    fun deleteSession(sessionBet: SessionBet)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMainSession(mainSession: MainSession)

    @Update
    fun updateMainSession(mainSession: MainSession)

    @Delete
    fun deleteMainSession(mainSession: MainSession)


    @Query("SELECT * FROM mainSession where matchID = :matchID")
    @JvmSuppressWildcards
    fun getMainSession(matchID: String): LiveData<List<MainSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAccountModel(accountModel: AccountModel)

    @Update
    fun updateAccountModel(accountModel: AccountModel)

    @Query("SELECT * FROM accountmodel WHERE sessionID = :sessionID")
    fun getAccountListBySession(sessionID: String): LiveData<List<AccountModel>>

    @Query("SELECT * FROM accountmodel WHERE matchID = :matchID")
    fun getAccountListByMatch(matchID: Int): LiveData<List<AccountModel>>


}
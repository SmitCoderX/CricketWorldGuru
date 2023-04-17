package com.wedoapps.cricketLiveLine.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.wedoapps.cricketLiveLine.model.matchBet.MatchBet
import com.wedoapps.cricketLiveLine.model.sessionBet.MainSession
import com.wedoapps.cricketLiveLine.model.sessionBet.SessionBet
import com.wedoapps.cricketLiveLine.model.sessionBet.declaresession.AccountModel
import com.wedoapps.cricketLiveLine.utils.Convertors


@Database(entities = [MatchBet::class, MainSession::class, SessionBet::class, AccountModel::class], version = 1, exportSchema = false)


@TypeConverters(Convertors::class)
abstract class CricketGuruDatabase : RoomDatabase() {

    abstract fun getCricketGuruDao(): CricketGuruDao

    companion object {

        @Volatile
        private var instance: CricketGuruDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also {
                instance = it
            }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                CricketGuruDatabase::class.java,
                "CricketGuru.db"
            ).allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()


    }

}
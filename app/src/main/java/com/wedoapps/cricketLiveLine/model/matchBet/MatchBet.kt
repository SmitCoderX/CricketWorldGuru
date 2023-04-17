package com.wedoapps.cricketLiveLine.model.matchBet

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Entity(
    tableName = "MatchBet"
)

@Parcelize
public data class MatchBet(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var matchID: String? = "",
    var rate: Int? = null,
    var amount: Int? = null,
    var type: String? = "",
    var team: String? = "",
    var allTeams: ArrayList<String>? = null,
    var default: Boolean? = false,
    var playerName: String? = "",
    var team1Value: Int? = null,
    var team2Value: Int? = null,
    var drawValue: Int? = null,
    var date: String = getDateAndTime(),
    var totalAmount: Double? = 0.0,
) : Parcelable {

    /*constructor():this(
        -1,
        "",
        0,
        0,
        "",
        "",
        ArrayList<String>(),
        false,
        "",
        0,
        1,
        2,
        ""
    ){

    }*/
    companion object {
        @SuppressLint("SimpleDateFormat")
        private fun getDateAndTime(): String {
            val currentTime: Date = Calendar.getInstance().time
            val simpleFormat = SimpleDateFormat("dd MMM hh:mm")
            return simpleFormat.format(currentTime)
        }
    }
}

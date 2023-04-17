package com.wedoapps.cricketLiveLine.model.sessionBet

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*


@Entity(
    tableName = "mainSession"
)
@Parcelize
data class MainSession(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var matchID: String? = "",
    var sessionName: String? = "",
    var selectedTeamName: String? = "",
    var dataAndTime: String = getDateAndTime(),
) : Parcelable {

    @Ignore
    constructor() : this(0,"")

    companion object {
        @SuppressLint("SimpleDateFormat")
        private fun getDateAndTime(): String {
            val currentTime: Date = Calendar.getInstance().time
            val simpleFormat = SimpleDateFormat("dd MMM hh:mm")
            return simpleFormat.format(currentTime)
        }
    }


}

package com.wedoapps.cricketLiveLine.model

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class HomeMatch(
    @Expose
    @PropertyName("id")
    @field:SerializedName("id")
    var id: String? = "",
    @Expose
    @PropertyName("MatchDetail")
    @field:SerializedName("MatchDetail")
    var matchDetail: String? = "",
    @Expose
    @PropertyName("CurrentDate")
    @field:SerializedName("CurrentDate")
    var currentDate: String? = "",
    @Expose
    @PropertyName("MatchStatus")
    @field:SerializedName("MatchStatus")
    var matchStatus: String? = "",
    @Expose
    @PropertyName("Team2")
    @field:SerializedName("Team2")
    var team2: String? = "",
    @Expose
    @PropertyName("Team1")
    @field:SerializedName("Team1")
    var team1: String? = "",
    @Expose
    @PropertyName("CodeTeam1")
    @field:SerializedName("CodeTeam1")
    var codeTeam1: String? = "",
    @Expose
    @PropertyName("CodeTeam2")
    @field:SerializedName("CodeTeam2")
    var codeTeam2: String? = "",
    @Expose
    @PropertyName("FullNameTeam1")
    @field:SerializedName("FullNameTeam1")
    var fullNameTeam1: String? = "",
    @Expose
    @PropertyName("FullNameTeam2")
    @field:SerializedName("FullNameTeam2")
    var fullNameTeam2: String? = "",
    @Expose
    @PropertyName("MatchResult")
    @field:SerializedName("MatchResult")
    var matchResult: String? = "",
    @Expose
    @PropertyName("IsShowFlag")
    @field:SerializedName("IsShowFlag")
    var isShowFlag: Boolean? = false,
    @Expose
    @PropertyName("IsToss")
    @field:SerializedName("IsToss")
    var isToss: Boolean? = false,
    @Expose
    @PropertyName("MatchDate")
    @field:SerializedName("MatchDate")
    val matchDate: Long? = null,
    @Expose
    @PropertyName("Venue")
    @field:SerializedName("Venue")
    val venue: String? = "",
    @Expose
    @PropertyName("SessionHistoryInfo1")
    @field:SerializedName("SessionHistoryInfo1")
    var sessionHistoryInfo1: List<Session>? = listOf(),
    @Expose
    @PropertyName("SessionHistoryInfo2")
    @field:SerializedName("SessionHistoryInfo2")
    var sessionHistoryInfo2: List<Session>? = listOf(),
) : Parcelable {

}


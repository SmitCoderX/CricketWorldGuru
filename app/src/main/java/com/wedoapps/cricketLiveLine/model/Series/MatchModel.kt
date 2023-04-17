package com.wedoapps.cricketLiveLine.model.Series

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.PropertyName
import com.google.gson.annotations.Expose
import kotlinx.parcelize.Parcelize

@Parcelize
data class MatchModel(

	@Expose
	@PropertyName("matchDate")
	var matchDate: Long? = null,

	@Expose
	@PropertyName("matchId")
    var matchId: String? = "",

	@Expose
	@PropertyName("matchNo")
	var matchNo: String? = "",

	@Expose
	@PropertyName("matchPriority")
	var matchPriority: Long?= null,

	@Expose
	@PropertyName("matchResult")
	var matchResult: String? = "",

	@Expose
	@PropertyName("matchStatus")
	var matchStatus: String? = "",

	@Expose
	@PropertyName("matchType")
	var matchType: String? = "",

	@Expose
	@PropertyName("matchTime")
	var matchTime: String? = "",

	@Expose
	@PropertyName("seriesId")
	var seriesId: String? = "",

	@Expose
	@PropertyName("matchRate1")
	var matchRate1: String? = "",

	@Expose
	@PropertyName("matchRate2")
	var matchRate2: String? = "",

	@Expose
	@PropertyName("team1Code")
	var team1Code: String? = "",

	@PropertyName("team1FullName")
	var team1FullName: String? = "",

	@Expose
	@PropertyName("team1Over")
	var team1Over: String? = "",

	@Expose
	@PropertyName("team1Score")
	var team1Score: String? = "",

	@Expose
	@PropertyName("team2Code")
	var team2Code: String? = "",

	@PropertyName("team2FullName")
	var team2FullName: String? = "",

	@Expose
	@PropertyName("team2Over")
	var team2Over: String? = "",

	@Expose
	@PropertyName("team2Score")
	var team2Score: String? = "",

	@Expose
	@PropertyName("favTeamName")
	var favTeamName: String? = "",

	@Expose
	@PropertyName("venue")
	var venue: String? = "",

): Parcelable

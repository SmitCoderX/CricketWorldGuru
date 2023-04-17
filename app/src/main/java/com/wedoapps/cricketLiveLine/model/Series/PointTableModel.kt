package com.wedoapps.cricketLiveLine.model.Series

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.PropertyName
import com.google.gson.annotations.Expose
import kotlinx.parcelize.Parcelize

@Parcelize
data class PointTableModel(

	@Expose
	@PropertyName("pointId")
	var pointId: String? = "",

	@Expose
	@PropertyName("teamName")
	var teamName: String? = "",

	@Expose
	@PropertyName("teamTotalNoOfMatch")
	var totalNoOfMatch: String? = "",

	@Expose
	@PropertyName("matchPlayed")
    var matchPlayed: String? = "",

	@Expose
	@PropertyName("matchWin")
	var matchWin: String? = "",

	@Expose
	@PropertyName("matchLose")
	var matchLose: String? = "",

	@Expose
	@PropertyName("matchTie")
	var matchTie: String? = "",

	@Expose
	@PropertyName("matchPTS")
	var matchPTS: Long? = null,

	@Expose
	@PropertyName("matchNrr")
	var matchNrr: String? = "",


): Parcelable
package com.wedoapps.cricketLiveLine.model.info

import com.google.firebase.firestore.PropertyName
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Team(
    @Expose
    @PropertyName("Team2")
    val team2: String? = "",
)

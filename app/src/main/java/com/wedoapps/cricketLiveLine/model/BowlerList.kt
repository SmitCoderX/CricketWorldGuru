package com.wedoapps.cricketLiveLine.model

import com.google.firebase.firestore.PropertyName
import com.google.gson.annotations.Expose

data class BowlerList(
    @Expose
    @PropertyName("Team1List")
    var team1List: ArrayList<Bowlers>? = null,
    @Expose
    @PropertyName("Team2List")
    var team2List: ArrayList<Bowlers>? = null,
)

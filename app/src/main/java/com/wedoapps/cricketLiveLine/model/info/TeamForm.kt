package com.wedoapps.cricketLiveLine.model.info

import com.google.firebase.firestore.PropertyName
import com.google.gson.annotations.Expose

data class TeamForm(
    @Expose
    @PropertyName("NameTeam2")
    var nameTeam2: String? = "",
    @Expose
    @PropertyName("NameTeam1")
    var nameTeam1: String? = "",
    @Expose
    @PropertyName("FormTeam1")
    var formTeam1: String? = "",
    @Expose
    @PropertyName("FormTeam2")
    var formTeam2: String? = "",
)

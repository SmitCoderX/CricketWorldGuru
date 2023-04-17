package com.wedoapps.cricketLiveLine.model

import com.google.firebase.firestore.PropertyName
import com.google.gson.annotations.Expose

data class TeamScore(
    @Expose
    @PropertyName("PlayerScore")
    var playerScore: List<PlayerScore>? = null,
    @Expose
    @PropertyName("PlayerScore2")
    var playerScore2: List<PlayerScore>? = null,
)

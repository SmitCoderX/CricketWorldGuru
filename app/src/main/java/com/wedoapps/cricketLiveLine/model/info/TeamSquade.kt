package com.wedoapps.cricketLiveLine.model.info

import com.google.firebase.firestore.PropertyName
import com.google.gson.annotations.Expose
import com.wedoapps.cricketLiveLine.utils.Constants
import java.util.*

data class TeamSquade(
    @Expose
    @PropertyName("id")
    var id: String = generateID(),
    @Expose
    @PropertyName("Team2")
    var team2: String? = "",
    @Expose
    @PropertyName("Team1")
    var team1: String? = "",
) {
    companion object {
        private fun generateID(): String {
            return Constants.PLAYER + UUID.randomUUID().toString()
        }
    }
}

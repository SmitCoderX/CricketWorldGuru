package com.wedoapps.cricketLiveLine.model

import com.google.firebase.firestore.PropertyName
import com.google.gson.annotations.Expose
import com.wedoapps.cricketLiveLine.utils.Constants.REPO
import java.util.*

data class PlayerScore(
    var id: String = generateID(),
    @Expose
    @PropertyName("Ball")
    var ball: String? = "",
    @Expose
    @PropertyName("Run")
    var run: String? = "",
    @Expose
    @PropertyName("6s")
    var sixes: String? = "",
    @Expose
    @PropertyName("4s")
    var fours: String? = "",
    @Expose
    @PropertyName("OtherInfo")
    var otherInfo: String? = "",
    @Expose
    @PropertyName("Name")
    var name: String? = "",
    @Expose
    @PropertyName("SR")
    var sr: String? = "",
) {
    companion object {
        private fun generateID(): String {
            return REPO + UUID.randomUUID().toString()
        }
    }
}
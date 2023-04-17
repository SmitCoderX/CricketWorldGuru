package com.wedoapps.cricketLiveLine.model

import com.google.firebase.firestore.PropertyName
import com.google.gson.annotations.Expose
import com.wedoapps.cricketLiveLine.utils.Constants.BOWLER
import java.util.*

data class Bowlers(
    var id: String = generateID(),
    @Expose
    @PropertyName("Over")
    var over: String? = "",
    @Expose
    @PropertyName("Wicket")
    var wicket: String? = "",
    @Expose
    @PropertyName("Maiden")
    var maiden: String? = "",
    @Expose
    @PropertyName("Run")
    var run: String? = "",
    @Expose
    @PropertyName("ER")
    var er: String? = "",
    @Expose
    @PropertyName("Name")
    var name: String? = "",
) {


    companion object {
        private fun generateID(): String {
            return BOWLER + UUID.randomUUID().toString()
        }
    }
}
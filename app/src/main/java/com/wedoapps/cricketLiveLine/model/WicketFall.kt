package com.wedoapps.cricketLiveLine.model

import com.google.firebase.firestore.PropertyName
import com.google.gson.annotations.Expose
import com.wedoapps.cricketLiveLine.utils.Constants.WICKET
import java.util.*

data class WicketFall(
    var id: String = generateID(),
    @Expose
    @PropertyName("Name")
    var name: String? = "",
    @Expose
    @PropertyName("Over")
    var over: String? = "",
    @Expose
    @PropertyName("Score")
    var score: String? = "",
) {
    companion object {
        private fun generateID(): String {
            return WICKET + UUID.randomUUID().toString()
        }
    }
}

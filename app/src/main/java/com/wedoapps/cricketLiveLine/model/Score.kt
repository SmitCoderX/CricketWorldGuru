package com.wedoapps.cricketLiveLine.model

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import com.google.gson.annotations.Expose
import kotlinx.parcelize.Parcelize

@Parcelize
data class Score(
    @Expose
    @PropertyName("Over")
    var over: String? = "",
    @Expose
    @PropertyName("Score")
    var score: String? = "",
) : Parcelable
package com.wedoapps.cricketLiveLine.model

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import com.google.gson.annotations.Expose
import kotlinx.parcelize.Parcelize

@Parcelize
data class Session(
    @Expose
    @PropertyName("Min")
    val min: String? = "",
    @Expose
    @PropertyName("Max")
    val max: String? = "",
    @Expose
    @PropertyName("Complete")
    val complete: String? = "",
    @Expose
    @PropertyName("Name")
    val name: String? = "",
    @Expose
    @PropertyName("Open")
    val open: String? = "",
) : Parcelable

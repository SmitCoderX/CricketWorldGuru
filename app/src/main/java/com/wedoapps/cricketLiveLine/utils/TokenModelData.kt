package com.wedoapps.cricketLiveLine.utils

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class TokenModelData(

    @Expose
    @SerializedName("id")
    var id: Int = 0,

    @Expose
    @SerializedName("app_id")
    var appId: String? = null,

    @Expose
    @SerializedName("model_name")
    var modelName: String? = null,

    @Expose
    @SerializedName("os")
    var os: String? = null,

    @Expose
    @SerializedName("os_version")
    var osVersion: String? = null,

    @Expose
    @SerializedName("push_token")
    var pushToken: String? = null,

    @SerializedName("created_at")
    var createdAt: Date? = null

) : Parcelable

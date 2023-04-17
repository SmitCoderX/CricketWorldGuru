package com.wedoapps.cricketLiveLine.utils

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CommonResponse(
        @Expose
        @SerializedName("status")
        var status: Boolean?,
        @Expose
        @SerializedName("message")
        var message: String?,
        @Expose
        @SerializedName("token")
        var token: TokenModelData?
)



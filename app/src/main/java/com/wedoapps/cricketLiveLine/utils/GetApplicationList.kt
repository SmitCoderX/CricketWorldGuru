package com.wedoapps.cricketLiveLine.utils

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GetApplicationList(
    @SerializedName("apps")
    @Expose
    val appsArrayList: ArrayList<Apps>,
    @SerializedName("message")
    @Expose
    val message: String,
    @SerializedName("status")
    @Expose
    val status: Boolean
):Parcelable {

    constructor(parcel: Parcel):this(arrayListOf<Apps>().apply {
        parcel.readList(this, Apps::class.java.classLoader)
    },
        parcel.readString().toString(),parcel.readString() as Boolean)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(appsArrayList)
        parcel.writeString(message)
        parcel.writeByte(if (status) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GetApplicationList> {
        override fun createFromParcel(parcel: Parcel): GetApplicationList {
            return GetApplicationList(parcel)
        }

        override fun newArray(size: Int): Array<GetApplicationList?> {
            return arrayOfNulls(size)
        }
    }
}
package com.wedoapps.cricketLiveLine.utils

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Apps(
    @SerializedName("banner_ads")
    @Expose
    val banner_ads: String?="",
    @SerializedName("big_ads")
    @Expose
    val big_ads: String?="",
    @SerializedName("created_at")
    @Expose
    val created_at: String?="",
    @SerializedName("deleted_at")
    @Expose
    val deleted_at: String? = "",
    @SerializedName("id")
    @Expose
    val id: Int,
    @SerializedName("is_banner_ads_visible")
    @Expose
    val is_banner_ads_visible: Int,
    @SerializedName("is_big_ads_visible")
    @Expose
    val is_big_ads_visible: Int,
    @SerializedName("is_url")
    @Expose
    val is_url: Int,
    @SerializedName("message")
    @Expose
    val message: String?="",
    @SerializedName("name")
    @Expose
    val name: String?="",
    @SerializedName("phone")
    @Expose
    val phone: String?="",
    @SerializedName("telegram")
    @Expose
    val telegram: String?="",
    @SerializedName("updated_at")
    @Expose
    val updated_at: String?="",
    @SerializedName("url_name")
    @Expose
    val url_name: String?="",

    @SerializedName("contact_us")
    @Expose
    val contactUs: String?=""


) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(banner_ads)
        dest.writeString(big_ads)
        dest.writeString(created_at)
        dest.writeString(deleted_at)
        dest.writeInt(id)
        dest.writeInt(is_banner_ads_visible)
        dest.writeInt(is_big_ads_visible)
        dest.writeInt(is_url)
        dest.writeString(message)
        dest.writeString(name)
        dest.writeString(phone)
        dest.writeString(telegram)
        dest.writeString(updated_at)
        dest.writeString(url_name)
        dest.writeString(contactUs)
    }

    companion object CREATOR : Parcelable.Creator<Apps> {
        override fun createFromParcel(parcel: Parcel): Apps {
            return Apps(parcel)
        }

        override fun newArray(size: Int): Array<Apps?> {
            return arrayOfNulls(size)
        }
    }

}
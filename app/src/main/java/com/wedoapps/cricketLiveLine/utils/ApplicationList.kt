 package com.wedoapps.cricketLiveLine.utils

 import android.os.Parcel
 import android.os.Parcelable
 import com.google.gson.annotations.Expose
 import com.google.gson.annotations.SerializedName

 data class ApplicationList(
     @SerializedName("apps")
    @Expose
    val apps: Apps,
     @SerializedName("message")
    @Expose
    val message: String,
     @SerializedName("status")
    @Expose
    val status: Boolean
): Parcelable {

     constructor(parcel: Parcel):this(apps = parcel.readParcelable(Apps::class.java.classLoader)!!,
         parcel.readString().toString(),parcel.readString() as Boolean)

     override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(apps,flags)
         parcel.writeString(message)
         parcel.writeByte(if (status) 1 else 0)
     }

     override fun describeContents(): Int {
         return 0
     }

     companion object CREATOR : Parcelable.Creator<ApplicationList> {
         override fun createFromParcel(parcel: Parcel): ApplicationList {
             return ApplicationList(parcel)
         }

         override fun newArray(size: Int): Array<ApplicationList?> {
             return arrayOfNulls(size)
         }
     }

 }
package com.wedoapps.cricketLiveLine.model.sessionBet.declaresession

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(
    tableName = "AccountModel"
)

/**
 * Created by GAURAV on 20-11-2017.
 */
@Parcelize
data class AccountModel(
    @PrimaryKey(autoGenerate = true)
    var accountID: Int? = null,
    var id: String? = "",
    var partyName: String? = "",
    var matchID: Int? = null,
    var sessionID: String? = "",
    var isMatch: Int? = 0,
    var amount: Double? = 0.0,
    var commiAmount: Double? = 0.0,
) : Parcelable{

    @Ignore
    constructor() : this(0)
}
/*{

    constructor()
    constructor(
        accountID: Int,
        partyID: Int,
        partyName: String?,
        matchID: Int,
        sessionID: String?,
        isMatch: Int,
        amount: Double,
        commiAmount: Double
    ) {
        this.accountID = accountID
        this.partyID = partyID
        this.partyName = partyName
        this.matchID = matchID
        this.sessionID = sessionID
        this.isMatch = isMatch
        this.amount = amount
        this.commiAmount = commiAmount
    }

    protected constructor(`in`: Parcel) {
        accountID = `in`.readInt()
        partyID = `in`.readInt()
        partyName = `in`.readString()
        matchID = `in`.readInt()
        sessionID = `in`.readString()
        isMatch = `in`.readInt()
        amount = `in`.readDouble()
        commiAmount = `in`.readDouble()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(accountID)
        dest.writeInt(partyID)
        dest.writeString(partyName)
        dest.writeInt(matchID)
        dest.writeString(sessionID)
        dest.writeInt(isMatch)
        dest.writeDouble(amount)
        dest.writeDouble(commiAmount)
    }

    companion object {
        @JvmField val CREATOR: Creator<AccountModel?> = object : Creator<AccountModel?> {
            override fun createFromParcel(`in`: Parcel): AccountModel {
                return AccountModel(`in`)
            }

            override fun newArray(size: Int): Array<AccountModel?> {
                return arrayOfNulls(size)
            }
        }
    }
}*/
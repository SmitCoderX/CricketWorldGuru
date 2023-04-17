package com.wedoapps.cricketLiveLine.model

import com.google.firebase.firestore.PropertyName
import com.google.gson.annotations.Expose

data class AllWicketList(
    @Expose
    @PropertyName("WicketList1")
    var wicketList1: ArrayList<WicketFall>? = null,
    @Expose
    @PropertyName("WicketList2")
    var wicketList2: ArrayList<WicketFall>? = null,
)
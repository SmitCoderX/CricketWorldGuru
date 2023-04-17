package com.seriespanel.model

import android.os.Parcelable
import com.google.firebase.database.PropertyName
import com.google.gson.annotations.Expose
import com.wedoapps.cricketLiveLine.model.Series.MatchModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class SeriesModel(
    @Expose
    @PropertyName("match")
    var match: HashMap<String?, MatchModel?>? = HashMap(),

    @Expose
    @PropertyName("seriesFromDate")
    var seriesFromDate: String? = null,

    @Expose
    @PropertyName("seriesId")
    var seriesId: String? = "",

    @Expose
    @PropertyName("seriesName")
    var seriesName: String? = null,

    @Expose
    @PropertyName("seriesPriority")
    var seriesPriority: Long? = null,

    @Expose
    @PropertyName("seriesToDate")
    var seriesToDate: String? = null,

    @Expose
    @PropertyName("totalMatches")
    var totalMatches: String? = null,

    @Expose
    @PropertyName("pointTbl")
    var pointTbl: HashMap<String?, PointTableModel?>? = HashMap(),


    @Expose
    @PropertyName("year")
    var year: String? = null,

    ) : Parcelable
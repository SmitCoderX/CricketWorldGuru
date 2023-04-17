package com.wedoapps.cricketLiveLine.model.info

import com.google.firebase.firestore.PropertyName
import com.google.gson.annotations.Expose

data class Info (
    @Expose
    @PropertyName("id")
    var id: String? = "",
    @Expose
    @PropertyName("LowestTotal")
    var lowestTotal: String? = "",
    @Expose
    @PropertyName("HighestTotal")
    var highestTotal: String? = "",
    @Expose
    @PropertyName("IsToss")
    var isToss: Boolean? = false,
    @Expose
    @PropertyName("TeamSquade")
    var teamSquade: TeamSquade? = null,
    @Expose
    @PropertyName("NoOfMatch")
    var noOfMatch: String? = "",
    @Expose
    @PropertyName("ThirdUmpire")
    var thirdUmpire: String? = "",
    @Expose
    @PropertyName("MatchResult")
    var matchResult: String? = "",
    @Expose
    @PropertyName("MatchStatus")
    var matchStatus: String? = "",
    @Expose
    @PropertyName("Team2")
    var team2: String? = "",
    @Expose
    @PropertyName("Team1")
    var team1: String? = "",
    @Expose
    @PropertyName("HighestChased")
    var highestChased: String? = "",
    @Expose
    @PropertyName("MatchDetail")
    var matchDetail: String? = "",
    @Expose
    @PropertyName("Avg2ndInnings")
    var avg2ndInnings: String? = "",
    @Expose
    @PropertyName("CurrentDate")
    var currentDate: String? = "",
    @Expose
    @PropertyName("Avg1stInnings")
    var avg1stInnings: String? = "",
    @Expose
    @PropertyName("TossInfo")
    var tossInfo: String? = "",
    @Expose
    @PropertyName("IsShowFlag")
    var isShowFlag: Boolean? = false,
    @Expose
    @PropertyName("Venue")
    var venue: String? = "",
    @Expose
    @PropertyName("Umpire")
    var umpire: String? = "",
    @Expose
    @PropertyName("Referee")
    var referee: String? = "",
    @Expose
    @PropertyName("MOM")
    var mom: String? = "",
    @Expose
    @PropertyName("LowestDefended")
    var lowestDefended: String? = "",
    @Expose
    @PropertyName("Head2Head")
    var head2Head: String? = "",
    @Expose
    @PropertyName("Series")
    var series: String? = "",
    @Expose
    @PropertyName("TeamForm")
    var teamForm: TeamForm? = null,
)

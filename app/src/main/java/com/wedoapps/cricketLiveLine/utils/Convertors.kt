package com.wedoapps.cricketLiveLine.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Convertors {
    @TypeConverter
    fun toString(json: String): ArrayList<String> {
        val type = object : TypeToken<ArrayList<String>>() {}.type
        return Gson().fromJson(json, type)
    }

    @TypeConverter
    fun toList(data: ArrayList<String>): String {
        val type = object : TypeToken<ArrayList<String>>() {}.type
        return Gson().toJson(data, type)
    }

    /*   @TypeConverter
       fun toSessionBet(json: String): List<SessionBet> {
           val type = object : TypeToken<List<SessionBet>>() {}.type
           return Gson().fromJson(json, type)
       }

       @TypeConverter
       fun toSessionJson(data: List<SessionBet>): String {
           val type = object : TypeToken<List<SessionBet>>() {}.type
           return Gson().toJson(data, type)
       }

       @TypeConverter
       fun toSessionData(json: String): List<SessionData> {
           val type = object : TypeToken<List<SessionData>>() {}.type
           return Gson().fromJson(json, type)
       }

       @TypeConverter
       fun toSessionDataJson(data: List<SessionData>): String {
           val type = object : TypeToken<List<SessionData>>() {}.type
           return Gson().toJson(data, type)
       }*/
}
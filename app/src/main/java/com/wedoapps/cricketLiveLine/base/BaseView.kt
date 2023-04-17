package com.wedoapps.cricketLiveLine.base

interface BaseView {
    fun internalServer()

    fun onUnknownError(error: String?)

    fun onTimeout()

    fun onNetworkError()

    fun onConnectionError()
}
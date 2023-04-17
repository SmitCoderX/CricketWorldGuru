package com.wedoapps.cricketLiveLine.utils

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wedoapps.cricketLiveLine.repository.CricketGuruRepository
import com.wedoapps.cricketLiveLine.ui.CricketGuruViewModel


class ViewModelProviderFactory(
    private val app: Application,
    private val repository: CricketGuruRepository,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CricketGuruViewModel(app, repository) as T
    }
}
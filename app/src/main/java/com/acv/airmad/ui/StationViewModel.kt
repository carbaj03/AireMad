package com.acv.airmad.ui

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.acv.airmad.data.StationRepository
import com.acv.airmad.data.mapper
import com.acv.airmad.ui.search.Station


class StationViewModel(
        private val repo: StationRepository = StationRepository()
) : ViewModel() {
    private lateinit var stations: MutableLiveData<List<Station>>

    fun getStations(): LiveData<List<Station>> {
        stations = MutableLiveData()
        stations.value = repo.loadStations().execute().body()!!.mapper()
        return stations
    }
}
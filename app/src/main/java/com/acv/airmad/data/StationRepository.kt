package com.acv.airmad.data


class StationRepository(
        private val service: Service = retrofit(client = client())
) {
    fun loadStations() = service.allStations()
}
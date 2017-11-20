package com.acv.airmad.data

import com.acv.airmad.ui.search.Station
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


fun client(): () -> OkHttpClient.Builder = {
    val logging = HttpLoggingInterceptor()
    logging.level = HttpLoggingInterceptor.Level.BODY
    val httpClient = OkHttpClient.Builder()
    httpClient.addInterceptor(logging)
}

fun retrofit(url: String = "http://airemad.com/api/", client: () -> OkHttpClient.Builder): Service =
        Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client().build())
                .build()
                .create(Service::class.java)

interface Service {
    @GET("v1/station")
    fun allStations(): Call<List<StationResponse>>
}

data class StationResponse(
        @SerializedName("id") val id: String,
        @SerializedName("altitud") val altitude: Int,
        @SerializedName("direccion") val address: String,
//        @SerializedName("latitud") latitude: String,
        @SerializedName("latitud_decimal") val latitude: Float,
//        @SerializedName("longitud")
        @SerializedName("longitud_decimal") val longitude: Float,
        @SerializedName("nombre_estacion") val name: String,
//        @SerializedName("numero_estacion") val number: List<Int>,
        @SerializedName("tipo_estacion") val type: Int
//        @SerializedName("datos_disponibles") val data: StationDataResponse
)

data class StationDataResponse(
        @SerializedName("acustic") val acustic: List<String>,
        @SerializedName("pollution") val pollution: Boolean,
        @SerializedName("pollen") val pollen: List<String>
)

fun StationResponse.mapper() = Station(id, name, latitude, longitude)
fun List<StationResponse>.mapper() = map { it.mapper() }
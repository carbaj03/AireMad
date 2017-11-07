package com.acv.airmad

import android.content.Context
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


/**
 * Created by alejandro on 7/11/17.
 */

fun Context.client(): () -> OkHttpClient.Builder =  {
    val logging = HttpLoggingInterceptor()
    logging.level = HttpLoggingInterceptor.Level.BODY
    val httpClient = OkHttpClient.Builder()
    httpClient.addInterceptor(logging)
}
fun Context.retrofit(a: () -> OkHttpClient.Builder) = Retrofit.Builder()
        .baseUrl("http://airemad.com/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(a().build())
        .build()
        .create(AirMadService::class.java)

interface AirMadService {
    @GET("v1/station")
    fun allStations(): Call<List<Station>>
}

data class Station(
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
//        @SerializedName("datos_disponibles") val data: StationData
)

data class StationData(
        @SerializedName("acustic") val acustic: List<String>,
        @SerializedName("pollution") val pollution: Boolean,
        @SerializedName("pollen") val pollen: List<String>
)

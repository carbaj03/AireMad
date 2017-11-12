package com.acv.airmad

import android.content.Context
import android.os.StrictMode
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

infix fun ViewGroup.inflate(res: Int) =
        LayoutInflater.from(context).inflate(res, this, false)

fun Context.color(color: Int) =
        ContextCompat.getColor(this, color)

fun Context.strictMode() =
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())

fun Context.inputMethodManager() =
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager


fun AppCompatActivity.actionbar() = supportActionBar!!.apply {
    setDisplayShowTitleEnabled(true)
    setDisplayHomeAsUpEnabled(true)
    title = ""
}

infix fun View.focusChange(f: (view: View, b: Boolean) -> Unit) {
    onFocusChangeListener = View.OnFocusChangeListener(f)
}

infix fun View.click(f: () -> Unit) =
        setOnClickListener { f() }

fun Context.client(): () -> OkHttpClient.Builder = {
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

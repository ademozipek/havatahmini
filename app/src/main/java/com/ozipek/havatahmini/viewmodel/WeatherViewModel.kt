package com.ozipek.havatahmini.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.*
import com.google.gson.GsonBuilder
import com.ozipek.havatahmini.model.Data
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

class WeatherViewModel : ViewModel() {

    private lateinit var job : Job

    private val URL : String = "https://api.openweathermap.org/data/2.5/weather"
    private val API_KEY : String = "c75d86551aff588b8362b085f0fb5234"

    private var weatherURL : String

    private var _lat = MutableLiveData<String>()
    var lat = MutableLiveData<String>()
        get() = _lat

    private var _lon = MutableLiveData<String>()
    var lon = MutableLiveData<String>()
        get() = _lon

    private val _weatherData = MutableLiveData<Data>()
    val weatherData : MutableLiveData<Data>
        get() = _weatherData

    private lateinit var locationCallback: LocationCallback

    private var language : String

    init{
        job = Job()
        weatherURL = ""
        language = Locale.getDefault().toLanguageTag().toString().take(2)
    }

    @SuppressLint("MissingPermission")
    fun getLocation(locationRequest: LocationRequest, fusedLocationClient: FusedLocationProviderClient, context:Context){
        //Güncel konum bilgisini dinlemeye başlıyoruz.
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations){
                    /*Gelen koordinatı kısaltıyorum. API'de uzun bir şekilde verince hata aldım.
                    Matematiksel işlem hatasını önlemek için String olarak ilk 5 karateri aldım.
                    41.10 veya 28.99 şeklinde geliyor koordinatlar*/
                    _lat.value = location.latitude.toString().take(5)
                    _lon.value = location.longitude.toString().take(5)
                    getWeatherData(context)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper())

    }

    private fun getWeatherData(context:Context){
        job = GlobalScope.launch(Dispatchers.IO){
            weatherURL = "$URL?lat=${lat.value}" +
                    "&lon=${lon.value}" +
                    "&appid=$API_KEY" +
                    "&units=metric" +
                    "&lang=$language"
            val stringRequest = StringRequest(Request.Method.POST,weatherURL, {
                val gsonBuilder = GsonBuilder()
                val gson = gsonBuilder.create()
                var cut = it.toString().replace("[","")
                cut = cut.replace("]","")
                /*API'dan gelen veride [ ] alanları bulunuyor
                bu da gson converterda hataya sebep oluyor, o yüzden replace kullandım*/
                _weatherData.value = gson.fromJson("$cut", Data::class.java)
            }, {
                Log.e("WeatherViewModel", it.localizedMessage)
            })

            var requestQueue = Volley.newRequestQueue(context)
            requestQueue.add(stringRequest)
        }

    }

    fun contactUs(context : Context){
        val sendIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:ademozipek@gmail.com"))
        startActivity(context,sendIntent,null)
    }

}

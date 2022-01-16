package com.ozipek.havatahmini

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import com.ozipek.havatahmini.databinding.ActivityMainBinding
import com.ozipek.havatahmini.viewmodel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.*

private lateinit var binding : ActivityMainBinding

private lateinit var viewModel: WeatherViewModel

private lateinit var fusedLocationClient: FusedLocationProviderClient

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //viewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        //Hava durumu verileri yüklenene kadar yüklenme gif'ini oynatıyorum.
        Glide
            .with(this)
            .load(R.drawable.loading_anim)
            .into(binding.animationImage)

        //ViewModel tanımlıyoruz
        viewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)

        viewModel.weatherData.observe(this, Observer {
            binding.locationText.text = it.name
            binding.weatherStatusText.text = it.weather?.description.toString().replaceFirstChar {
                it.uppercase()
            }
            binding.degreeText.text = "${it.main?.temp?.toInt().toString()}°C"
            binding.feelsLikeValue.text = "${it.main?.feelsLike?.toInt().toString()}°C"
            binding.humidityValue.text = "${it.main?.humidity?.toInt().toString()}%"
            binding.dateText.text = SimpleDateFormat("dd MMMM yyyy EEEE").format(Date())
            changeAnimation(it.weather?.id?.toInt())
        })


        //Konum izni görünümle alakalı olduğunu düşündüğümden burada istedim.
        askPermission()


    }

    private fun changeAnimation(id: Int?) {
        when(id){
            in 200..299 -> Glide
                .with(this)
                .load(R.drawable.storm)
                .into(binding.animationImage)
            in 300..399 -> Glide
                .with(this)
                .load(R.drawable.drizzle)
                .into(binding.animationImage)
            in 500..599 -> Glide
                .with(this)
                .load(R.drawable.rain)
                .into(binding.animationImage)
            in 600..699 -> Glide
                .with(this)
                .load(R.drawable.snow)
                .into(binding.animationImage)
            in 700..799 -> Glide
                .with(this)
                .load(R.drawable.fog)
                .into(binding.animationImage)
            800 -> Glide
                .with(this)
                .load(R.drawable.sunny)
                .into(binding.animationImage)
            in 801..899 -> Glide
                .with(this)
                .load(R.drawable.cloudly)
                .into(binding.animationImage)
        }
    }

    private fun askPermission(){
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                   requestLocation()
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                   requestLocation()
                } else -> {
                // Eğer reddederse uygulama izin ayar ekranına göndermesi için Snackbar gösteriyoruz.
                binding.weatherStatusText.text = resources.getText(R.string.permissionError)
                Snackbar.make(binding.root,getString(R.string.locationPermissionText),Snackbar.LENGTH_INDEFINITE).setAction(getString(
                                    R.string.snackbarButton), View.OnClickListener {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package",packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }).show()

            }
            }
        }

        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION))
    }


    /*İzni kod kalabalığı olmasın diye yukarıda istediğim için
    burada izin sormasını SuppressLint ile es geçiyoruz*/
    @SuppressLint("MissingPermission")
    fun requestLocation(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        /* Son bilinen konum istenirse

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                //Bilinen son konum. Boş da döndürebilir.
            }*/

        val locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }


        viewModel.getLocation(locationRequest!!,fusedLocationClient, this)
    }


    fun contactUs(view: View){
        viewModel.contactUs(this)
    }
}
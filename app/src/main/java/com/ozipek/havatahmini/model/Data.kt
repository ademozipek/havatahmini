package com.ozipek.havatahmini.model

import com.google.gson.annotations.SerializedName
import com.ozipek.havatahmini.viewmodel.*

data class Data (

    @SerializedName("coord") var coord : Coord? = null,
    @SerializedName("weather") var weather : Weather?    = null,
    @SerializedName("base") var base  : String? = null,
    @SerializedName("main") var main : Main? = null,
    @SerializedName("visibility") var visibility : String? = null,
    @SerializedName("wind") var wind : Wind? = null,
    @SerializedName("clouds") var clouds : Clouds? = null,
    @SerializedName("dt") var dt : String? = null,
    @SerializedName("sys") var sys : Sys? = null,
    @SerializedName("timezone") var timezone  : String? = null,
    @SerializedName("id") var id : String? = null,
    @SerializedName("name") var name : String? = null,
    @SerializedName("cod") var cod : String? = null

)
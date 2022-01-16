package com.ozipek.havatahmini.viewmodel

import com.google.gson.annotations.SerializedName


data class Sys (

  @SerializedName("type" ) var type : String? = null,
  @SerializedName("id" ) var id : String? = null,
  @SerializedName("country" ) var country : String? = null,
  @SerializedName("sunrise" ) var sunrise : Int?    = null,
  @SerializedName("sunset"  ) var sunset  : Int?    = null

)
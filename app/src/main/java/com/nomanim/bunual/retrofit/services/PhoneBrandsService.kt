package com.nomanim.bunual.retrofit.services

import com.nomanim.bunual.retrofit.listModels.PhoneBrandsList
import retrofit2.Call
import retrofit2.http.GET

interface PhoneBrandsService {

    @GET("iamasaf-profile/bunual-api-collection/master/brands.json")
    fun getData() : Call<PhoneBrandsList>
}
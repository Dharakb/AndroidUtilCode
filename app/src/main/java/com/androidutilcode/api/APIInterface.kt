package com.androidutilcode.api

import com.androidutilcode.models.MasterDataResponse
import retrofit2.Call
import retrofit2.http.GET

/**
 * Created by Dharak Bhatt on 2/12/19.
 * @author Dharak Bhatt
 */
interface APIInterface {

    @GET(APIEndpoints.GET_MASTER_DATA)
    fun getMasterData(): Call<MasterDataResponse>

}
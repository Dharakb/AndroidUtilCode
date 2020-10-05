package com.androidutilcode.api

import com.androidutilcode.models.MasterDataResponse

/**
 * Created by Dharak Bhatt on 2/12/19.
 * @author Dharak Bhatt
 */
class APIRepository {
    private var client: APIInterface = RetrofitClient.webservice.create(APIInterface::class.java)

    fun getMasterData() = NetworkCall<MasterDataResponse>().makeCall(client.getMasterData())
}
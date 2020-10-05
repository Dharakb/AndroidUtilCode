package com.androidutilcode.api

import androidx.lifecycle.MutableLiveData
import com.androidutilcode.models.CommonResponse
import retrofit2.Call
import retrofit2.Callback
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Created by Dharak Bhatt on 2/12/19.
 * @author Dharak Bhatt
 */
open class NetworkCall<T : CommonResponse> {
    lateinit var call: Call<T>

    fun makeCall(call: Call<T>): MutableLiveData<ResponseUtils<T>> {
        this.call = call
        val callBackKt = CallBackKt<T>()
        this.call.clone().enqueue(callBackKt)
        return callBackKt.result
    }

    class CallBackKt<T : CommonResponse> : Callback<T> {
        var result: MutableLiveData<ResponseUtils<T>> = MutableLiveData()

        override fun onFailure(call: Call<T>, t: Throwable) {
            when (t) {
                is UnknownHostException -> result.value =
                    ResponseUtils.error(CommonResponse(message = "Unable to resolve host"))
                is SocketTimeoutException -> result.value =
                    ResponseUtils.error(CommonResponse(message = "Connection timed out"))
                else -> result.value = ResponseUtils.error(CommonResponse())
            }
            t.printStackTrace()
        }

        override fun onResponse(call: Call<T>, response: retrofit2.Response<T>) {
            result.value = when {
                response.isSuccessful -> ResponseUtils.success(response.body())
                response.code() == 401 -> {
                    ResponseUtils.unauthorized(ErrorUtils.parseError(response))
                }
                else -> {
                    ResponseUtils.error(ErrorUtils.parseError(response))
                }
            }
        }
    }
}
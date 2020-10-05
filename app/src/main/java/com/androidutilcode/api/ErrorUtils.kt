package com.androidutilcode.api

import com.androidutilcode.models.CommonResponse
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response
import java.io.IOException

object ErrorUtils {
    fun parseError(response: Response<*>): CommonResponse? {
        val converter: Converter<ResponseBody, CommonResponse> =
            RetrofitClient.webservice.responseBodyConverter(
                CommonResponse::class.java,
                arrayOfNulls<Annotation>(0)
            )
        val error: CommonResponse?
        error = try {
            converter.convert(response.errorBody())
        } catch (e: IOException) {
            e.printStackTrace()
            return CommonResponse()
        }
        return error
    }
}
package com.androidutilcode.api

import android.content.Context
import com.androidutilcode.R
import com.androidutilcode.models.CommonResponse
import com.androidutilcode.utils.DialogUtils

class ResponseUtils<T : CommonResponse> constructor(
    val status: Status,
    val data: T?,
    val apiError: CommonResponse?
) {

    companion object {
        enum class Status {
            SUCCESS, UNAUTHORIZED, ERROR
        }

        fun <T : CommonResponse> success(data: T?): ResponseUtils<T> {
            return ResponseUtils(Status.SUCCESS, data, null)
        }

        fun <T : CommonResponse> error(apiError: CommonResponse?): ResponseUtils<T> {
            return ResponseUtils(Status.ERROR, null, apiError)
        }

        fun <T : CommonResponse> unauthorized(apiError: CommonResponse?): ResponseUtils<T> {
            return ResponseUtils(Status.UNAUTHORIZED, null, apiError)
        }

        fun <T : CommonResponse> checkIfValidResponse(
            context: Context,
            it: ResponseUtils<T>,
            showFailureDialog: Boolean = true
        ): Status {
            if (it.data == null && it.status != Status.UNAUTHORIZED) {
                DialogUtils.showDialog(
                    context,
                    message = it.apiError?.message
                        ?: context.getString(R.string.something_went_wrong),
                    positiveButtonText = context.getString(R.string.ok)
                )
                return Status.ERROR
            }

            if (it.status == Status.UNAUTHORIZED) {
                it.apiError?.message?.let { it1 ->
                    DialogUtils.showUnauthorisedDialog(
                        context,
                        it1
                    )
                }
                return Status.UNAUTHORIZED
            }

            return if (it.data?.success == 1) {
                Status.SUCCESS
            } else {
                if (showFailureDialog)
                    DialogUtils.showDialog(
                        context,
                        message = it.data?.message
                            ?: context.getString(R.string.something_went_wrong),
                        positiveButtonText = context.getString(
                            R.string.ok
                        )
                    )
                Status.ERROR
            }
        }
    }
}
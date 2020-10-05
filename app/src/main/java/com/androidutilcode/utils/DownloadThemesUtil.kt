package com.androidutilcode.utils

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

class DownloadThemesUtil {

    companion object {
        private var downloadProgress: Int = 0

        fun startDownload(context: Context, downloadLink: String) {
            var input: InputStream? = null
            var output: OutputStream? = null
            var connection: HttpURLConnection? = null
            val outputFile: File?

            try {
                val url = URL(downloadLink)
                connection = url.openConnection() as HttpURLConnection
                connection.connect()

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                    Log.e(
                        "Download Error",
                        "Server returned HTTP " + connection.responseCode + " " + connection.responseMessage
                    )
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                val fileLength = connection.contentLength

                // download the file
                input = connection.inputStream

                // TODO Change FileName
                outputFile = File(CommonUtils.getPrivateDownloadsDir(context), "tempFile")
                if (outputFile.exists()) {
                    return
                }
                output = FileOutputStream(outputFile)

                val data = ByteArray(4096)
                var total: Long = 0
                var count = input!!.read(data)

                while (count != -1) {
                    total += count.toLong()
                    if (fileLength > 0)
                        downloadProgress = (total * 80 / fileLength).toInt()
                    Log.d("Downloading", downloadProgress.toString())
                    output.write(data, 0, count)
                    count = input.read(data)
                }
            } catch (e: Exception) {
                Log.e("downloading", "error", e)
            } finally {
                try {
                    output?.close()
                    input?.close()
                } catch (ignored: Exception) {
                    Log.e("downloading", "error", ignored)
                }
                connection?.disconnect()
            }
        }
    }
}
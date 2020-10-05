package com.androidutilcode.utils

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns

class FetchImagePath {

    companion object {

        @TargetApi(Build.VERSION_CODES.KITKAT)
        fun getPath(context: Context, uri: Uri, data: Intent): String? {

            val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
            val isQ = Build.VERSION.SDK_INT > Build.VERSION_CODES.Q

            if (isQ) {
                var result: String? = null

                if (uri.scheme == "content") {
                    val cursor = context.contentResolver.query(uri, null, null, null, null)

                    try {
                        if (cursor != null && cursor.moveToFirst()) {
                            result =
                                cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        cursor?.close()
                    }
                }

                if (result == null) {
                    result = uri.path
                    val cut = result?.lastIndexOf('/')
                    if (cut != -1) {
                        if (result != null) {
                            if (cut != null) {
                                result = result.substring(cut + 1)
                            }
                        }
                    }
                }

                val cursor: Cursor? = context.contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null, null, null, null
                )

                if (cursor != null) {
                    var path: String?

                    if (cursor.moveToFirst()) {
                        do {
                            path =
                                cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))

                            if (path.contains(result.toString())) {
                                return path
                            }
                        } while (cursor.moveToNext())
                    }

                    cursor.close()
                }
            } else if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                if (isExternalStorageDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)

                    val split =
                        docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                    val type = split[0]

                    if ("primary".equals(type, ignoreCase = true)) {
                        return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                    }
                } else if (isDownloadsDocument(uri)) {
                    val id = DocumentsContract.getDocumentId(uri)

                    if (id.startsWith("raw:"))
                        return id.replaceFirst("raw:", "")

                    if (id.startsWith("msf:")) {
                        val cursor: Cursor? = context.contentResolver.query(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            null,
                            null,
                            null,
                            null
                        )

                        if (cursor != null) {
                            var path: String? = null

                            if (cursor.moveToFirst()) {
                                do {
                                    path =
                                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))

                                } while (cursor.moveToNext())
                            }
                            cursor.close()

                            return path
                        }
                    }

                    /**
                     * Old Method*/

                    /*val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        java.lang.Long.valueOf(id)
                    )

                    return getDataColumn(context, contentUri, null, null)*/

                    return getPatFromDownloads(uri, context)
                } else if (isMediaDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)

                    val split =
                        docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                    val type = split[0]

                    var contentUri: Uri? = null

                    when (type) {
                        "image" -> {
                            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        }
                        "video" -> {
                            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        }
                        "audio" -> {
                            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                        }
                    }

                    val selection = "_id=?"
                    val selectionArgs = arrayOf(split[1])

                    return getDataColumn(context, contentUri, selection, selectionArgs)
                }
            } else if ("content".equals(uri.scheme!!, ignoreCase = true)) {
                return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(
                    context,
                    uri,
                    null,
                    null
                )
            } else if ("file".equals(uri.scheme!!, ignoreCase = true)) {
                return uri.path
            }

            return null
        }

        private fun getPatFromDownloads(uri: Uri, context: Context): String? {
            var result: String? = null

            if (uri.scheme == "content") {
                val cursor = context.contentResolver.query(uri, null, null, null, null)

                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        result =
                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    cursor?.close()
                }
            }

            if (result == null) {
                result = uri.path

                val cut = result?.lastIndexOf('/')

                if (cut != -1)
                    if (result != null) {
                        if (cut != null) {
                            result = result.substring(cut + 1)
                        }
                    }
            }

            val cursor: Cursor? = context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                null
            )

            if (cursor != null) {
                var path: String?

                if (cursor.moveToFirst()) {
                    do {
                        path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))

                        if (path.contains(result.toString())) {
                            return path
                        }
                    } while (cursor.moveToNext())
                }

                cursor.close()
            }

            return null
        }

        private fun getDataColumn(
            context: Context,
            uri: Uri?,
            selection: String?,
            selectionArgs: Array<String>?
        ): String? {

            var cursor: Cursor? = null
            val column = "_data"
            val projection = arrayOf(column)

            try {
                cursor = uri?.let {
                    context.contentResolver.query(it, projection, selection, selectionArgs, null)
                }

                if (cursor != null && cursor.moveToFirst()) {
                    val index = cursor.getColumnIndexOrThrow(column)
                    return cursor.getString(index)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                cursor?.close()
            }
            return null
        }

        private fun isExternalStorageDocument(uri: Uri): Boolean {
            return "com.android.externalstorage.documents" == uri.authority
        }

        private fun isDownloadsDocument(uri: Uri): Boolean {
            return "com.android.providers.downloads.documents" == uri.authority
        }

        private fun isMediaDocument(uri: Uri): Boolean {
            return "com.android.providers.media.documents" == uri.authority
        }

        private fun isGooglePhotosUri(uri: Uri): Boolean {
            return "com.google.android.apps.photos.content" == uri.authority
        }
    }
}
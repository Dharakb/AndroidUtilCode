package com.androidutilcode.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import com.androidutilcode.R
import kotlinx.android.synthetic.main.dialog_image_select.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

/**
 * Created by Dharak Bhatt on 2/12/19.
 * @author Dharak Bhatt
 */
class ImageSelectionUtils {

    companion object {

        const val GALLERY_REQUEST_CODE = 100
        const val CAMERA_REQUEST_CODE = 200

        fun imageSelectionOptionDialog(context: Context): Dialog {
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.dialog_image_select)
            dialog.setCancelable(true)
            dialog.setCanceledOnTouchOutside(false)
            val window = dialog.window
            if (window != null) {
                window.setBackgroundDrawableResource(android.R.color.transparent)
                window.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            dialog.tv_take_photo_label.setOnClickListener {
                capturePicture(context, CAMERA_REQUEST_CODE)
                dialog.dismiss()
            }
            dialog.tv_choose_photo_label.setOnClickListener {
                choosePhotoFromGallery(context)
                dialog.dismiss()
            }
            dialog.tv_cancel_label.setOnClickListener {
                dialog.dismiss()
            }
            return dialog
        }

        fun capturePicture(context: Context, cameraCaptureCode: Int) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val f = File(CommonUtils.getPrivateAlbumStorageDir(context), "temp.jpg")
            intent.putExtra(
                MediaStore.EXTRA_OUTPUT,
                FileProvider.getUriForFile(
                    context,
                    context.applicationContext.packageName + ".provider",
                    f
                )
            )
            try {
                (context as Activity).startActivityForResult(intent, cameraCaptureCode)
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    context.getString(R.string.no_app_found),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        private fun choosePhotoFromGallery(context: Context) {
            val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            try {
                (context as Activity).startActivityForResult(intent, GALLERY_REQUEST_CODE)
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    context.getString(R.string.no_app_found),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        fun getImagePathFromGallery(context: Context, data: Intent): String? {
            val selectedImage = data.data
            return selectedImage?.let { FetchImagePath.getPath(context, it, data) }
        }

        fun captureImageFromCamera(context: Context, saveToFile: Boolean): String {
            if (saveToFile) {
                val rootDir = CommonUtils.getPrivateAlbumStorageDir(context)

                val file = File(rootDir, System.currentTimeMillis().toString() + ".jpg")
                try {
                    file.createNewFile()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                var f = File(rootDir)
                for (temp in f.listFiles()) {
                    if (temp.name == "temp.jpg") {
                        f = temp
                        break
                    }
                }

                var bitmap: Bitmap

                try {
                    val bitmapOptions = BitmapFactory.Options()
                    bitmap = BitmapFactory.decodeFile(
                        f.absolutePath,
                        bitmapOptions
                    )
                    bitmap = scaleBitmapWithAspectRatio(bitmap, 1000, 1000)
                    bitmap = rotateImageIfRequired(bitmap, f.absolutePath)
                    val outFile: OutputStream
                    try {
                        outFile = FileOutputStream(file)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outFile)
                        outFile.flush()
                        outFile.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                f.delete()
                return file.absolutePath
            }

            var f = File(Environment.getExternalStorageDirectory().toString())
            for (temp in f.listFiles()) {
                if (temp.name == "temp.jpg") {
                    f = temp
                    break
                }
            }

            return f.absolutePath
        }

        fun calculateInSampleSize(
            options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int
        ): Int {
            // Raw height and width of image
            val height = options.outHeight
            val width = options.outWidth
            var inSampleSize = 1

            if (height > reqHeight || width > reqWidth) {

                val halfHeight = height / 2
                val halfWidth = width / 2

                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                    inSampleSize *= 2
                }
            }

            return inSampleSize
        }

        fun scaleBitmapWithAspectRatio(bm: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
            var bm = bm
            var width = bm.width
            var height = bm.height

            Log.v("Pictures", "Width and height are $width--$height")

            if (width > maxWidth || height > maxHeight) {
                when {
                    width > height -> {
                        // landscape
                        val ratio = width.toFloat() / maxWidth
                        width = maxWidth
                        height = (height / ratio).toInt()
                    }
                    height > width -> {
                        // portrait
                        val ratio = height.toFloat() / maxHeight
                        height = maxHeight
                        width = (width / ratio).toInt()
                    }
                    else -> {
                        // square
                        height = maxHeight
                        width = maxWidth
                    }
                }
            }

            Log.v("Pictures", "after scaling Width and height are $width--$height")

            bm = Bitmap.createScaledBitmap(bm, width, height, true)
            return bm
        }

        fun rotateImageIfRequired(result: Bitmap, imagePath: String): Bitmap {
            var result = result
            var ei: ExifInterface? = null
            try {
                ei = ExifInterface(imagePath)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            if (ei == null)
                return result

            val orientation = ei.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )

            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> result = rotateImage(result, 90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> result = rotateImage(result, 180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> result = rotateImage(result, 270f)
            }

            return result
        }

        private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(angle)
            return Bitmap.createBitmap(
                source, 0, 0, source.width, source.height,
                matrix, true
            )
        }

    }
}
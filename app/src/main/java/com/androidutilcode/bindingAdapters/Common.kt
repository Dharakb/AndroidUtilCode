package com.androidutilcode.bindingAdapters

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.androidutilcode.utils.BitmapUtils
import com.squareup.picasso.Picasso

/**
 * Created by Dharak Bhatt on 2/12/19.
 * @author Dharak Bhatt
 */

@BindingAdapter(value = ["placeholder", "imageUrl", "isThumbnail", "imageSize"], requireAll = false)
fun loadImage(
    imgView: ImageView,
    placeholder: Drawable?,
    imageUrl: String?,
    isThumbnail: Boolean = false,
    imageSize: Int = 0
) {

    if (!imageUrl.isNullOrBlank()) {
        Picasso.get().apply {
            load(imageUrl).apply {
                if (placeholder != null)
                    placeholder(placeholder)
                if (isThumbnail)
                    transform(BitmapUtils.getTransformation(BitmapUtils.SIZE_THUMBNAIL))
                else
                    if (imageSize > 0)
                        transform(BitmapUtils.getTransformation(imageSize))
                into(imgView)
            }
        }
    } else {
        placeholder?.let {
            imgView.setImageDrawable(it)
        }
    }
}

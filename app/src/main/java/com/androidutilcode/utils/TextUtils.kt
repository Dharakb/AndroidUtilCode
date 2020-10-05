package com.androidutilcode.utils

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.TextView
import androidx.core.content.ContextCompat

/**
 * Created by Dharak Bhatt on 2/12/19.
 * @author Dharak Bhatt
 */
class TextUtils {
    companion object {

        fun formatWebViewText(string: String): String {

            return "<html xmlns=\"http://www.w3.org/1999/xhtml\">" +
                    "<meta charset=\"UTF-8\">" +
                    "<head>" +
                    "    <style>" +
                    "    @font-face {" +
                    "            font-family: MyFont;" +
                    "            src: url(\"file:///android_asset/fonts/sf_pro_display_regular.otf\")" +
                    "    }" +
                    "    body{" +
                    "            background-color:#AARRGGBB;" +
                    "            margin: 0px;" +
                    "            padding:8px;" +
                    "            font-family: MyFont;" +
                    "            text-align: justify;" +
                    "            color:#000000;" +
                    "            font-size: 16;" +
                    "    }" +
                    "    p {" +
                    "            word-wrap: break-word;" +
                    "    }" +
                    "    </style>" +
                    "</head>" +
                    "<body>" +
                    "    <p>" +
                    string +
                    "    </p>" +
                    "</body>" +
                    "</html>"
        }

        fun makeLinks(
            context: Context,
            textView: TextView,
            links: Array<String>,
            clickableSpans: Array<ClickableSpan>,
            textColor: Int = 0
        ) {
            val spannableString = SpannableString(textView.text)
            for (i in links.indices) {
                val clickableSpan = clickableSpans[i]
                val link = links[i]

                val startIndexOfLink = textView.text.toString().indexOf(link)
                spannableString.setSpan(
                    clickableSpan, startIndexOfLink, startIndexOfLink + link.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                if (textColor != 0)
                    spannableString.setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(context, textColor)),
                        startIndexOfLink,
                        startIndexOfLink + link.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                spannableString.setSpan(
                    StyleSpan(Typeface.BOLD),
                    startIndexOfLink,
                    startIndexOfLink + link.length,
                    0
                )
            }
            textView.movementMethod = LinkMovementMethod.getInstance()
            textView.setText(spannableString, TextView.BufferType.SPANNABLE)
        }

    }
}
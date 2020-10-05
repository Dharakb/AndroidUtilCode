package com.androidutilcode.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * Created by Dharak Bhatt on 2/12/19.
 * @author Dharak Bhatt
 */
class MasterDataResponse : CommonResponse() {
    val data = MasterData()
}

class MasterData {
    val themes: MutableList<ThemesData> = ArrayList()
    val currency: MutableList<CurrencyData> = ArrayList()
}

data class CurrencyData(
    var id: Int = 0,

    @SerializedName("name")
    var currencyName: String? = ""
)

@Entity(tableName = "themeData")
class ThemesData {

    @PrimaryKey(autoGenerate = false)
    var id: Int = 0

    @SerializedName("name")
    var themeName: String? = ""

    @SerializedName("theme_file")
    var themeFile: String? = null

    var isThemeSelected: Boolean = false

    var themeFilePath: String? = null

    @SerializedName("theme_headline")
    var themeHeadline: String = ""

    @SerializedName("theme_image")
    var themeImage: String = ""
}
package com.androidutilcode.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.androidutilcode.models.ThemesData

/**
 * Created by Dharak Bhatt on 2/12/19.
 * @author Dharak Bhatt
 */
@Dao
interface ThemeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(themesData: List<ThemesData>)

    @Query("SELECT * FROM themeData")
    fun getAllItems(): List<ThemesData>

    @Query("UPDATE themeData SET themeFilePath = :filePath WHERE id = :id")
    fun addThemePath(filePath: String, id: Int)

    @Query("DELETE FROM themeData")
    fun deleteAll()
}
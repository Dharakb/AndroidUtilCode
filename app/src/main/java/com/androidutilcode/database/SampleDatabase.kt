package com.androidutilcode.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.androidutilcode.models.ThemesData

@Database(entities = [ThemesData::class], version = 1)
abstract class SampleDatabase : RoomDatabase() {

    abstract fun themesDao(): ThemeDao

    companion object {
        private const val DATABASE_NAME = "SampleDatabase"

        @Volatile
        private var INSTANCE: SampleDatabase? = null

        fun getDatabase(context: Context): SampleDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    SampleDatabase::class.java,
                    DATABASE_NAME
                ).build()
            }
            return INSTANCE as SampleDatabase
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE themeData ADD COLUMN createdAt INTEGER"
                )
                database.execSQL(
                    "ALTER TABLE themeData ADD COLUMN updatedAt INTEGER"
                )
            }
        }
    }
}
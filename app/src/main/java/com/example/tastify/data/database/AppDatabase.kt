package com.example.tastify.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.tastify.data.dao.RestaurantDao
import com.example.tastify.data.dao.ReviewDao
import com.example.tastify.data.model.Restaurant
import com.example.tastify.data.model.Review

@Database(entities = [Review::class, Restaurant::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun reviewDao(): ReviewDao
    abstract fun restaurantDao(): RestaurantDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Migration from version 1 to 2 (Add new column to Review table)
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Example: Adding a new column "imagePath" to the reviews table
                database.execSQL("ALTER TABLE reviews ADD COLUMN imagePath TEXT DEFAULT NULL")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    // Option 1: Keep existing data (use migrations)
                   // .addMigrations(MIGRATION_1_2)

                    // Option 2: Reset DB when schema changes (if data loss is acceptable)
                     .fallbackToDestructiveMigration()

                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

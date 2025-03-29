package com.example.tastify.data.database

import android.content.Context
import androidx.room.*
import com.example.tastify.data.dao.RestaurantDao
import com.example.tastify.data.dao.ReviewDao
import com.example.tastify.data.dao.UserDao
import com.example.tastify.data.model.Restaurant
import com.example.tastify.data.model.Review
import com.example.tastify.data.model.User

@Database(entities = [Review::class, Restaurant::class, User::class], version = 6, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun reviewDao(): ReviewDao
    abstract fun restaurantDao(): RestaurantDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

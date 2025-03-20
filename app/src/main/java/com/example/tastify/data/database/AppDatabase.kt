package com.example.tastify.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.tastify.data.dao.RestaurantDao
import com.example.tastify.data.dao.ReviewDao
import com.example.tastify.data.dao.UserDao
import com.example.tastify.data.model.Restaurant
import com.example.tastify.data.model.Review
import com.example.tastify.data.model.User

@Database(entities = [Review::class, Restaurant::class, User::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun reviewDao(): ReviewDao
    abstract fun restaurantDao(): RestaurantDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // יצירת טבלה חדשה עם מבנה מתוקן
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS users_new (" +
                            "id TEXT PRIMARY KEY NOT NULL, " +
                            "name TEXT DEFAULT 'Guest', " +  // ✅ ברירת מחדל תקינה
                            "email TEXT NOT NULL, " +
                            "profileImageUrl TEXT DEFAULT '')"  // ✅ הסרת בעיית NULL
                )

                // בדיקה אם קיימת טבלת `users` ישנה
                val cursor = database.query("SELECT name FROM sqlite_master WHERE type='table' AND name='users'")
                val tableExists = cursor.count > 0
                cursor.close()

                if (tableExists) {
                    try {
                        database.execSQL(
                            "INSERT INTO users_new (id, name, email, profileImageUrl) " +
                                    "SELECT id, COALESCE(name, 'Guest'), email, COALESCE(profileImageUrl, '') FROM users"
                        )
                        database.execSQL("DROP TABLE users")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                // שינוי שם הטבלה החדשה ל-`users`
                database.execSQL("ALTER TABLE users_new RENAME TO users")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addMigrations(MIGRATION_2_3) // ✅ שימוש במיגרציה כדי לשמור נתונים
                    .fallbackToDestructiveMigration() // ✅ אופציה למחיקה במקרה של בעיה
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
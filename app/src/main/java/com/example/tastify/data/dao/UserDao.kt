package com.example.tastify.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.tastify.data.model.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserById(userId: String): LiveData<User?>

    @Update
    suspend fun updateUser(user: User)
}

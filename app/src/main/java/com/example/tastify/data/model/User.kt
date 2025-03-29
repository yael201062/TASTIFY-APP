package com.example.tastify.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String = "",
    @ColumnInfo(name = "name") val name: String? = null,
    @ColumnInfo(name = "email") val email: String = "",
    @ColumnInfo(name = "profileImageUrl", defaultValue = "") val profileImageUrl: String? = null
) {
    constructor() : this("", null, "", null)
}

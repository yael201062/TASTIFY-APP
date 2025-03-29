package com.example.tastify.data.database

import androidx.room.TypeConverter
import com.google.firebase.Timestamp
import java.util.*

class Converters {

    @TypeConverter
    fun fromTimestamp(timestamp: Timestamp?): Long? {
        return timestamp?.toDate()?.time
    }

    @TypeConverter
    fun toTimestamp(time: Long?): Timestamp? {
        return time?.let { Timestamp(Date(it)) }
    }
}

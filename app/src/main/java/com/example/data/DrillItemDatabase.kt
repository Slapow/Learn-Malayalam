package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.model.ActflProficiencyTier
import com.example.model.DrillItem

class DrillItemConverters {
    @TypeConverter
    fun tierToString(tier: ActflProficiencyTier): String = tier.name

    @TypeConverter
    fun stringToTier(value: String): ActflProficiencyTier = ActflProficiencyTier.valueOf(value)
}

@Database(entities = [DrillItem::class], version = 1, exportSchema = false)
@TypeConverters(DrillItemConverters::class)
abstract class DrillItemDatabase : RoomDatabase() {
    abstract fun drillItemDao(): DrillItemDao

    companion object {
        @Volatile
        private var instance: DrillItemDatabase? = null

        fun getInstance(context: Context): DrillItemDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    DrillItemDatabase::class.java,
                    "parayu_drills.db"
                ).build().also { instance = it }
            }
    }
}

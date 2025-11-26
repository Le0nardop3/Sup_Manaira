package com.manaira.supmanaira.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.manaira.supmanaira.data.local.dao.RegistroDao
import com.manaira.supmanaira.data.local.entities.RegistroEntity

@Database(
    entities = [RegistroEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun registroDao(): RegistroDao
}

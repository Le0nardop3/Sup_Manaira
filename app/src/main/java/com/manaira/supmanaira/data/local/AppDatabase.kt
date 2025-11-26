package com.manaira.supmanaira.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.manaira.supmanaira.data.local.dao.ItemDao
import com.manaira.supmanaira.data.local.dao.RegistroDao
import com.manaira.supmanaira.data.local.entities.ItemEntity
import com.manaira.supmanaira.data.local.entities.RegistroEntity

@Database(
    entities = [
        RegistroEntity::class,
        ItemEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun registroDao(): RegistroDao
    abstract fun itemDao(): ItemDao
}

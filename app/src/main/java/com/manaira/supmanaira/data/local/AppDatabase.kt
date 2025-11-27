package com.manaira.supmanaira.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.manaira.supmanaira.data.local.dao.ItemDao
import com.manaira.supmanaira.data.local.dao.RegistroDao
import com.manaira.supmanaira.data.local.dao.ProdutoDao
import com.manaira.supmanaira.data.local.entities.ItemEntity
import com.manaira.supmanaira.data.local.entities.RegistroEntity
import com.manaira.supmanaira.data.local.entities.ProdutoEntity

@Database(
    entities = [
        RegistroEntity::class,
        ItemEntity::class,
        ProdutoEntity::class
    ],
    version = 3, // MUDEI AQUI — tem que aumentar SEMPRE!
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun registroDao(): RegistroDao
    abstract fun itemDao(): ItemDao
    abstract fun produtoDao(): ProdutoDao
}

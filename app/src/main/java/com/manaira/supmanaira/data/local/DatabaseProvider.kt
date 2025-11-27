package com.manaira.supmanaira.data.local

import android.content.Context
import androidx.room.Room

object DatabaseProvider {

    @Volatile
    private var db: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return db ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "supmanaira.db"
            )
                .fallbackToDestructiveMigration() // ← ESSA LINHA ELIMINA O CRASH
                .build()

            db = instance
            instance
        }
    }
}

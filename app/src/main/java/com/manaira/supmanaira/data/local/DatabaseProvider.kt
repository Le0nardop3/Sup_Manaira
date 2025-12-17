package com.manaira.supmanaira.data.local

import android.content.Context
import androidx.room.Room
import com.manaira.supmanaira.data.local.migration.MIGRATION_5_6

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
                .addMigrations(MIGRATION_5_6)
                .build()

            db = instance
            instance
        }
    }
}

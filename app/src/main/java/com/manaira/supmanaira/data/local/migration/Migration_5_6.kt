package com.manaira.supmanaira.data.local.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {

        // Cria nova tabela com FK + CASCADE
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS itens_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                registroId INTEGER NOT NULL,
                codigo TEXT,
                nome TEXT NOT NULL,
                quantidade TEXT NOT NULL,
                tipo TEXT NOT NULL,
                validade TEXT,
                observacao TEXT,
                FOREIGN KEY(registroId) REFERENCES registros(id) ON DELETE CASCADE
            )
        """)

        // Copia dados existentes
        database.execSQL("""
            INSERT INTO itens_new (
                id, registroId, codigo, nome, quantidade, tipo, validade, observacao
            )
            SELECT 
                id, registroId, codigo, nome, quantidade, tipo, validade, observacao
            FROM itens
        """)

        // Remove tabela antiga
        database.execSQL("DROP TABLE IF EXISTS itens")

        // Renomeia
        database.execSQL("ALTER TABLE itens_new RENAME TO itens")

        // Índice
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS index_itens_registroId ON itens(registroId)"
        )
    }
}

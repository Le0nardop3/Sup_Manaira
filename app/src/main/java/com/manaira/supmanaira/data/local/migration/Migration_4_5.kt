package com.manaira.supmanaira.data.local.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {

        // 1️⃣ Criar nova tabela com FK + CASCADE
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

        // 2️⃣ Copiar os dados da tabela antiga
        database.execSQL("""
            INSERT INTO itens_new (
                id, registroId, codigo, nome, quantidade, tipo, validade, observacao
            )
            SELECT 
                id, registroId, codigo, nome, quantidade, tipo, validade, observacao
            FROM itens
        """)

        // 3️⃣ Apagar tabela antiga
        database.execSQL("DROP TABLE itens")

        // 4️⃣ Renomear nova tabela
        database.execSQL("ALTER TABLE itens_new RENAME TO itens")

        // 5️⃣ Criar índice
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS index_itens_registroId ON itens(registroId)"
        )
    }
}

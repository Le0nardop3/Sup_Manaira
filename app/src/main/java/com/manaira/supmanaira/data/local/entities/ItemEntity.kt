package com.manaira.supmanaira.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "itens")
data class ItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val registroId: Int,   // FK para o registro pai
    val nome: String,
    val quantidade: Int,
    val tipo: String,
    val validade: String? = null
)

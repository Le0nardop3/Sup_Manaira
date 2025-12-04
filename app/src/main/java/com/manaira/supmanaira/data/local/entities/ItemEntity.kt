package com.manaira.supmanaira.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "itens")
data class ItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val registroId: Int,

    // NOVO: código do produto (pode ser nulo)
    val codigo: String? = null,

    val nome: String,
    val quantidade: String,
    val tipo: String,
    val validade: String? = null,

    // NOVO: observação
    val observacao: String? = null
)

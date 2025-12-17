package com.manaira.supmanaira.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "itens",
    foreignKeys = [
        ForeignKey(
            entity = RegistroEntity::class,
            parentColumns = ["id"],
            childColumns = ["registroId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("registroId")]
)
data class ItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val registroId: Int,

    val codigo: String? = null,

    val nome: String,
    val quantidade: String,
    val tipo: String,
    val validade: String? = null,

    val observacao: String? = null
)

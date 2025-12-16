package com.manaira.supmanaira.data.local.dao

import androidx.room.*
import com.manaira.supmanaira.data.local.entities.ItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {

    // ============================
    // ITENS DE UM REGISTRO (JÁ EXISTENTE)
    // ============================
    @Query("SELECT * FROM itens WHERE registroId = :registroId ORDER BY id DESC")
    fun getItens(registroId: Int): Flow<List<ItemEntity>>

    // ============================
    // NOVO — TODOS OS ITENS COM VALIDADE
    // (usado no Controle de Validades)
    // ============================
    @Query("""
        SELECT * FROM itens
        WHERE validade IS NOT NULL
        AND validade != ''
    """)
    fun getTodosItensComValidade(): Flow<List<ItemEntity>>

    // ============================
    // CRUD
    // ============================
    @Insert
    suspend fun inserir(item: ItemEntity)

    @Update
    suspend fun atualizar(item: ItemEntity)

    @Delete
    suspend fun deletar(item: ItemEntity)
}

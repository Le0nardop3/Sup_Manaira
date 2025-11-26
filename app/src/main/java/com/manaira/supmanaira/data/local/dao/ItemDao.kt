package com.manaira.supmanaira.data.local.dao

import androidx.room.*
import com.manaira.supmanaira.data.local.entities.ItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {

    @Query("SELECT * FROM itens WHERE registroId = :registroId ORDER BY id DESC")
    fun getItens(registroId: Int): Flow<List<ItemEntity>>

    @Insert
    suspend fun inserir(item: ItemEntity)

    @Update
    suspend fun atualizar(item: ItemEntity)

    @Delete
    suspend fun deletar(item: ItemEntity)
}

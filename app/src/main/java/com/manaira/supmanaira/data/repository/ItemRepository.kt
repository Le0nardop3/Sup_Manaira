package com.manaira.supmanaira.data.repository

import com.manaira.supmanaira.data.local.dao.ItemDao
import com.manaira.supmanaira.data.local.entities.ItemEntity
import kotlinx.coroutines.flow.Flow

class ItemRepository(private val dao: ItemDao) {

    fun getItens(registroId: Int): Flow<List<ItemEntity>> =
        dao.getItens(registroId)

    suspend fun inserir(item: ItemEntity) =
        dao.inserir(item)

    suspend fun atualizar(item: ItemEntity) =
        dao.atualizar(item)

    suspend fun deletar(item: ItemEntity) =
        dao.deletar(item)
}

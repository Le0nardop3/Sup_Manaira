package com.manaira.supmanaira.data.repository

import com.manaira.supmanaira.data.local.dao.RegistroDao
import com.manaira.supmanaira.data.local.entities.RegistroEntity
import kotlinx.coroutines.flow.Flow

class RegistroRepository(
    private val dao: RegistroDao
) {

    fun getRegistros(): Flow<List<RegistroEntity>> =
        dao.getRegistros()

    suspend fun inserir(registro: RegistroEntity) =
        dao.inserir(registro)

    suspend fun atualizar(registro: RegistroEntity) =
        dao.atualizar(registro)

    suspend fun deletar(registro: RegistroEntity) =
        dao.deletar(registro)
}

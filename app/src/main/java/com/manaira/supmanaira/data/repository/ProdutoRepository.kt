package com.manaira.supmanaira.data.repository

import com.manaira.supmanaira.data.local.dao.ProdutoDao

class ProdutoRepository(private val dao: ProdutoDao) {

    suspend fun buscarPorCodigo(codigo: String) = dao.buscarPorCodigo(codigo)

    suspend fun inserirTodos(lista: List<com.manaira.supmanaira.data.local.entities.ProdutoEntity>) {
        dao.inserirTodos(lista)
    }

    suspend fun contar() = dao.contar()
}

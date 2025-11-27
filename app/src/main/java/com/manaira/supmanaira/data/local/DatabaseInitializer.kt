package com.manaira.supmanaira.data.local

import android.content.Context
import com.manaira.supmanaira.data.local.entities.ProdutoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DatabaseInitializer {

    suspend fun importarProdutos(context: Context, db: AppDatabase) {
        val dao = db.produtoDao()

        if (dao.contar() > 0) return  // já importado

        val produtos = mutableListOf<ProdutoEntity>()

        withContext(Dispatchers.IO) {
            context.assets.open("produtos.csv")
                .bufferedReader()
                .useLines { lines ->
                    lines.drop(1).forEach { linha ->

                        val partes = linha.split(",")

                        if (partes.size >= 2) {
                            val codigo = partes[0].trim()
                            val descricao = partes[1].trim()

                            produtos.add(ProdutoEntity(codigo, descricao))
                        }
                    }
                }

            dao.inserirTodos(produtos)
        }
    }
}

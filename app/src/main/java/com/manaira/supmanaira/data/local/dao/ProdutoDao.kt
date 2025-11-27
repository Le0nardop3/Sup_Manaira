package com.manaira.supmanaira.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.manaira.supmanaira.data.local.entities.ProdutoEntity

@Dao
interface ProdutoDao {

    @Query("SELECT * FROM produtos WHERE codigo = :codigo LIMIT 1")
    suspend fun buscarPorCodigo(codigo: String): ProdutoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirTodos(produtos: List<ProdutoEntity>)

    @Query("SELECT COUNT(*) FROM produtos")
    suspend fun contar(): Int
}

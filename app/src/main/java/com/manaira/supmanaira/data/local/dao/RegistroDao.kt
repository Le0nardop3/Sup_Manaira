package com.manaira.supmanaira.data.local.dao

import androidx.room.*
import com.manaira.supmanaira.data.local.entities.RegistroEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistroDao {

    @Query("SELECT * FROM registros ORDER BY id DESC")
    fun getRegistros(): Flow<List<RegistroEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(registro: RegistroEntity)

    @Update
    suspend fun atualizar(registro: RegistroEntity)

    @Delete
    suspend fun deletar(registro: RegistroEntity)
}

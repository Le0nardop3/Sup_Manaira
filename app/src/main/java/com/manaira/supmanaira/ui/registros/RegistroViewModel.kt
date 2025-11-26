package com.manaira.supmanaira.ui.registros

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manaira.supmanaira.data.repository.RegistroRepository
import com.manaira.supmanaira.data.local.entities.RegistroEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RegistroViewModel(
    private val repository: RegistroRepository
) : ViewModel() {

    val registros: StateFlow<List<RegistroEntity>> =
        repository.getRegistros()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    fun criarRegistro(nome: String) {
        viewModelScope.launch {
            repository.inserir(
                RegistroEntity(
                    nome = nome,
                    dataCriacao = System.currentTimeMillis()
                )
            )
        }
    }

    fun renomearRegistro(id: Int, novoNome: String) {
        viewModelScope.launch {
            val atual = registros.value.find { it.id == id } ?: return@launch

            repository.atualizar(
                atual.copy(nome = novoNome)
            )
        }
    }

    fun deletarRegistro(id: Int) {
        viewModelScope.launch {
            val atual = registros.value.find { it.id == id } ?: return@launch
            repository.deletar(atual)
        }
    }
}

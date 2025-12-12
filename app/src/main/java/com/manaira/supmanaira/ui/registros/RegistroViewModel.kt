package com.manaira.supmanaira.ui.registros

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manaira.supmanaira.data.local.entities.RegistroEntity
import com.manaira.supmanaira.data.repository.RegistroRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RegistroViewModel(
    private val repository: RegistroRepository
) : ViewModel() {

    // =========================
    // LISTA DE REGISTROS
    // =========================
    val registros: StateFlow<List<RegistroEntity>> =
        repository.getRegistros()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )

    // =========================
    // TÍTULO DO RELATÓRIO (REGISTRO ATUAL)
    // =========================
    private val _tituloRelatorio = MutableStateFlow("")
    val tituloRelatorio: StateFlow<String> = _tituloRelatorio.asStateFlow()

    /**
     * Carrega o título salvo do registro.
     * Se estiver vazio, usa o nome do registro como fallback.
     */
    fun carregarTitulo(registroId: Int) {
        viewModelScope.launch {
            registros
                .map { lista -> lista.firstOrNull { it.id == registroId } }
                .filterNotNull()
                .first()
                .let { registro ->
                    _tituloRelatorio.value =
                        registro.tituloRelatorio.ifBlank { registro.nome }
                }
        }
    }

    /**
     * Atualiza o título do relatório e persiste no banco.
     * Não permite valor vazio.
     */
    fun atualizarTitulo(registroId: Int, novoTitulo: String) {
        val tituloLimpo = novoTitulo.trim()
        if (tituloLimpo.isBlank()) return

        _tituloRelatorio.value = tituloLimpo

        viewModelScope.launch {
            val atual = registros.value.firstOrNull { it.id == registroId }
                ?: return@launch

            repository.atualizar(
                atual.copy(tituloRelatorio = tituloLimpo)
            )
        }
    }

    // =========================
    // CRUD REGISTRO
    // =========================
    fun criarRegistro(nome: String) {
        val nomeLimpo = nome.trim()
        if (nomeLimpo.isBlank()) return

        viewModelScope.launch {
            repository.inserir(
                RegistroEntity(
                    nome = nomeLimpo,
                    dataCriacao = System.currentTimeMillis(),
                    tituloRelatorio = nomeLimpo // 🔥 nasce coerente
                )
            )
        }
    }

    fun renomearRegistro(id: Int, novoNome: String) {
        val nomeLimpo = novoNome.trim()
        if (nomeLimpo.isBlank()) return

        viewModelScope.launch {
            val atual = registros.value.firstOrNull { it.id == id }
                ?: return@launch

            repository.atualizar(
                atual.copy(
                    nome = nomeLimpo,
                    tituloRelatorio = nomeLimpo // 🔥 mantém coerência
                )
            )
        }
    }

    fun deletarRegistro(id: Int) {
        viewModelScope.launch {
            val atual = registros.value.firstOrNull { it.id == id }
                ?: return@launch

            repository.deletar(atual)
        }
    }
}

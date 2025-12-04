package com.manaira.supmanaira.ui.itens

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manaira.supmanaira.data.local.DatabaseProvider
import com.manaira.supmanaira.data.local.entities.ItemEntity
import com.manaira.supmanaira.data.repository.ItemRepository
import com.manaira.supmanaira.data.repository.ProdutoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ItemViewModel(
    private val registroId: Int,
    context: Context
) : ViewModel() {

    private val db = DatabaseProvider.getDatabase(context)
    private val repository = ItemRepository(db.itemDao())
    private val produtoRepository = ProdutoRepository(db.produtoDao())

    val itens = repository.getItens(registroId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Agora quantidade é String
    fun criarItem(
        codigo: String?,
        nome: String,
        quantidade: String,   // <-- ALTERADO AQUI
        tipo: String,
        validade: String?,
        observacao: String?
    ) {
        viewModelScope.launch {
            repository.inserir(
                ItemEntity(
                    registroId = registroId,
                    codigo = codigo,
                    nome = nome,
                    quantidade = quantidade,   // <-- STRING
                    tipo = tipo,
                    validade = validade,
                    observacao = observacao
                )
            )
        }
    }

    fun atualizarItem(item: ItemEntity) {
        viewModelScope.launch {
            repository.atualizar(item)
        }
    }

    fun deletarItem(item: ItemEntity) {
        viewModelScope.launch {
            repository.deletar(item)
        }
    }

    suspend fun buscarDescricaoPorCodigo(codigo: String): String? {
        return produtoRepository.buscarPorCodigo(codigo)?.descricao
    }
}

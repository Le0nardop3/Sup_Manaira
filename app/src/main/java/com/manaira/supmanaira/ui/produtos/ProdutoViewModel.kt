    package com.manaira.supmanaira.ui.produtos

    import android.content.Context
    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import com.manaira.supmanaira.data.local.DatabaseProvider
    import com.manaira.supmanaira.data.repository.ProdutoRepository
    import kotlinx.coroutines.launch
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.withContext

    class ProdutoViewModel(context: Context) : ViewModel() {

        private val db = DatabaseProvider.getDatabase(context)
        private val repository = ProdutoRepository(db.produtoDao())

        suspend fun buscar(codigo: String) =
            withContext(Dispatchers.IO) {
                repository.buscarPorCodigo(codigo)
            }
    }

package com.manaira.supmanaira.ui.itens

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ItemViewModelFactory(
    private val registroId: Int,
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ItemViewModel(
            registroId = registroId,
            context = context
        ) as T
    }
}

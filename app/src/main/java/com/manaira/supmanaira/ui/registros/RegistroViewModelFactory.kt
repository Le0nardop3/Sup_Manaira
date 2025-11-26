package com.manaira.supmanaira.ui.registros

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.manaira.supmanaira.data.local.DatabaseProvider
import com.manaira.supmanaira.data.repository.RegistroRepository


class RegistroViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        val db = DatabaseProvider.getDatabase(context)
        val repo = RegistroRepository(db.registroDao())

        return RegistroViewModel(repo) as T
    }
}

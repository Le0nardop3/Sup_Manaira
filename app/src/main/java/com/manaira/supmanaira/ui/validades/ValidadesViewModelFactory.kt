package com.manaira.supmanaira.ui.validades

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ValidadesViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ValidadesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ValidadesViewModel(context) as T
        }
        throw IllegalArgumentException("ViewModel inválido")
    }
}

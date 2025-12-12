package com.manaira.supmanaira.utils

import android.content.Context
import android.util.Log
import org.json.JSONObject

object ProdutoJsonUtils {

    private var cache: Map<String, String>? = null

    fun buscarDescricao(context: Context, codigoLido: String): String? {

        if (cache == null) {
            Log.d("JSON", "Carregando produtos.json em memória")
            cache = carregar(context)
            Log.d("JSON", "Produtos carregados: ${cache?.size}")
        }

        val codigo = codigoLido.trim()

        // tentativa direta
        cache?.get(codigo)?.let {
            Log.d("JSON", "ACHOU direto: $codigo -> $it")
            return it
        }

        // tentativa sem zeros à esquerda
        val semZeros = codigo.trimStart('0')
        if (semZeros != codigo) {
            cache?.get(semZeros)?.let {
                Log.d("JSON", "ACHOU sem zeros: $semZeros -> $it")
                return it
            }
        }

        Log.w("JSON", "NÃO ACHOU código: $codigo")
        return null
    }

    private fun carregar(context: Context): Map<String, String> {
        val jsonText = context.assets
            .open("produtos.json")
            .bufferedReader()
            .use { it.readText() }

        val obj = JSONObject(jsonText)
        val map = HashMap<String, String>(obj.length())

        val keys = obj.keys()
        while (keys.hasNext()) {
            val k = keys.next()
            map[k.trim()] = obj.getString(k)
        }
        return map
    }
}

package com.manaira.supmanaira.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.manaira.supmanaira.data.local.entities.ItemEntity
import org.dhatim.fastexcel.Workbook
import java.io.File
import java.io.FileOutputStream

object ExportUtils {

    fun exportarExcel(
        context: Context,
        titulo: String,
        nomeRegistro: String, // ← nome lógico do registro
        itens: List<ItemEntity>
    ): Uri? {
        return try {

            val downloads = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            )

            // garante extensão correta e nome limpo
            val nomeArquivo = if (nomeRegistro.endsWith(".xlsx", true)) {
                nomeRegistro
            } else {
                "$nomeRegistro.xlsx"
            }

            val file = File(downloads, nomeArquivo)

            // 🔥 FileOutputStream sobrescreve automaticamente
            FileOutputStream(file, false).use { fos ->
                val workbook = Workbook(fos, "SupManaira", "1.0")
                val sheet = workbook.newWorksheet("Relatório")

                var row = 0

                // ----------------------- TÍTULO -----------------------
                sheet.value(row, 0, titulo)
                row += 2

                // ----------------------- CABEÇALHO -----------------------
                val headers = listOf(
                    "Código",
                    "Nome",
                    "Quantidade",
                    "Validade",
                    "Observação"
                )

                headers.forEachIndexed { col, h ->
                    sheet.value(row, col, h)
                }

                row++

                // ----------------------- DADOS -----------------------
                itens.forEach { item ->
                    sheet.value(row, 0, item.codigo ?: "")
                    sheet.value(row, 1, item.nome)
                    sheet.value(row, 2, item.quantidade)
                    sheet.value(row, 3, item.validade ?: "")
                    sheet.value(row, 4, item.observacao ?: "")
                    row++
                }

                workbook.finish()
            }

            FileProvider.getUriForFile(
                context,
                context.packageName + ".provider",
                file
            )

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun compartilharArquivo(context: Context, fileUri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            putExtra(Intent.EXTRA_STREAM, fileUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(
            Intent.createChooser(intent, "Compartilhar arquivo")
        )
    }
}

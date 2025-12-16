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
        nomeRegistro: String,
        itens: List<ItemEntity>
    ): Uri? {
        return try {

            val downloads = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            )

            val nomeArquivo = if (nomeRegistro.endsWith(".xlsx", true)) {
                nomeRegistro
            } else {
                "$nomeRegistro.xlsx"
            }

            val file = File(downloads, nomeArquivo)

            FileOutputStream(file, false).use { fos ->

                val workbook = Workbook(fos, "SupManaira", "1.0")
                val sheet = workbook.newWorksheet("Relatório")

                var row = 0

                // mescla A1:E2
                sheet.range(0, 0, 1, 4).merge()

                // estilo da área
                sheet.range(0, 0, 1, 4)
                    .style()
                    .bold()
                    .fontSize(22)
                    .fontColor("#000000") // <<< ESSENCIAL
                    .verticalAlignment("center")

                // altura das linhas
                sheet.rowHeight(0, 36.0)
                sheet.rowHeight(1, 36.0)

                // escreve no centro visual
                sheet.value(0, 2, titulo)

                row = 2




                /* =========================
                   CABEÇALHO
                   ========================= */

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

                sheet.range(row, 0, row, 4)
                    .style()
                    .bold()
                    .fontColor("#FFFFFF")
                    .fillColor("#0D47A1")
                    .horizontalAlignment("center")

                row++

                /* =========================
                   DADOS (ZEBRADO)
                   ========================= */

                itens.forEachIndexed { index, item ->

                    sheet.value(row, 0, item.codigo ?: "")
                    sheet.value(row, 1, item.nome)
                    sheet.value(row, 2, item.quantidade)
                    sheet.value(row, 3, item.validade ?: "")
                    sheet.value(row, 4, item.observacao ?: "")

                    if (index % 2 == 1) {
                        sheet.range(row, 0, row, 4)
                            .style()
                            .fillColor("#F2F2F2")
                    }

                    row++
                }

                /* =========================
                   LARGURA COLUNAS
                   ========================= */

                sheet.width(0, 20.0)
                sheet.width(1, 40.0)
                sheet.width(2, 18.0)
                sheet.width(3, 18.0)
                sheet.width(4, 30.0)

                workbook.finish()
            }

            FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun compartilharArquivo(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(
            Intent.createChooser(intent, "Compartilhar Excel")
        )
    }
}

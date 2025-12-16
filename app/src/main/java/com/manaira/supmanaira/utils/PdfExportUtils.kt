package com.manaira.supmanaira.utils

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.manaira.supmanaira.data.local.entities.ItemEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object PdfExportUtils {

    fun exportarPdf(
        context: Context,
        titulo: String,
        nomeArquivo: String,
        itens: List<ItemEntity>
    ): Uri? {
        return try {

            val pdfDocument = PdfDocument()

            val pageWidth = 595f
            val pageHeight = 842f

            val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = 20f
                typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
            }

            val headerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = 11f
                typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            }

            val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = 10f
                typeface = Typeface.MONOSPACE
            }

            val linePaint = Paint().apply {
                strokeWidth = 1f
            }

            var pageNumber = 1
            var y = 0f

            lateinit var currentPage: PdfDocument.Page
            lateinit var canvas: Canvas

            fun startNewPage() {
                val pageInfo = PdfDocument.PageInfo.Builder(
                    pageWidth.toInt(),
                    pageHeight.toInt(),
                    pageNumber++
                ).create()

                currentPage = pdfDocument.startPage(pageInfo)
                canvas = currentPage.canvas

                // ===== TÍTULO =====
                canvas.drawText(titulo, pageWidth / 2, 50f, titlePaint)

                // ===== DATA =====
                val dataHora = SimpleDateFormat(
                    "dd/MM/yyyy HH:mm",
                    Locale("pt", "BR")
                ).format(Date())

                canvas.drawText(
                    "Gerado em: $dataHora",
                    pageWidth - 40f,
                    70f,
                    Paint(textPaint).apply {
                        textAlign = Paint.Align.RIGHT
                        textSize = 9f
                    }
                )

                // ===== CABEÇALHO =====
                y = 110f
                desenharCabecalho(canvas, headerPaint, linePaint)

                // espaço REAL após cabeçalho
                y += 24f
            }

            startNewPage()

            itens.forEach { item ->
                if (y > pageHeight - 50f) {
                    pdfDocument.finishPage(currentPage)
                    startNewPage()
                }

                desenharLinha(canvas, textPaint, linePaint, item, y)
                y += 20f
            }

            pdfDocument.finishPage(currentPage)

            val downloads = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            )
            val file = File(downloads, "$nomeArquivo.pdf")

            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()

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

    private fun desenharCabecalho(
        canvas: Canvas,
        paint: Paint,
        linePaint: Paint
    ) {
        val colX = floatArrayOf(30f, 120f, 380f, 450f, 530f)
        val y = 110f

        val headers = arrayOf("Código", "Nome", "Qtd", "Validade", "Obs")

        headers.forEachIndexed { i, text ->
            canvas.drawText(text, colX[i], y, paint)
        }

        canvas.drawLine(20f, y + 6f, 575f, y + 6f, linePaint)
    }

    private fun desenharLinha(
        canvas: Canvas,
        paint: Paint,
        linePaint: Paint,
        item: ItemEntity,
        y: Float
    ) {
        val colX = floatArrayOf(30f, 120f, 380f, 450f, 530f)
        val larguraNome = 240f

        canvas.drawText(item.codigo ?: "", colX[0], y, paint)

        val nome = cortarTexto(item.nome, paint, larguraNome)
        canvas.drawText(nome, colX[1], y, paint)

        canvas.drawText(item.quantidade, colX[2], y, paint)
        canvas.drawText(item.validade ?: "", colX[3], y, paint)
        canvas.drawText(item.observacao ?: "", colX[4], y, paint)

        canvas.drawLine(20f, y + 4f, 575f, y + 4f, linePaint)
    }

    private fun cortarTexto(
        texto: String,
        paint: Paint,
        larguraMaxima: Float
    ): String {
        if (paint.measureText(texto) <= larguraMaxima) return texto

        var t = texto
        while (t.isNotEmpty() && paint.measureText("$t…") > larguraMaxima) {
            t = t.dropLast(1)
        }
        return "$t…"
    }

    fun compartilharPdf(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Compartilhar PDF"))
    }
}

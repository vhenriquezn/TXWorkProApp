package com.vhenriquez.txwork.worker

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.compose.ui.res.stringResource
import androidx.core.content.FileProvider
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.io.image.ImageDataFactory
import com.vhenriquez.txwork.model.InstrumentEntity
import java.io.File
import java.io.FileOutputStream
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.events.PdfDocumentEvent
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.borders.SolidBorder
import com.itextpdf.layout.element.AreaBreak
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.properties.AreaBreakType
import com.itextpdf.layout.properties.HorizontalAlignment
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import com.itextpdf.layout.properties.VerticalAlignment
import com.vhenriquez.txwork.R
import com.vhenriquez.txwork.utils.HeaderFooterEventHandler
import java.io.ByteArrayOutputStream
import kotlin.math.sqrt
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.itextpdf.kernel.colors.Color
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.element.Text
import com.vhenriquez.txwork.model.CalibrationEntity
import com.vhenriquez.txwork.utils.CommonUtils
import com.vhenriquez.txwork.utils.MyXAxisFormatter

class TemplatePDFCertificate {
    private lateinit var document : Document
    private var titleStyle = 0
    private var subTitleStyle = 1
    private var textStyle = 2
    private var textStyleBold = 3
    private var textStyleBoldPainted = 4
    private lateinit var titleColor : Color
    private lateinit var subTitleColor : Color

    private val solid = SolidBorder(1f)
    private val line = SolidBorder(0.5f)

    private var imageError : Image? = null

    fun generatePdf(context: Context, data: InstrumentEntity, activityId: String, activityName: String) : File {
        val file = File(createFolder(activityName), "${data.tag.replace("/","_")}.pdf")
        val pdfWriter = PdfWriter(FileOutputStream(file))
        val pdf = PdfDocument(pdfWriter)
        document = Document(pdf, PageSize.LETTER)
        document.setMargins(60f,50f,20f,50f)
        val calibrationEntity = data.calibrations.getOrDefault(activityId, CalibrationEntity())
        addHeaderFooter(context, pdf, calibrationEntity.calibrationType)
        addInfoInstrument(context, data, calibrationEntity)
        document.close()
        return file
    }

    fun openPdfFile(context: Context, file : File?){
        if (file != null){
            val uri = FileProvider.getUriForFile(context, context.packageName,
                file)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "application/pdf")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            try {
                context.startActivity(intent)
            }catch (e : Exception){
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=com.adobe.reader"))
                )
                Toast.makeText(context, "No se encontro una aplicacion para visualizar el PDF", Toast.LENGTH_LONG).show()

            }
        }
    }

    private fun createFolder(activityName: String) : File{
        val folder = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).path,
            "/${activityName.replace("/","_")}/Hojas de Verificación")
        if (!folder.exists())
            folder.mkdirs()
        return folder
    }

    private fun addHeaderFooter(context: Context, pdf: PdfDocument, typeCalibration : String) {
        val tableHeader = Table(UnitValue.createPercentArray(floatArrayOf(130f, 263f, 130f)))
        tableHeader.width = UnitValue.createPercentValue(100f)

        val bmp = BitmapFactory.decodeResource(context.resources, R.drawable.veset_logo)
        val stream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val image = Image(ImageDataFactory.create(stream.toByteArray()))
        image.setHeight(42f)
        image.setHorizontalAlignment(HorizontalAlignment.CENTER)
        var pdfCell = Cell().add(Paragraph(""))
        pdfCell.setBorder(Border.NO_BORDER)
        tableHeader.addCell(pdfCell)

        pdfCell = Cell().add(Paragraph( "PROTOCOLO DE VERIFICACIÓN\nServicio Tecnico\n$typeCalibration"))
            .setBorder(Border.NO_BORDER)
            .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
            .setFontSize(9f)
        pdfCell.setTextAlignment(TextAlignment.CENTER)
        tableHeader.addCell(pdfCell)

        pdfCell = Cell().add(image)
        pdfCell.setBorder(Border.NO_BORDER)
        pdfCell.setVerticalAlignment(VerticalAlignment.MIDDLE)
        tableHeader.addCell(pdfCell)
//
        val tableFooter = Table(UnitValue.createPercentArray(1)).useAllAvailableWidth()
        tableHeader.width = UnitValue.createPercentValue(100f)
        val bmpFooterPhoto = BitmapFactory.decodeResource(context.resources, R.drawable.footer_photo)
        val streamFooterPhoto = ByteArrayOutputStream()
        bmpFooterPhoto.compress(Bitmap.CompressFormat.PNG, 100, streamFooterPhoto)
        val footerImage = Image(ImageDataFactory.create(streamFooterPhoto.toByteArray()))
        footerImage.setHeight(60f)
        footerImage.setHorizontalAlignment(HorizontalAlignment.CENTER)

        val footerCell = Cell().add(footerImage)
        footerCell.setBorder(Border.NO_BORDER)
        tableFooter.addCell(footerCell)
        pdf.addEventHandler(PdfDocumentEvent.END_PAGE, HeaderFooterEventHandler(tableHeader, tableFooter))
    }

    private fun addInfoInstrument(context: Context, instrumentEntity: InstrumentEntity, calibrationEntity: CalibrationEntity){
        var idColError = 23
        var cantError: Int
        val strPattern = arrayListOf("", "", "", "", "")
        val errorFound = arrayListOf<Entry>()
        val errorLeft = arrayListOf<Entry>()

        for (index in 0 until calibrationEntity.patternEntities.size){
            val pattern = calibrationEntity.patternEntities[index]
            strPattern.add(index, "${pattern.name} / ${pattern.brand} / ${pattern.serial} /" +
                    " ${pattern.certificate.certificateId} / ${pattern.certificate.laboratory}")
        }
        val tablePage1 = Table(floatArrayOf(68F, 68F, 91F, 95F, 72F, 68F, 80F, 76F, 58F))//676

        with(tablePage1){
            addCell(createCell("DATOS DEL INSTRUMENTO", titleStyle, 9, 1, TextAlignment.CENTER, solid, solid, solid, null))

            addCell(createCell("TAG", textStyleBold, 2, 1, TextAlignment.LEFT, solid, null, null, null))
            addCell(createCell(":", textStyleBold, 1, 1, TextAlignment.LEFT, null, null, null, null))
            addCell(createCell(instrumentEntity.tag, textStyle, 6, 1, TextAlignment.CENTER, null,solid, null, null))

            addCell(createCell("FECHA", textStyleBold, 2, 1, TextAlignment.LEFT, solid, null, null, null))
            addCell(createCell(":", textStyleBold, 1, 1, TextAlignment.LEFT, null, null, null, null))
            addCell(createCell(calibrationEntity.calibrateDate, textStyle, 6, 1, TextAlignment.CENTER, null,solid, null, null))

            addCell(createCell("DESCRIPCION", textStyleBold, 2, 1, TextAlignment.LEFT, solid, null, null, null))
            addCell(createCell(":", textStyleBold, 1, 1, TextAlignment.LEFT, null, null, null, null))
            addCell(createCell(instrumentEntity.description, textStyle, 6, 1, TextAlignment.CENTER, null,solid, null, null))

            addCell(createCell("MARCA", textStyleBold, 2, 1, TextAlignment.LEFT, solid, null, null, null))
            addCell(createCell(":", textStyleBold, 1, 1, TextAlignment.LEFT, null, null, null, null))
            addCell(createCell(instrumentEntity.brand, textStyle, 6, 1, TextAlignment.CENTER, null,solid, null, null))

            addCell(createCell("MODELO", textStyleBold, 2, 1, TextAlignment.LEFT, solid, null, null, null))
            addCell(createCell(":", textStyleBold, 1, 1, TextAlignment.LEFT, null, null, null, null))
            addCell(createCell(instrumentEntity.model, textStyle, 6, 1, TextAlignment.CENTER, null,solid, null, null))

            addCell(createCell("N° SERIE", textStyleBold, 2, 1, TextAlignment.LEFT, solid, null, null, null))
            addCell(createCell(":", textStyleBold, 1, 1, TextAlignment.LEFT, null, null, null, null))
            addCell(createCell(instrumentEntity.serial, textStyle, 6, 1, TextAlignment.CENTER, null,solid, null, null))

            if (instrumentEntity.instrumentType == context.getString(R.string.addInstrument_text_rbManometer) ||
                instrumentEntity.instrumentType == context.getString(R.string.addInstrument_text_rbThermometer)){

                addCell(createCell("DIAMETRO CARATULA", textStyleBold, 2, 1, TextAlignment.LEFT, solid, null, null, null))
                addCell(createCell(":", textStyleBold, 1, 1, TextAlignment.LEFT, null, null, null, null))
                addCell(createCell(instrumentEntity.diameter, textStyle, 6, 1, TextAlignment.CENTER, null,solid, null, null))

                addCell(createCell("RESOLUCION", textStyleBold, 2, 1, TextAlignment.LEFT, solid, null, null, null))
                addCell(createCell(":", textStyleBold, 1, 1, TextAlignment.LEFT, null, null, null, null))
                addCell(createCell(instrumentEntity.resolution, textStyle, 6, 1, TextAlignment.CENTER, null,solid, null, null))
                idColError += 2
            }

            addCell(createCell("", textStyleBold, 9, 1, TextAlignment.LEFT, solid, solid, null, null))
            addCell(createCell("DATOS DEL SENSOR", titleStyle, 9, 1, TextAlignment.CENTER, solid, solid, null, null))

            addCell(createCell("RANGO", textStyleBold, 2, 1, TextAlignment.LEFT, solid, null, null, null))
            addCell(createCell(":", textStyleBold, 1, 1, TextAlignment.LEFT, null, null, null, null))
            addCell(createCell(instrumentEntity.getRangeVerification(), textStyle, 6, 1, TextAlignment.CENTER, null,solid, null, null))

            addCell(createCell("SPAN", textStyleBold, 2, 1, TextAlignment.LEFT, solid, null, null, null))
            addCell(createCell(":", textStyleBold, 1, 1, TextAlignment.LEFT, null, null, null, null))
            addCell(createCell(instrumentEntity.getSpanInstrument(), textStyle, 6, 1, TextAlignment.CENTER, null,solid, null, null))

            addCell(createCell(if (instrumentEntity.output == "FIELDBUS" || instrumentEntity.output == "PROFIBUS")
                "DIRECCION ${instrumentEntity.output}" else "DAMPING", textStyleBold, 2, 1, TextAlignment.LEFT, solid, null, null, null))
            addCell(createCell(":", textStyleBold, 1, 1, TextAlignment.LEFT, null, null, null, null))
            addCell(createCell(if (instrumentEntity.output == "FIELDBUS" || instrumentEntity.output == "PROFIBUS")
                instrumentEntity.address else instrumentEntity.getDampingInstrument(), textStyle, 6, 1, TextAlignment.CENTER, null,solid, null, null))

            addCell(createCell("OUTPUT", textStyleBold, 2, 1, TextAlignment.LEFT, solid, null, null, null))
            addCell(createCell(":", textStyleBold, 1, 1, TextAlignment.LEFT, null, null, null, null))
            addCell(createCell(instrumentEntity.output, textStyle, 6, 1, TextAlignment.CENTER, null,solid, null, null))

            addCell(createCell("TIPO", textStyleBold, 2, 1, TextAlignment.LEFT, solid, null, null, null))
            addCell(createCell(":", textStyleBold, 1, 1, TextAlignment.LEFT, null, null, null, null))
            addCell(createCell(instrumentEntity.sensorType, textStyle, 6, 1, TextAlignment.CENTER, null,solid, null, null))

            if (instrumentEntity.instrumentType == context.getString(R.string.addInstrument_text_rbThermometer) || (instrumentEntity.instrumentType == context.getString(R.string.addInstrument_text_rbSensor) && instrumentEntity.magnitude == context.getString(R.string.addInstrument_text_rbTemperature))){
                addCell(createCell("DIAMETRO SENSOR", textStyleBold, 2, 1, TextAlignment.LEFT, solid, null, null, null))
                addCell(createCell(":", textStyleBold, 1, 1, TextAlignment.LEFT, null, null, null, null))
                addCell(createCell(instrumentEntity.sensorDiameter, textStyle, 6, 1, TextAlignment.CENTER, null,solid, null, null))

                addCell(createCell("LARGO UTIL", textStyleBold, 2, 1, TextAlignment.LEFT, solid, null, null, null))
                addCell(createCell(":", textStyleBold, 1, 1, TextAlignment.LEFT, null, null, null, null))
                addCell(createCell(instrumentEntity.usefulLength, textStyle, 6, 1, TextAlignment.CENTER, null,solid, null, null))
                idColError += 2
            }

            if (instrumentEntity.instrumentType == context.getString(R.string.addInstrument_text_rbManometer) || instrumentEntity.instrumentType == context.getString(R.string.addInstrument_text_rbThermometer)){
                addCell(createCell("CONEXION PROCESO", textStyleBold, 2, 1, TextAlignment.LEFT, solid, null, null, null))
                addCell(createCell(":", textStyleBold, 1, 1, TextAlignment.LEFT, null, null, null, null))
                addCell(createCell(instrumentEntity.processConnection, textStyle, 6, 1, TextAlignment.CENTER, null,solid, null, null))
                idColError += 1
            }

            addCell(createCell("", textStyleBold, 9, 1, TextAlignment.LEFT, solid, solid, null, null))
            addCell(createCell("EQUIPOS PATRONES UTILIZADOS", titleStyle, 9, 1, TextAlignment.CENTER, solid, solid, null, null))
            addCell(createCell("Tipo / Marca / Modelo / N° Serie / N° Certificado", subTitleStyle, 9, 1, TextAlignment.CENTER, solid, solid, null, null))
            addCell(createCell(strPattern[0], textStyle, 9, 1, TextAlignment.CENTER, solid, solid, null, null))
            addCell(createCell(strPattern[1], textStyle, 9, 1, TextAlignment.CENTER, solid, solid, null, null))
            addCell(createCell(strPattern[2], textStyle, 9, 1, TextAlignment.CENTER, solid, solid, null, null))
            addCell(createCell(strPattern[3], textStyle, 9, 1, TextAlignment.CENTER, solid, solid, null, null))

            addCell(createCell("", textStyleBold, 9, 1, TextAlignment.LEFT, solid, solid, null, null))
            addCell(createCell("OBSERVACIONES", titleStyle, 9, 1, TextAlignment.CENTER, solid, solid, null, null))

            addCell(createCell("Error span Promedio Encontrado", textStyleBold, 3, 1, TextAlignment.LEFT, solid, null, null, null))
            addCell(createCell(":", textStyleBold, 1, 1, TextAlignment.LEFT, null, null, null, null))
            addCell(createCell("-", textStyleBold, 5, 1, TextAlignment.CENTER, null,solid, null, null))

            addCell(createCell("Error span Promedio Dejado", textStyleBold, 3, 1, TextAlignment.LEFT, solid, null, null, null))
            addCell(createCell(":", textStyleBold, 1, 1, TextAlignment.LEFT, null, null, null, null))
            addCell(createCell("-", textStyleBold, 5, 1, TextAlignment.CENTER, null,solid, null, null))
            addCell(createCell("", textStyleBold, 9, 1, TextAlignment.LEFT, solid, solid, null, null))

            addCell(createCell(calibrationEntity.observation, textStyle, 9, 10, TextAlignment.CENTER, solid, solid, null, null))
            addCell(createCell("REALIZADO POR:", textStyle, 4, 1, TextAlignment.RIGHT, solid, null, null, null))
            addCell(createCell(calibrationEntity.calibrateInst.uppercase(), textStyle, 4, 1, TextAlignment.CENTER, null, null, null, line))
            addCell(createCell("", textStyleBold, 1, 1, TextAlignment.CENTER, null, solid, null, null))

            addCell(createCell("", textStyleBold, 4, 1, TextAlignment.LEFT, solid, null, null, null))
            addCell(createCell("ESPECIALISTA", textStyleBold, 4, 1, TextAlignment.CENTER, null, null, null, null))
            addCell(createCell("", textStyleBold, 1, 1, TextAlignment.CENTER, null, solid, null, null))
            addCell(createCell("", textStyleBold, 9, 1, TextAlignment.LEFT, solid, solid, null, solid))
        }

        if (instrumentEntity.reportType != context.resources.getStringArray(R.array.report_type_options)[7]){
            val tablePage2 = Table(floatArrayOf(68F, 68F, 91F, 95F, 72F, 68F, 80F, 76F, 58F))
            var tablePage3: Table? = null

            with(tablePage2) {
                addCell(createCell("RESULTADOS", titleStyle, 9, 1, TextAlignment.CENTER, solid, solid, solid, null))

                addCell(createCell("", textStyleBold, 1, 2, TextAlignment.LEFT, solid, null, null, null))
                addCell(createCell("ESCALA", subTitleStyle, 2, 2, TextAlignment.CENTER, solid, solid, solid, solid))
                addCell(createCell("VALORES ENCONTRADOS", subTitleStyle, 5, 2, TextAlignment.CENTER, null, solid, solid, solid))
                addCell(createCell("", textStyle, 1, 2, TextAlignment.CENTER, null,solid, null, null))

                when (instrumentEntity.reportType) {
                    context.resources.getStringArray(R.array.report_type_options)[5]-> addHeaderSwitch(tablePage2, instrumentEntity)
                    context.resources.getStringArray(R.array.report_type_options)[0] -> addHeaderManometer(tablePage2, instrumentEntity)
                    context.resources.getStringArray(R.array.report_type_options)[3],
                    context.resources.getStringArray(R.array.report_type_options)[6]-> addHeaderPH(tablePage2, instrumentEntity)
                    else -> addHeaderOther(tablePage2, if (instrumentEntity.magnitude == "Flujo Multivariable") instrumentEntity.verificationUnitSV else instrumentEntity.verificationUnit)
                }

                val calibrationDates = calibrationEntity.calibrationValues ?: mutableMapOf()
                val porcentajeArray = arrayOf("0", "25", "50", "75", "100", "75", "50", "25", "0","",
                    "0", "25", "50", "75", "100", "75", "50", "25", "0","",
                    "0", "25", "50", "75", "100", "75", "50", "25", "0")
                when(instrumentEntity.reportType){
//                "PH", "Portatil"->{
                    context.resources.getStringArray(R.array.report_type_options)[3]->{//3 puntos directos
                        var errorFoundProm = 0F
                        cantError = 0
                        for (row in 0 until calibrationDates.size){
                            val datesCol = calibrationDates.getOrElse("values $row") {
                                mutableMapOf<String, String>()
                            } as MutableMap<String, String>
                            if (datesCol.isEmpty()) continue
                            val value0 = getValue(datesCol.getOrPut( "value 0"){"0"})
                            val value1 = getValue(datesCol.getOrPut( "value 1"){"0"})
                            val span = instrumentEntity.getSpan()
                            val error1 = CommonUtils.getErrorRelativo(value0, value1, span)

                            addCell(createCell("", textStyle, 1, 1, TextAlignment.CENTER, solid, solid, null, null))
                            addCell(createCell(String.format("%.3f",value0), textStyle, 2, 1, TextAlignment.CENTER, null, solid, null, if (row == calibrationDates.size-1) solid else line))
                            addCell(createCell(String.format("%.3f",value0), textStyle, 2, 1, TextAlignment.CENTER, null, line, null, if (row == calibrationDates.size-1) solid else line))
                            addCell(createCell(String.format("%.3f",value1), textStyle, 2, 1, TextAlignment.CENTER, null, line, null, if (row == calibrationDates.size-1) solid else line))
                            addCell(createCell(String.format("%.2f",error1), textStyle, 1, 1, TextAlignment.CENTER, null, solid, null, if (row == calibrationDates.size-1) solid else line))
                            addCell(createCell("", textStyle, 1, 1, TextAlignment.CENTER, null, solid, null, null))

                            errorFoundProm += error1
                            if (!datesCol["value 0"].isNullOrEmpty())
                                cantError += 1
                        }
                        if (cantError > 0)
                            errorFoundProm /= cantError
                        addCell(createCell("", textStyle, 3, 1, TextAlignment.CENTER, solid, solid, null, null))
                        addCell(createCell("Error Span Prom Encontrado", textStyleBold, 2, 1, TextAlignment.CENTER, null, solid, null, solid))
                        addCell(createCell("${String.format("%.2f",errorFoundProm)} %", textStyleBold, 1, 1, TextAlignment.CENTER, null, solid, null, solid))
                        addCell(createCell("", textStyleBold, 3, 1, TextAlignment.CENTER, null, solid, null, null))
                        addCell(createCell("", textStyleBold, 9, 1, TextAlignment.CENTER, solid, solid, null, null))
                        ((tablePage1.getCell(idColError,4).children[0] as Paragraph).children[0] as Text).text = "${String.format("%.2f",errorFoundProm)} %"
                        //tablePage1.getCell(23,4).add(Paragraph("${String.format("%.2f",errorFoundProm)} %"))
                        addCell(createCell("", textStyleBold, 1, 2, TextAlignment.LEFT, solid, null, null, null))
                        addCell(createCell("ESCALA", subTitleStyle, 2, 2, TextAlignment.CENTER, solid, solid, solid, solid))
                        addCell(createCell("VALORES DEJADOS", subTitleStyle, 5, 2, TextAlignment.CENTER, null, solid, solid, solid))
                        addCell(createCell("", textStyle, 1, 2, TextAlignment.CENTER, null,solid, null, null))
                        addHeaderPH(tablePage2, instrumentEntity)
                        errorFoundProm = 0f
                        cantError = 0

                        for (row in 0 until calibrationDates.size){
                            val datesCol = calibrationDates.getOrElse("values $row") {
                                mutableMapOf<String, String>()
                            } as MutableMap<String, String>
                            if (datesCol.isEmpty()) continue
                            val value0 = getValue(datesCol.getOrPut( "value 0"){"0"})
                            val value2 = getValue(datesCol.getOrPut( "value 2"){"0"})
                            val span = instrumentEntity.getSpan()
                            val error2 = CommonUtils.getErrorRelativo(value0, value2, span)
                            addCell(createCell("", textStyle, 1, 1, TextAlignment.CENTER, solid, solid, null, null))
                            addCell(createCell(String.format("%.3f",value0), textStyle, 2, 1, TextAlignment.CENTER, null, solid, null, if (row == calibrationDates.size-1) solid else line))
                            addCell(createCell(String.format("%.3f",value0), textStyle, 2, 1, TextAlignment.CENTER, null, line, null, if (row == calibrationDates.size-1) solid else line))
                            addCell(createCell(String.format("%.3f",value2), textStyle, 2, 1, TextAlignment.CENTER, null, line, null, if (row == calibrationDates.size-1) solid else line))
                            addCell(createCell(String.format("%.2f",error2), textStyle, 1, 1, TextAlignment.CENTER, null, solid, null, if (row == calibrationDates.size-1) solid else line))
                            addCell(createCell("", textStyle, 1, 1, TextAlignment.CENTER, null, solid, null, null))

                            errorFoundProm += error2
                            if (!datesCol["value 0"].isNullOrEmpty())
                                cantError += 1
                        }
                        if (cantError > 0)
                            errorFoundProm /= cantError

                        addCell(createCell("", textStyle, 3, 1, TextAlignment.CENTER, solid, solid, null, null))
                        addCell(createCell("Error Span Prom Dejado", textStyleBold, 2, 1, TextAlignment.CENTER, null, solid, null, solid))
                        addCell(createCell("${String.format("%.2f",errorFoundProm)} %", textStyleBold, 1, 1, TextAlignment.CENTER, null, solid, null, solid))
                        addCell(createCell("", textStyleBold, 3, 1, TextAlignment.CENTER, null, solid, null, null))
                        addCell(createCell("", textStyleBold, 9, 1, TextAlignment.CENTER, solid, solid, null, null))
                        ((tablePage1.getCell(idColError+1,4).children[0] as Paragraph).children[0] as Text).text = "${String.format("%.2f",errorFoundProm)} %"
                        // tablePage1.getCell(24,4).add(Paragraph("${String.format("%.2f",errorFoundProm)} %"))
                        imageError = null
                    }

//                "Manómetro", "Termómetro", "Sensor", "Fieldbus", "Profibus"->{
                    context.resources.getStringArray(R.array.report_type_options)[0]->{
                        var errorFoundProm = 0F
                        cantError = 0
                        for (row in 0 until calibrationDates.size) {
                            val datesCol = calibrationDates.getOrElse("values $row") {
                                mutableMapOf<String, String>()
                            } as MutableMap<String, String>
                            if (datesCol.isEmpty()) continue
                            val value0 = getValue(datesCol.getOrPut( "value 0"){"0"})
                            val value1 = getValue(datesCol.getOrPut( "value 1"){"0"})
                            val span = instrumentEntity.getSpan()
                            val error1 = CommonUtils.getErrorRelativo(value0, value1, span)
                            addCell(createCell("", textStyle, 1, 1, TextAlignment.CENTER, solid, solid, null, null))
                            addCell(createCell(porcentajeArray[row], textStyle, 1, 1, TextAlignment.CENTER, null, line, null, if (row == calibrationDates.size-1) solid else line))
                            addCell(createCell(String.format("%.3f",value0), textStyle, 1, 1, TextAlignment.CENTER, null, solid, null, if (row == calibrationDates.size-1) solid else line))
                            addCell(createCell(String.format("%.3f",value0), textStyle, 2, 1, TextAlignment.CENTER, null, line, null, if (row == calibrationDates.size-1) solid else line))
                            addCell(createCell(String.format("%.3f",value1), textStyle, 2, 1, TextAlignment.CENTER, null, line, null, if (row == calibrationDates.size-1) solid else line))
                            addCell(createCell(String.format("%.2f",error1), textStyle, 1, 1, TextAlignment.CENTER, null, solid, null, if (row == calibrationDates.size-1) solid else line))
                            addCell(createCell("", textStyle, 1, 1, TextAlignment.CENTER, null, solid, null, null))

                            errorFound.add( Entry(row.toFloat(), (error1)))
                            errorFoundProm += error1
                            if (!datesCol["value 0"].isNullOrEmpty())
                                cantError += 1
                        }
                        if (cantError > 0)
                            errorFoundProm /= cantError

                        addCell(createCell("", textStyle, 3, 1, TextAlignment.CENTER, solid, solid, null, null))
                        addCell(createCell("Error Span Prom Encontrado", textStyleBold, 2, 1, TextAlignment.CENTER, null, solid, null, solid))
                        addCell(createCell("${String.format("%.2f",errorFoundProm)} %", textStyleBold, 1, 1, TextAlignment.CENTER, null, solid, null, solid))
                        addCell(createCell("", textStyleBold, 3, 1, TextAlignment.CENTER, null, solid, null, null))
                        addCell(createCell("", textStyleBold, 9, 1, TextAlignment.CENTER, solid, solid, null, null))
                        ((tablePage1.getCell(idColError,4).children[0] as Paragraph).children[0] as Text).text = "${String.format("%.2f",errorFoundProm)} %"
                        //tablePage1.getCell(idColError,4).add(Paragraph("${String.format("%.2f",errorFoundProm)} %"))
                        addCell(createCell("", textStyleBold, 1, 2, TextAlignment.LEFT, solid, null, null, null))
                        addCell(createCell("ESCALA", subTitleStyle, 2, 2, TextAlignment.CENTER, solid, solid, solid, solid))
                        addCell(createCell("VALORES DEJADOS", subTitleStyle, 5, 2, TextAlignment.CENTER, null, solid, solid, solid))
                        addCell(createCell("", textStyle, 1, 2, TextAlignment.CENTER, null,solid, null, null))
                        addHeaderManometer(tablePage2, instrumentEntity)
                        errorFoundProm = 0f
                        cantError = 0
                        for (row in 0 until calibrationDates.size) {
                            val datesCol = calibrationDates.getOrElse("values $row") {
                                mutableMapOf<String, String>()
                            } as MutableMap<String, String>
                            if (datesCol["value 0"].toString().isEmpty() || datesCol.isEmpty()) continue
                            val value0 = getValue(datesCol.getOrPut( "value 0"){"0"})
                            val value2 = getValue(datesCol.getOrPut( "value 2"){"0"})
                            val span = instrumentEntity.getSpan()
                            val error2 = CommonUtils.getErrorRelativo(value0, value2, span)
                            addCell(createCell("", textStyle, 1, 1, TextAlignment.CENTER, solid, solid, null, null))
                            addCell(createCell(porcentajeArray[row], textStyle, 1, 1, TextAlignment.CENTER, null, line, null, if (row == calibrationDates.size-1) solid else line))
                            addCell(createCell(String.format("%.3f",value0), textStyle, 1, 1, TextAlignment.CENTER, null, solid, null, if (row == calibrationDates.size-1) solid else line))
                            addCell(createCell(String.format("%.3f",value0), textStyle, 2, 1, TextAlignment.CENTER, null, line, null, if (row == calibrationDates.size-1) solid else line))
                            addCell(createCell(String.format("%.3f",value2), textStyle, 2, 1, TextAlignment.CENTER, null, line, null, if (row == calibrationDates.size-1) solid else line))
                            addCell(createCell(String.format("%.2f",error2), textStyle, 1, 1, TextAlignment.CENTER, null, solid, null, if (row == calibrationDates.size-1) solid else line))
                            addCell(createCell("", textStyle, 1, 1, TextAlignment.CENTER, null, solid, null, null))

                            errorLeft.add( Entry(row.toFloat(), error2))
                            errorFoundProm += error2
                            if (!datesCol["value 0"].isNullOrEmpty())
                                cantError += 1
                        }
                        if (cantError > 0)
                            errorFoundProm /= cantError

                        addCell(createCell("", textStyle, 3, 1, TextAlignment.CENTER, solid, solid, null, null))
                        addCell(createCell("Error Span Prom Dejado", textStyleBold, 2, 1, TextAlignment.CENTER, null, solid, null, solid))
                        addCell(createCell("${String.format("%.2f",errorFoundProm)} %", textStyleBold, 1, 1, TextAlignment.CENTER, null, solid, null, solid))
                        addCell(createCell("", textStyleBold, 3, 1, TextAlignment.CENTER, null, solid, null, null))
                        addCell(createCell("", textStyleBold, 9, 1, TextAlignment.CENTER, solid, solid, null, null))
                        ((tablePage1.getCell(idColError+1,4).children[0] as Paragraph).children[0] as Text).text = "${String.format("%.2f",errorFoundProm)} %"
                        // tablePage1.getCell(idColError+1,4).add(Paragraph("${String.format("%.2f",errorFoundProm)} %"))

                        if (errorFound.isNotEmpty() && errorLeft.isNotEmpty())
                            imageError = createChart(context, errorFound, errorLeft)
                    }

//                "Transmisor 4–20 mA lineal", "Transmisor 4–20 mA cuadratico"->{
                    context.resources.getStringArray(R.array.report_type_options)[1],
                    context.resources.getStringArray(R.array.report_type_options)[2]->{
                        val current =
                            if (instrumentEntity.reportType == context.resources.getStringArray(R.array.report_type_options)[1])
                                arrayOf("4,000", "8,000", "12,000", "16,000", "20,000", "16,000", "12,000", "8,000", "4,000","",
                                    "4,000", "8,000", "12,000", "16,000", "20,000", "16,000", "12,000", "8,000", "4,000","",
                                    "4,000", "8,000", "12,000", "16,000", "20,000", "16,000", "12,000", "8,000", "4,000")
                            else
                                arrayOf("4,000", "12,000", "15,314", "17,856", "20,000", "17,856", "15,314", "12,000", "4,000")
                        var errorFoundProm = 0F
                        cantError = 0
                        for (row in 0 until calibrationDates.size) {
                            if ((row == 9 || row == 19) && instrumentEntity.magnitude == "Flujo Multivariable"){
                                addCell(createCell("", textStyle, 3, 1, TextAlignment.CENTER, solid, solid, null, null))
                                addCell(createCell("Error Span Prom Dejado", textStyleBold, 2, 1, TextAlignment.CENTER, null, solid, null, solid))
                                addCell(createCell("${String.format("%.2f", (errorFoundProm/9))} %", textStyleBold, 1, 1, TextAlignment.CENTER, null, solid, null, solid))
                                addCell(createCell("", textStyleBold, 3, 1, TextAlignment.CENTER, null, solid, null, null))
                                addCell(createCell("", textStyle, 1, 1, TextAlignment.CENTER, solid, null, null, null))
                                addCell(createCell("", textStyle, 7, 1, TextAlignment.CENTER, null, null, null, solid))
                                addCell(createCell("", textStyle, 1, 1, TextAlignment.CENTER, null, solid, null, null))
                                when (row) {
                                    9 -> addHeaderOther(tablePage2, instrumentEntity.verificationUnitTV)
                                    19 -> addHeaderOther(tablePage2, instrumentEntity.verificationUnitQV)
                                }
                                errorFoundProm = 0F
                                cantError = 0
                                continue
                            }
                            val datesCol = calibrationDates.getOrElse("values $row") {
                                mutableMapOf<String, String>()
                            } as MutableMap<String, String>
                            if (datesCol.isEmpty()) continue

                            val value0 = getValue(datesCol.getOrPut( "value 0"){"0"})//PV esperada
                            val value1 = getValue(datesCol.getOrPut( "value 1"){"0"})//corriente leida
                            var error1 = 0f
                            if (value1 != 0f)
                                error1 = CommonUtils.getErrorRelativo((current[row].replace(",",".")).toFloat(), value1, 16F)

                            addCell(createCell("", textStyle, 1, 1, TextAlignment.CENTER, solid, solid, null, null))
                            addCell(createCell(porcentajeArray[row], textStyle, 1, 1, TextAlignment.CENTER, null, line, null, if (row == calibrationDates.size-1) solid else line))
                            addCell(createCell(value0.toString(), textStyle, 1, 1, TextAlignment.CENTER, null, solid, null, if (row == calibrationDates.size-1) solid else line))
                            addCell(createCell(current[row], textStyle, 2, 1, TextAlignment.CENTER, null, line, null, if (row == calibrationDates.size-1) solid else line))
                            addCell(createCell(String.format("%.3f",value1), textStyle, 2, 1, TextAlignment.CENTER, null, line, null, if (row == calibrationDates.size-1) solid else line))
                            addCell(createCell(String.format("%.2f",error1), textStyle, 1, 1, TextAlignment.CENTER, null, solid, null, if (row == calibrationDates.size-1) solid else line))
                            addCell(createCell("", textStyle, 1, 1, TextAlignment.CENTER, null, solid, null, null))

                            errorFound.add( Entry(row.toFloat(), error1))
                            errorFoundProm += error1
                            if (!datesCol["value 0"].isNullOrEmpty())
                                cantError += 1
                        }

                        if (cantError > 0)
                            errorFoundProm /= cantError

                        addCell(createCell("", textStyle, 3, 1, TextAlignment.CENTER, solid, solid, null, null))
                        addCell(createCell("Error Span Prom Encontrado", textStyleBold, 2, 1, TextAlignment.CENTER, null, solid, null, solid))
                        addCell(createCell("${String.format("%.2f",errorFoundProm)} %", textStyleBold, 1, 1, TextAlignment.CENTER, null, solid, null, solid))
                        addCell(createCell("", textStyleBold, 3, 1, TextAlignment.CENTER, null, solid, null, null))
                        addCell(createCell("", textStyleBold, 9, 1, TextAlignment.CENTER, solid, solid, null, null))
                        ((tablePage1.getCell(idColError,4).children[0] as Paragraph).children[0] as Text).text = "${String.format("%.2f",errorFoundProm)} %"
                        //tablePage1.getCell(23,4).add(Paragraph("${String.format("%.2f",errorFoundProm)} %"))

                        if (instrumentEntity.magnitude == "Flujo Multivariable"){
                            addCell(createCell("",textStyle, 9, 3, TextAlignment.CENTER, solid, solid, null, solid))

                            tablePage3 = Table(floatArrayOf(68F, 68F, 91F, 95F, 72F, 68F, 80F, 76F, 58F))
                            tablePage3!!.addCell(createCell("RESULTADOS", titleStyle, 9, 1, TextAlignment.CENTER, solid, solid, solid, null))
                            tablePage3!!.addCell(createCell("", textStyleBold, 1, 2, TextAlignment.LEFT, solid, null, null, null))
                            tablePage3!!.addCell(createCell("ESCALA", subTitleStyle, 2, 2, TextAlignment.CENTER, solid, solid, solid, solid))
                            tablePage3!!.addCell(createCell("VALORES DEJADOS", subTitleStyle, 5, 2, TextAlignment.CENTER, null, solid, solid, solid))
                            tablePage3!!.addCell(createCell("", textStyle, 1, 2, TextAlignment.CENTER, null,solid, null, null))
                            addHeaderOther(tablePage3!!, instrumentEntity.verificationUnitSV)

                            errorFoundProm = 0f
                            cantError = 0
                            for (row in 0 until calibrationDates.size) {
                                if (row == 9 || row == 19){
                                    tablePage3!!.addCell(createCell("", textStyle, 3, 1, TextAlignment.CENTER, solid, solid, null, null))
                                    tablePage3!!.addCell(createCell("Error Span Prom Dejado", textStyleBold, 2, 1, TextAlignment.CENTER, null, solid, null, solid))
                                    tablePage3!!.addCell(createCell("${String.format("%.2f", (errorFoundProm/9))} %", textStyleBold, 1, 1, TextAlignment.CENTER, null, solid, null, solid))
                                    tablePage3!!.addCell(createCell("", textStyleBold, 3, 1, TextAlignment.CENTER, null, solid, null, null))
                                    tablePage3!!.addCell(createCell("", textStyle, 1, 1, TextAlignment.CENTER, solid, null, null, null))
                                    tablePage3!!.addCell(createCell("", textStyle, 7, 1, TextAlignment.CENTER, null, null, null, solid))
                                    tablePage3!!.addCell(createCell("", textStyle, 1, 1, TextAlignment.CENTER, null, solid, null, null))
                                    when (row) {
                                        9 -> addHeaderOther(tablePage3!!, instrumentEntity.verificationUnitTV)
                                        19 -> addHeaderOther(tablePage3!!, instrumentEntity.verificationUnitQV)
                                    }
                                    errorFoundProm = 0F
                                    cantError = 0
                                    continue
                                }
                                val datesCol = calibrationDates.getOrElse("values $row") {
                                    mutableMapOf<String, String>()
                                } as MutableMap<String, String>
                                if (datesCol.isEmpty()) continue
                                val value0 = getValue(datesCol.getOrPut( "value 0"){"0"}) //PV esperada
                                val value2 = getValue(datesCol.getOrPut( "value 2"){"0"}) //Corriente Leida
                                var error2 = 0f
                                if (value2 != 0f)
                                    error2 = CommonUtils.getErrorRelativo((current[row].replace(",",".")).toFloat(), value2, 16F)

                                tablePage3!!.addCell(createCell("", textStyle, 1, 1, TextAlignment.CENTER, solid, solid, null, null))
                                tablePage3!!.addCell(createCell(porcentajeArray[row], textStyle, 1, 1, TextAlignment.CENTER, null, line, null, if (row == calibrationDates.size-1) solid else line))
                                tablePage3!!.addCell(createCell(value0.toString(), textStyle, 1, 1, TextAlignment.CENTER, null, solid, null, if (row == calibrationDates.size-1) solid else line))
                                tablePage3!!.addCell(createCell(current[row], textStyle, 2, 1, TextAlignment.CENTER, null, line, null, if (row == calibrationDates.size-1) solid else line))
                                tablePage3!!.addCell(createCell(String.format("%.3f",value2), textStyle, 2, 1, TextAlignment.CENTER, null, line, null, if (row == calibrationDates.size-1) solid else line))
                                tablePage3!!.addCell(createCell(String.format("%.2f",error2), textStyle, 1, 1, TextAlignment.CENTER, null, solid, null, if (row == calibrationDates.size-1) solid else line))
                                tablePage3!!.addCell(createCell("", textStyle, 1, 1, TextAlignment.CENTER, null, solid, null, null))

                                errorLeft.add( Entry(row.toFloat(), error2))
                                errorFoundProm += error2
                                if (!datesCol["value 0"].isNullOrEmpty())
                                    cantError += 1
                            }
                            if (cantError > 0)
                                errorFoundProm /= cantError

                            tablePage3!!.addCell(createCell("", textStyle, 3, 1, TextAlignment.CENTER, solid, solid, null, null))
                            tablePage3!!.addCell(createCell("Error Span Prom Dejado", textStyleBold, 2, 1, TextAlignment.CENTER, null, solid, null, solid))
                            tablePage3!!.addCell(createCell("${String.format("%.2f",errorFoundProm)} %", textStyleBold, 1, 1, TextAlignment.CENTER, null, solid, null, solid))
                            tablePage3!!.addCell(createCell("", textStyleBold, 3, 1, TextAlignment.CENTER, null, solid, null, null))
                            tablePage3!!.addCell(createCell("", textStyleBold, 9, 1, TextAlignment.CENTER, solid, solid, null, null))
                            ((tablePage1.getCell(idColError+1,4).children[0] as Paragraph).children[0] as Text).text = "${String.format("%.2f",errorFoundProm)} %"
                            //tablePage1.getCell(24,4).add(Paragraph("${String.format("%.2f",errorFoundProm)} %"))
                            imageError = null

                        }else {
                            addCell(createCell("", textStyleBold, 1, 2, TextAlignment.LEFT, solid, null, null, null))
                            addCell(createCell("ESCALA", subTitleStyle, 2, 2, TextAlignment.CENTER, solid, solid, solid, solid))
                            addCell(createCell("VALORES DEJADOS", subTitleStyle, 5, 2, TextAlignment.CENTER, null, solid, solid, solid))
                            addCell(createCell("", textStyle, 1, 2, TextAlignment.CENTER, null, solid, null, null))
                            addHeaderOther(tablePage2, instrumentEntity.verificationUnit)
                            errorFoundProm = 0f
                            cantError = 0
                            for (row in 0 until calibrationDates.size) {
                                if ((row == 9 || row == 19) && instrumentEntity.magnitude == "Flujo Multivariable") {
                                    addCell(createCell("", textStyle, 3, 1, TextAlignment.CENTER, solid, solid, null, null))
                                    addCell(createCell("Error Span Prom Dejado", textStyleBold, 2, 1, TextAlignment.CENTER, null, solid, null, solid))
                                    addCell(createCell("${String.format("%.2f", (errorFoundProm/9))} %", textStyleBold, 1, 1, TextAlignment.CENTER, null, solid, null, solid))
                                    addCell(createCell("", textStyleBold, 3, 1, TextAlignment.CENTER, null, solid, null, null))
                                    addCell(createCell("", textStyle, 9, 1, TextAlignment.CENTER, solid, solid, null, null))
                                    continue
                                }
                                val datesCol = calibrationDates.getOrElse("values $row") { mutableMapOf<String, String>() } as MutableMap<String, String>
                                if (datesCol.isEmpty()) continue
                                val value0 = getValue(datesCol.getOrPut("value 0") { "0" }) //PV esperada
                                val value2 = getValue(datesCol.getOrPut("value 2") { "0" }) //Corriente Leida
                                var error2 = 0f
                                if (value2 != 0f)
                                    error2 = CommonUtils.getErrorRelativo((current[row].replace(",", ".")).toFloat(), value2, 16F)

                                addCell(createCell("", textStyle, 1, 1, TextAlignment.CENTER, solid, solid, null, null))
                                addCell(createCell(porcentajeArray[row], textStyle, 1, 1, TextAlignment.CENTER, null, line, null, if (row == calibrationDates.size - 1) solid else line))
                                addCell(createCell(value0.toString(), textStyle, 1, 1, TextAlignment.CENTER, null, solid, null, if (row == calibrationDates.size - 1) solid else line))
                                addCell(createCell(current[row], textStyle, 2, 1, TextAlignment.CENTER, null, line, null, if (row == calibrationDates.size - 1) solid else line))
                                addCell(createCell(String.format("%.3f", value2), textStyle, 2, 1, TextAlignment.CENTER, null, line, null, if (row == calibrationDates.size - 1) solid else line))
                                addCell(createCell(String.format("%.2f", error2), textStyle, 1, 1, TextAlignment.CENTER, null, solid, null, if (row == calibrationDates.size - 1) solid else line))
                                addCell(createCell("", textStyle, 1, 1, TextAlignment.CENTER, null, solid, null, null))

                                errorLeft.add(Entry(row.toFloat(), error2))
                                errorFoundProm += error2
                                if (!datesCol["value 0"].isNullOrEmpty())
                                    cantError += 1
                            }
                            if (cantError > 0)
                                errorFoundProm /= cantError

                            addCell(createCell("", textStyle, 3, 1, TextAlignment.CENTER, solid, solid, null, null))
                            addCell(createCell("Error Span Prom Dejado", textStyleBold, 2, 1, TextAlignment.CENTER, null, solid, null, solid))
                            addCell(createCell("${String.format("%.2f", errorFoundProm)} %", textStyleBold, 1, 1, TextAlignment.CENTER, null, solid, null, solid))
                            addCell(createCell("", textStyleBold, 3, 1, TextAlignment.CENTER, null, solid, null, null))
                            addCell(createCell("", textStyleBold, 9, 1, TextAlignment.CENTER, solid, solid, null, null))
                            ((tablePage1.getCell(idColError+1,4).children[0] as Paragraph).children[0] as Text).text = "${String.format("%.2f",errorFoundProm)} %"
                            //tablePage1.getCell(24,4).add(Paragraph("${String.format("%.2f",errorFoundProm)} %"))

                            if (errorFound.isNotEmpty() && errorLeft.isNotEmpty())
                                imageError = if (instrumentEntity.magnitude == "Flujo Multivariable")
                                    null
                                else
                                    createChart(context, errorFound, errorLeft)
                        }
                    }

//                "Switch"->{
                    context.resources.getStringArray(R.array.report_type_options)[5]->{
                        for (row in 0 until calibrationDates.size) {
                            val datesCol = calibrationDates.getOrElse("value $row") {
                                mutableMapOf<String, String>()
                            } as MutableMap<String, String>
                            if (datesCol["value 0"].toString().isEmpty() || datesCol.isEmpty()) continue
                            val error1 = CommonUtils.getErrorRelativo(instrumentEntity.verificationMax.replace(",",".").toFloat(), (datesCol.getOrPut("value 0"){"0"}).replace(",",".").toFloat(), instrumentEntity.verificationMax.replace(",",".").toFloat())
                            val error2 = CommonUtils.getErrorRelativo(instrumentEntity.verificationMax.replace(",",".").toFloat(), (datesCol.getOrPut("value 2"){"0"}).replace(",",".").toFloat(), instrumentEntity.verificationMax.replace(",",".").toFloat())

                            addCell(createCell("", textStyle, 1, 2, TextAlignment.CENTER, solid, solid, null, null))
                            addCell(createCell(instrumentEntity.verificationMax, textStyle, 2, 2, TextAlignment.CENTER, null, solid, null, solid))
                            addCell(createCell(datesCol.getOrPut("value 0"){"0"}, textStyle, 2, 2, TextAlignment.CENTER, null, solid, null, solid))
                            addCell(createCell(datesCol.getOrPut("value 1"){"0"}, textStyle, 2, 2, TextAlignment.CENTER, null, solid, null, solid))
                            addCell(createCell(String.format("%2f",error1), textStyle, 1, 2, TextAlignment.CENTER, null, solid, null, solid))
                            addCell(createCell("", textStyle, 1, 2, TextAlignment.CENTER, null, solid, null, null))

                            addCell(createCell("", textStyle, 9, 1, TextAlignment.CENTER, solid, solid, null, null))

                            addCell(createCell("", textStyleBold, 1, 2, TextAlignment.LEFT, solid, null, null, null))
                            addCell(createCell("ESCALA", subTitleStyle, 2, 2, TextAlignment.CENTER, solid, solid, solid, solid))
                            addCell(createCell("VALORES DEJADOS", subTitleStyle, 5, 2, TextAlignment.CENTER, null, solid, solid, solid))
                            addCell(createCell("", textStyle, 1, 2, TextAlignment.CENTER, null,solid, null, null))
                            addHeaderSwitch(tablePage2, instrumentEntity)

                            addCell(createCell("", textStyle, 1, 2, TextAlignment.CENTER, solid, solid, null, null))
                            addCell(createCell(instrumentEntity.verificationMax, textStyle, 2, 2, TextAlignment.CENTER, null, solid, null, solid))
                            addCell(createCell(datesCol.getOrPut("value 2"){"0"}, textStyle, 2, 2, TextAlignment.CENTER, null, solid, null, solid))
                            addCell(createCell(datesCol.getOrPut("value 3"){"0"}, textStyle, 2, 2, TextAlignment.CENTER, null, solid, null, solid))
                            addCell(createCell(String.format("%2f",error2), textStyle, 1, 2, TextAlignment.CENTER, null, solid, null, solid))
                            addCell(createCell("", textStyle, 1, 2, TextAlignment.CENTER, null, solid, null, null))

                            addCell(createCell("", textStyle, 9, 1, TextAlignment.CENTER, solid, solid, null, null))

                            addCell(createCell("", textStyleBold, 2, 1, TextAlignment.LEFT, solid, null, null, null))
                            addCell(createCell("CONDICIONES", subTitleStyle, 5, 1, TextAlignment.CENTER, solid, solid, solid, solid))
                            addCell(createCell("", textStyleBold, 2, 1, TextAlignment.CENTER, null, solid, null, null))

                            addCell(createCell("", textStyleBold, 2, 2, TextAlignment.CENTER, solid, solid, null, null))
                            addCell(createCell("MODO ACCIONAMIENTO", subTitleStyle, 2, 2, TextAlignment.CENTER, null, solid, null, solid))
                            addCell(createCell("CONTACTO ELECTRICO ", subTitleStyle, 3, 2, TextAlignment.CENTER, null,solid, null, solid))
                            addCell(createCell("", textStyleBold, 2, 2, TextAlignment.LEFT, null, solid, null, null))

                            addCell(createCell("", textStyleBold, 2, 2, TextAlignment.CENTER, solid, solid, null, null))
                            addCell(createCell(datesCol.getOrPut("value 4"){"0"}, textStyle, 2, 2, TextAlignment.CENTER, null, solid, null, solid))
                            addCell(createCell(datesCol.getOrPut("value 5"){"0"}, textStyle, 3, 2, TextAlignment.CENTER, null, solid, null, solid))
                            addCell(createCell("", textStyle, 2, 2, TextAlignment.CENTER, solid, solid, null, null))
                        }
                        imageError = null
                    }

//                "ZR"->{
                    context.resources.getStringArray(R.array.report_type_options)[6]->{
                        var errorFoundProm = 0F
                        cantError = 0
                        for (row in 0..1) {
                            val datesCol = calibrationDates.getOrElse("values $row") {
                                mutableMapOf<String, String>()
                            } as MutableMap<String, String>
                            if (datesCol.isEmpty()) continue
                            val value0 = getValue(datesCol.getOrPut( "value 0"){"0"})
                            val value1 = getValue(datesCol.getOrPut( "value 1"){"0"})
                            val error1 = CommonUtils.getErrorRelativo(value0, value1, instrumentEntity.getSpan())

                            addCell(createCell("", textStyle, 1, 1, TextAlignment.CENTER, solid, solid, null, null))
                            addCell(createCell(String.format("%.3f",value0), textStyle, 2, 1, TextAlignment.CENTER, null, solid, null, if (row == calibrationDates.size-11) solid else line))
                            addCell(createCell(String.format("%.3f",value0), textStyle, 2, 1, TextAlignment.CENTER, null, line, null, if (row == calibrationDates.size-11) solid else line))
                            addCell(createCell(String.format("%.3f",value1), textStyle, 2, 1, TextAlignment.CENTER, null, line, null, if (row == calibrationDates.size-11) solid else line))
                            addCell(createCell(String.format("%.2f",error1), textStyle, 1, 1, TextAlignment.CENTER, null, solid, null, if (row == calibrationDates.size-11) solid else line))
                            addCell(createCell("", textStyle, 1, 1, TextAlignment.CENTER, null, solid, null, null))

                            errorFoundProm += error1
                            if (!datesCol["value 0"].isNullOrEmpty())
                                cantError += 1
                        }
                        if (cantError > 0)
                            errorFoundProm /= cantError

                        addCell(createCell("", textStyle, 3, 1, TextAlignment.CENTER, solid, solid, null, null))
                        addCell(createCell("Error Span Prom Encontrado", textStyleBold, 2, 1, TextAlignment.CENTER, null, solid, null, solid))
                        addCell(createCell("${String.format("%.2f",errorFoundProm)} %", textStyleBold, 1, 1, TextAlignment.CENTER, null, solid, null, solid))
                        addCell(createCell("", textStyleBold, 3, 1, TextAlignment.CENTER, null, solid, null, null))
                        addCell(createCell("", textStyleBold, 9, 1, TextAlignment.CENTER, solid, solid, null, null))
                        //tablePage1.getCell(23,4).add(Paragraph("${String.format("%.2f",errorFoundProm)} %"))
                        ((tablePage1.getCell(idColError,4).children[0] as Paragraph).children[0] as Text).text = "${String.format("%.2f",errorFoundProm)} %"
                        addCell(createCell("", textStyleBold, 1, 2, TextAlignment.LEFT, solid, null, null, null))
                        addCell(createCell("ESCALA", subTitleStyle, 2, 2, TextAlignment.CENTER, solid, solid, solid, solid))
                        addCell(createCell("VALORES DEJADOS", subTitleStyle, 5, 2, TextAlignment.CENTER, null, solid, solid, solid))
                        addCell(createCell("", textStyle, 1, 2, TextAlignment.CENTER, null,solid, null, null))
                        addHeaderPH(tablePage2, instrumentEntity)
                        errorFoundProm = 0f
                        cantError = 0

                        for (row in 0..1) {
                            val datesCol = calibrationDates.getOrElse("values $row") {
                                mutableMapOf<String, String>()
                            } as MutableMap<String, String>
                            if (datesCol.isEmpty()) continue
                            val value0 = getValue(datesCol.getOrPut( "value 0"){"0"})
                            val value2 = getValue(datesCol.getOrPut( "value 2"){"0"})
                            var error2 = 0f
                            if (value2 != 0f)
                                error2 = CommonUtils.getErrorRelativo(value0, value2, instrumentEntity.getSpan())

                            addCell(createCell("", textStyle, 1, 1, TextAlignment.CENTER, solid, solid, null, null))
                            addCell(createCell(String.format("%.3f",value0), textStyle, 2, 1, TextAlignment.CENTER, null, solid, null, if (row == calibrationDates.size-11) solid else line))
                            addCell(createCell(String.format("%.3f",value0), textStyle, 2, 1, TextAlignment.CENTER, null, line, null, if (row == calibrationDates.size-11) solid else line))
                            addCell(createCell(String.format("%.3f",value2), textStyle, 2, 1, TextAlignment.CENTER, null, line, null, if (row == calibrationDates.size-11) solid else line))
                            addCell(createCell(String.format("%.2f",error2), textStyle, 1, 1, TextAlignment.CENTER, null, solid, null, if (row == calibrationDates.size-11) solid else line))
                            addCell(createCell("", textStyle, 1, 1, TextAlignment.CENTER, null, solid, null, null))

                            errorFoundProm += error2
                            if (!datesCol["value 0"].isNullOrEmpty())
                                cantError += 1
                        }
                        if (cantError > 0)
                            errorFoundProm /= cantError
//                    errorFoundProm /= 2

                        addCell(createCell("", textStyle, 3, 1, TextAlignment.CENTER, solid, solid, null, null))
                        addCell(createCell("Error Span Prom Dejado", textStyleBold, 2, 1, TextAlignment.CENTER, null, solid, null, solid))
                        addCell(createCell("${String.format("%.2f",errorFoundProm)} %", textStyleBold, 1, 1, TextAlignment.CENTER, null, solid, null, solid))
                        addCell(createCell("", textStyleBold, 3, 1, TextAlignment.CENTER, null, solid, null, null))
                        addCell(createCell("", textStyleBold, 9, 1, TextAlignment.CENTER, solid, solid, null, null))
                        ((tablePage1.getCell(idColError+1,4).children[0] as Paragraph).children[0] as Text).text = "${String.format("%.2f",errorFoundProm)} %"
//                        tablePage1.getCell(24,4).add(Paragraph("${String.format("%.2f",errorFoundProm)} %"))
                        addCell(createCell("", textStyle,9,1,TextAlignment.CENTER,solid,solid,null,null))
                        addCell(createCell("", textStyle,1,1,TextAlignment.CENTER,solid,solid,null,null))
                        addCell(createCell("OTROS RESULTADOS DE CALIBRACION",subTitleStyle,7,1,TextAlignment.CENTER,null,solid,solid,solid))
                        addCell(createCell("", textStyle,1,1,TextAlignment.CENTER,null,solid,null,null))
                        addCell(createCell("", textStyle, 1, 1, TextAlignment.CENTER, solid, null, null, null))
                        addCell(createCell("ITEM", subTitleStyle, 3, 1, TextAlignment.CENTER, solid, solid, null, solid))
                        addCell(createCell("ANTES", subTitleStyle, 1, 1, TextAlignment.CENTER, null, solid, null, solid))
                        addCell(createCell("DESPUES", subTitleStyle, 1, 1, TextAlignment.CENTER, null, solid, null, solid))
                        addCell(createCell("ESTADO", subTitleStyle, 2, 1, TextAlignment.CENTER, null, solid, null, solid))
                        addCell(createCell("", textStyle, 1, 1, TextAlignment.CENTER, null, solid, null, null))

                        for (row in 2 until calibrationDates.size) {
                            val datesCol = calibrationDates.getOrElse("values $row") {
                                mutableMapOf<String, String>()
                            } as MutableMap<String, String>
                            if (datesCol.isEmpty()) continue
                            addCell(createCell("", textStyle, 1, 1, TextAlignment.CENTER, solid, null, null, null))
                            addCell(createCell(datesCol.getOrPut("value 0"){"0"}, subTitleStyle, 3, 1, TextAlignment.CENTER, solid, solid, null, if (row == calibrationDates.size-1) solid else line))
                            addCell(createCell(datesCol.getOrPut("value 1"){"0"}, textStyle, 1, 1, TextAlignment.CENTER, null, solid, null, if (row == calibrationDates.size-1) solid else line))
                            addCell(createCell(datesCol.getOrPut("value 2"){"0"}, textStyle, 1, 1, TextAlignment.CENTER, null, solid, null, if (row == calibrationDates.size-1) solid else line))
                            addCell(createCell(datesCol.getOrPut("value 3"){"0"}, textStyle, 2, 1, TextAlignment.CENTER, null, solid, null, if (row == calibrationDates.size-1) solid else line))
                            addCell(createCell("", textStyle, 1, 1, TextAlignment.CENTER, null, solid, null, null))
                        }
                        imageError = null
                    }

                    context.resources.getStringArray(R.array.report_type_options)[4]->{
                        //val current = arrayOf("4.000","12.000", "20.000")
                        var errorFoundProm = 0F
                        cantError = 0
                        for (row in 0 ..2) {
                            val datesCol = calibrationDates.getOrElse("values $row") {
                                mutableMapOf<String, String>()
                            } as MutableMap<String, String>

                            if (datesCol.isEmpty()) continue

                            val value0 = getValue(datesCol.getOrPut( "value 0"){"0"})//PV esperada
                            val value1 = getValue(datesCol.getOrPut( "value 1"){"0"})//corriente leida
                            val current = getCurrent(
                                (value0 - instrumentEntity.verificationMin.toFloat()) / (instrumentEntity.getSpan() - instrumentEntity.verificationMin.toFloat()),
                                instrumentEntity.output == context.resources.getStringArray(R.array.transmitterOutput)[3])
                            var error1 = 0f
                            if (value1 != 0f)
                                error1 = CommonUtils.getErrorRelativo(current, value1, 16F)
//                                error1 = CommonUtils.getErrorRelativo((current[row].replace(",",".")).toFloat(), value1, 16F)

                            addCell(createCell("", textStyle, 1, 1, TextAlignment.CENTER, solid, solid, null, null))
                            addCell(createCell(porcentajeArray[row], textStyle, 1, 1, TextAlignment.CENTER, null, line, null, if (row == calibrationDates.size-1) solid else line))
                            addCell(createCell(value0.toString(), textStyle, 1, 1, TextAlignment.CENTER, null, solid, null, if (row == calibrationDates.size-1) solid else line))
                            addCell(createCell(current.toString(), textStyle, 2, 1, TextAlignment.CENTER, null, line, null, if (row == calibrationDates.size-1) solid else line))
                            addCell(createCell(String.format("%.3f",value1), textStyle, 2, 1, TextAlignment.CENTER, null, line, null, if (row == calibrationDates.size-1) solid else line))
                            addCell(createCell(String.format("%.2f",error1), textStyle, 1, 1, TextAlignment.CENTER, null, solid, null, if (row == calibrationDates.size-1) solid else line))
                            addCell(createCell("", textStyle, 1, 1, TextAlignment.CENTER, null, solid, null, null))

                            errorFound.add( Entry(row.toFloat(), error1))
                            errorFoundProm += error1
                            if (!datesCol["value 0"].isNullOrEmpty())
                                cantError += 1
                        }

                        if (cantError > 0)
                            errorFoundProm /= cantError

                        addCell(createCell("", textStyle, 3, 1, TextAlignment.CENTER, solid, solid, null, null))
                        addCell(createCell("Error Span Prom Encontrado", textStyleBold, 2, 1, TextAlignment.CENTER, null, solid, null, solid))
                        addCell(createCell("${String.format("%.2f",errorFoundProm)} %", textStyleBold, 1, 1, TextAlignment.CENTER, null, solid, null, solid))
                        addCell(createCell("", textStyleBold, 3, 1, TextAlignment.CENTER, null, solid, null, null))
                        addCell(createCell("", textStyleBold, 9, 1, TextAlignment.CENTER, solid, solid, null, null))
                        ((tablePage1.getCell(idColError,4).children[0] as Paragraph).children[0] as Text).text = "${String.format("%.2f",errorFoundProm)} %"
                        // tablePage1.getCell(23,4).add(Paragraph("${String.format("%.2f",errorFoundProm)} %"))
                        addCell(createCell("", textStyleBold, 1, 2, TextAlignment.LEFT, solid, null, null, null))
                        addCell(createCell("ESCALA", subTitleStyle, 2, 2, TextAlignment.CENTER, solid, solid, solid, solid))
                        addCell(createCell("VALORES DEJADOS", subTitleStyle, 5, 2, TextAlignment.CENTER, null, solid, solid, solid))
                        addCell(createCell("", textStyle, 1, 2, TextAlignment.CENTER, null, solid, null, null))
                        addHeaderOther(tablePage2, instrumentEntity.verificationUnit)
                        errorFoundProm = 0f
                        cantError = 0
                        for (row in 0 until calibrationDates.size) {
                            if ((row == 9 || row == 19) && instrumentEntity.magnitude == "Flujo Multivariable") {
                                addCell(createCell("", textStyle, 3, 1, TextAlignment.CENTER, solid, solid, null, null))
                                addCell(createCell("Error Span Prom Dejado", textStyleBold, 2, 1, TextAlignment.CENTER, null, solid, null, solid))
                                addCell(createCell("${String.format("%.2f", (errorFoundProm/9))} %", textStyleBold, 1, 1, TextAlignment.CENTER, null, solid, null, solid))
                                addCell(createCell("", textStyleBold, 3, 1, TextAlignment.CENTER, null, solid, null, null))
                                addCell(createCell("", textStyle, 9, 1, TextAlignment.CENTER, solid, solid, null, null))
                                continue
                            }
                            val datesCol = calibrationDates.getOrElse("values $row") { mutableMapOf<String, String>() } as MutableMap<String, String>
                            if (datesCol.isEmpty()) continue
                            val value0 = getValue(datesCol.getOrPut("value 0") { "0" }) //PV esperada
                            val value2 = getValue(datesCol.getOrPut("value 2") { "0" }) //Corriente Leida
                            val current = getCurrent(
                                (value0 - instrumentEntity.verificationMin.toFloat()) / (instrumentEntity.getSpan() - instrumentEntity.verificationMin.toFloat()),
                                instrumentEntity.output == context.resources.getStringArray(R.array.transmitterOutput)[3])
                            var error2 = 0f
                            if (value2 != 0f)
                                error2 = CommonUtils.getErrorRelativo(current, value2, 16F)

                            addCell(createCell("", textStyle, 1, 1, TextAlignment.CENTER, solid, solid, null, null))
                            addCell(createCell(porcentajeArray[row], textStyle, 1, 1, TextAlignment.CENTER, null, line, null, if (row == calibrationDates.size - 1) solid else line))
                            addCell(createCell(value0.toString(), textStyle, 1, 1, TextAlignment.CENTER, null, solid, null, if (row == calibrationDates.size - 1) solid else line))
                            addCell(createCell(current.toString(), textStyle, 2, 1, TextAlignment.CENTER, null, line, null, if (row == calibrationDates.size - 1) solid else line))
                            addCell(createCell(String.format("%.3f", value2), textStyle, 2, 1, TextAlignment.CENTER, null, line, null, if (row == calibrationDates.size - 1) solid else line))
                            addCell(createCell(String.format("%.2f", error2), textStyle, 1, 1, TextAlignment.CENTER, null, solid, null, if (row == calibrationDates.size - 1) solid else line))
                            addCell(createCell("", textStyle, 1, 1, TextAlignment.CENTER, null, solid, null, null))

                            errorLeft.add(Entry(row.toFloat(), error2))
                            errorFoundProm += error2
                            if (!datesCol["value 0"].isNullOrEmpty())
                                cantError += 1
                        }
                        if (cantError > 0)
                            errorFoundProm /= cantError

                        addCell(createCell("", textStyle, 3, 1, TextAlignment.CENTER, solid, solid, null, null))
                        addCell(createCell("Error Span Prom Dejado", textStyleBold, 2, 1, TextAlignment.CENTER, null, solid, null, solid))
                        addCell(createCell("${String.format("%.2f", errorFoundProm)} %", textStyleBold, 1, 1, TextAlignment.CENTER, null, solid, null, solid))
                        addCell(createCell("", textStyleBold, 3, 1, TextAlignment.CENTER, null, solid, null, null))
                        addCell(createCell("", textStyleBold, 9, 1, TextAlignment.CENTER, solid, solid, null, null))
                        ((tablePage1.getCell(idColError+1,4).children[0] as Paragraph).children[0] as Text).text = "${String.format("%.2f",errorFoundProm)} %"
                        if (errorFound.isNotEmpty() && errorLeft.isNotEmpty())
                            imageError = createChart(context, errorFound, errorLeft)
                    }

                    else->
                        Log.d("Generacion de certificados","No se reconoce tipo de reporte en ${instrumentEntity.tag}")
                }

                if (imageError != null){
                    addCell(createCell("GRAFICO ERROR",titleStyle, 9, 1, TextAlignment.CENTER, solid, solid, null, null))

                    val imageCell = Cell(1,9).add(imageError)
                    imageCell.setBorder(Border.NO_BORDER)
                    imageCell.setPaddingTop(5f)
                    imageCell.setHorizontalAlignment(HorizontalAlignment.CENTER)
                    imageCell.setVerticalAlignment(VerticalAlignment.MIDDLE)
                    imageCell.setBorderLeft(solid)
                    imageCell.setBorderRight(solid)
                    addCell(imageCell)
                }else {
                    if (tablePage3 != null)
                        tablePage3!!.addCell(createCell("", textStyle, 9, 3, TextAlignment.CENTER, solid, solid, null, null))
                    else
                        addCell(createCell("", textStyle, 9, 3, TextAlignment.CENTER, solid, solid, null, null))
                }
                if (tablePage3 != null)
                    tablePage3!!.addCell(createCell("", textStyle, 9, 1, TextAlignment.CENTER, solid,solid,null,solid))
                else
                    addCell(createCell("", textStyle, 9, 1, TextAlignment.CENTER, solid,solid,null,solid))

            }
            document.add(tablePage1)
            document.add(AreaBreak(AreaBreakType.NEXT_PAGE))
            document.add(tablePage2)
            if (tablePage3 != null){
                document.add(AreaBreak(AreaBreakType.NEXT_PAGE))
                document.add(tablePage3)
            }
        }else{
            document.add(tablePage1)
        }
    }

    private fun getCurrent(ratio: Float, isCuadratico: Boolean): Float {
        return if (isCuadratico)
            sqrt(ratio) * (16) + 4
        else
            ratio * (16) + 4
    }

    private fun addHeaderPH(table: Table, instrumentEntity: InstrumentEntity) {
        with(table){
            addCell(createCell("", textStyleBold, 1, 1, TextAlignment.LEFT, solid, null, null, null))
            addCell(createCell("VALOR PATRON", textStyleBoldPainted, 2, 1, TextAlignment.CENTER, solid, solid, null, line))
            addCell(createCell("EQUIVALENTE", textStyleBoldPainted, 2, 1, TextAlignment.CENTER, null, line, null, line))
            addCell(createCell("LECTURA INSTRUMENTO", textStyleBoldPainted, 2, 1, TextAlignment.CENTER, null, line, null, line))
            addCell(createCell("ERROR SPAN", textStyleBoldPainted, 1, 1, TextAlignment.CENTER, null, solid, null, line))
            addCell(createCell("", textStyle, 1, 1, TextAlignment.CENTER, null,solid, null, null))

            addCell(createCell("", textStyleBold, 1, 1, TextAlignment.CENTER, solid, solid, null, null))
            addCell(createCell("[${instrumentEntity.verificationUnit}]", textStyleBoldPainted, 2, 1, TextAlignment.CENTER, null, solid, null, solid))
            addCell(createCell("[${instrumentEntity.verificationUnit}]", textStyleBoldPainted, 2, 1, TextAlignment.CENTER, null, line, null, solid))
            addCell(createCell("[${instrumentEntity.verificationUnit}]", textStyleBoldPainted, 2, 1, TextAlignment.CENTER, null, line, null, solid))
            addCell(createCell("[%]", textStyleBoldPainted, 1, 1, TextAlignment.CENTER, null, solid, null, solid))
            addCell(createCell("", textStyle, 1, 1, TextAlignment.CENTER, null,solid, null, null))
        }
    }

    private fun addHeaderManometer(table: Table, instrumentEntity: InstrumentEntity) {
        with(table){
            addCell(createCell("", textStyleBold, 1, 1, TextAlignment.LEFT, solid, null, null, null))
            addCell(createCell("PUNTOS", textStyleBoldPainted, 1, 1, TextAlignment.CENTER, solid, line, null, line))
            addCell(createCell("VALOR PATRON", textStyleBoldPainted, 1, 1, TextAlignment.CENTER, null, solid, null, line))
            addCell(createCell("EQUIVALENTE", textStyleBoldPainted, 2, 1, TextAlignment.CENTER, null, line, null, line))
            addCell(createCell("LECTURA INSTRUMENTO", textStyleBoldPainted, 2, 1, TextAlignment.CENTER, null, line, null, line))
            addCell(createCell("ERROR SPAN", textStyleBoldPainted, 1, 1, TextAlignment.CENTER, null, solid, null, line))
            addCell(createCell("", textStyle, 1, 1, TextAlignment.CENTER, null,solid, null, null))

            addCell(createCell("", textStyleBold, 1, 1, TextAlignment.CENTER, solid, solid, null, null))
            addCell(createCell("[%]", textStyleBoldPainted, 1, 1, TextAlignment.CENTER, null, line, null, solid))
            addCell(createCell("[${instrumentEntity.verificationUnit}]", textStyleBoldPainted, 1, 1, TextAlignment.CENTER, null, solid, null, solid))
            addCell(createCell("[${instrumentEntity.verificationUnit}]", textStyleBoldPainted, 2, 1, TextAlignment.CENTER, null, line, null, solid))
            addCell(createCell("[${instrumentEntity.verificationUnit}]", textStyleBoldPainted, 2, 1, TextAlignment.CENTER, null, line, null, solid))
            addCell(createCell("[%]", textStyleBoldPainted, 1, 1, TextAlignment.CENTER, null, solid, null, solid))
            addCell(createCell("", textStyle, 1, 1, TextAlignment.CENTER, null,solid, null, null))
        }
    }

    private fun addHeaderOther(table: Table, verificationUnit : String) {
        with(table){
            addCell(createCell("", textStyleBold, 1, 1, TextAlignment.LEFT, solid, null, null, null))
            addCell(createCell("PUNTOS", textStyleBoldPainted, 1, 1, TextAlignment.CENTER, solid, line, null, line))
            addCell(createCell("VALOR PATRON", textStyleBoldPainted, 1, 1, TextAlignment.CENTER, null, solid, null, line))
            addCell(createCell("CORRIENTE EQUIVALENTE", textStyleBoldPainted, 2, 1, TextAlignment.CENTER, null, line, null, line))
            addCell(createCell("LECTURA INSTRUMENTO", textStyleBoldPainted, 2, 1, TextAlignment.CENTER, null, line, null, line))
            addCell(createCell("ERROR SPAN", textStyleBoldPainted, 1, 1, TextAlignment.CENTER, null, solid, null, line))
            addCell(createCell("", textStyle, 1, 1, TextAlignment.CENTER, null,solid, null, null))

            addCell(createCell("", textStyleBold, 1, 1, TextAlignment.CENTER, solid, solid, null, null))
            addCell(createCell("[%]", textStyleBoldPainted, 1, 1, TextAlignment.CENTER, null, line, null, solid))
            addCell(createCell("[$verificationUnit]", textStyleBoldPainted, 1, 1, TextAlignment.CENTER, null, solid, null, solid))
            addCell(createCell("[mA]", textStyleBoldPainted, 2, 1, TextAlignment.CENTER, null, line, null, solid))
            addCell(createCell("[mA]", textStyleBoldPainted, 2, 1, TextAlignment.CENTER, null, line, null, solid))
            addCell(createCell("[%]", textStyleBoldPainted, 1, 1, TextAlignment.CENTER, null, solid, null, solid))
            addCell(createCell("", textStyle, 1, 1, TextAlignment.CENTER, null,solid, null, null))
        }
    }

    private fun addHeaderSwitch(table: Table, instrumentEntity: InstrumentEntity) {
        with(table){

            addCell(createCell("", textStyleBold, 1, 1, TextAlignment.LEFT, solid, solid, null, null))
            addCell(createCell("RANGO SETTING", textStyleBoldPainted, 2, 1, TextAlignment.CENTER, null, solid, null, line))
            addCell(createCell("LECTURA", textStyleBoldPainted, 2, 1, TextAlignment.CENTER, null, line, null, line))
            addCell(createCell("REPOSICION", textStyleBoldPainted, 2, 1, TextAlignment.CENTER, null, line, null, line))
            addCell(createCell("ERROR", textStyleBoldPainted, 1, 1, TextAlignment.CENTER, null, solid, null, line))
            addCell(createCell("", textStyle, 1, 1, TextAlignment.CENTER, null,solid, null, null))

            addCell(createCell("", textStyleBold, 1, 1, TextAlignment.LEFT, solid, solid, null, null))
            addCell(createCell("[${instrumentEntity.verificationUnit}]", textStyleBoldPainted, 2, 1, TextAlignment.CENTER, null, solid, null, solid))
            addCell(createCell("[${instrumentEntity.verificationUnit}]", textStyleBoldPainted, 2, 1, TextAlignment.CENTER, null, line, null, solid))
            addCell(createCell("[${instrumentEntity.verificationUnit}]", textStyleBoldPainted, 2, 1, TextAlignment.CENTER, null, line, null, solid))
            addCell(createCell("[%]", textStyleBoldPainted, 1, 1, TextAlignment.CENTER, null, solid, null, solid))
            addCell(createCell("", textStyle, 1, 1, TextAlignment.CENTER, null,solid, null, null))
        }
    }

    private fun createCell(content: String, style: Int, colSpan: Int, rowSpan: Int,
                           mHorizontalAlignment : TextAlignment, left : SolidBorder?, right : SolidBorder?,
                           top : SolidBorder?, bottom : SolidBorder?) : Cell {
        titleColor = DeviceRgb(32, 55, 100)
        subTitleColor = DeviceRgb(180,198,231)
        val cell = Cell(rowSpan, colSpan).add(Paragraph(content))
        with(cell){
            setBorder(null)
            setBorderLeft(left)
            setBorderRight(right)
            setBorderTop(top)
            setBorderBottom(bottom)
            setMinHeight(10f * rowSpan)
            setTextAlignment(mHorizontalAlignment)
            setVerticalAlignment(VerticalAlignment.MIDDLE)


            when(style){
                0->{
                    setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                    setFontColor(DeviceRgb.WHITE)
                    setBackgroundColor(titleColor)
                    setFontSize(9f)
                }
                1->{
                    setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                    setBackgroundColor(subTitleColor)
                    setFontSize(9f)
                }
                2->{
                    setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                    setFontSize(7f)
                }
                3->{
                    setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                    setFontSize(7f)
                }
                4->{
                    setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                    setBackgroundColor(subTitleColor)
                    setFontSize(7f)
                }

                else -> {

                }
            }
        }
        return cell

    }

    private fun createChart(context: Context, valuesFound : ArrayList<Entry>, valuesLeft : ArrayList<Entry>) : Image{
        val image : Image
        val lineChart = LineChart(context)
        val dataSets = arrayListOf<ILineDataSet>()

        with(lineChart){
            setDrawGridBackground(false)
            xAxis.setDrawGridLines(false)
            axisRight.isEnabled = false
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1f
            xAxis.valueFormatter = MyXAxisFormatter()
            description.isEnabled = false
            extraBottomOffset = 10f
        }
        val l = lineChart.legend
        with(l){
            form = Legend.LegendForm.LINE
            formSize = 20f
            orientation = Legend.LegendOrientation.HORIZONTAL
            verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            yOffset = 5f
        }

        val dataSetFound = LineDataSet(valuesFound, "Error Encontrado")
        with(dataSetFound){
            lineWidth = 3f
            color = android.graphics.Color.rgb(68,114,196) //Azul
            setDrawValues(false)
            setDrawCircles(false)
            dataSets.add(dataSetFound)
        }

        val dataSetLeft = LineDataSet(valuesLeft, "Error Dejado")
        with(dataSetLeft){
            lineWidth = 1.8f
            color = android.graphics.Color.rgb(237,125,49)//Naranjo
            setDrawValues(false)
            setDrawCircles(false)
            dataSets.add(dataSetLeft)
        }
        val data = LineData(dataSets)
        lineChart.data = data

        lineChart.measure(
            View.MeasureSpec.makeMeasureSpec(1300, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(600, View.MeasureSpec.EXACTLY)
        )
        lineChart.layout(0, 0, lineChart.measuredWidth, lineChart.measuredHeight)
        lineChart.invalidate()

        val bitmap = Bitmap.createBitmap(lineChart.measuredWidth, lineChart.measuredHeight,
            Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        lineChart.draw(canvas)

        val  stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        image = Image(ImageDataFactory.create(stream.toByteArray()))
        image.setHeight(180f)
        image.setHorizontalAlignment(HorizontalAlignment.CENTER)
        return image
    }

    fun getValue(date : String) : Float{
        if (date.isNotEmpty())
            return date.replace(",",".").toFloat()
        return 0f
    }
}
package com.nanobnk.epayment.reporting.service.global

import com.lowagie.text.*
import com.lowagie.text.pdf.PdfWriter
import com.nanobnk.epayment.reporting.service.BuildDetailPdfTable
import com.nanobnk.epayment.reporting.service.RequestSqlDataFromDB
import com.nanobnk.epayment.reporting.utils.MapToModel
import com.nanobnk.epayment.service.utils.StringDateFormaterUtils
import com.nanobnk.epayment.service.utils.TranslateUtils
import com.opencsv.CSVWriter
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.core.env.Environment
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileWriter
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import java.util.stream.Collectors
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


@Component
abstract class AbstractNoticeReportServiceGlobalTranslate {

    companion object : KLogging()

    @Autowired
    lateinit var env: Environment

    @Autowired
    lateinit var translateUtils: TranslateUtils

    @Autowired
    lateinit var buildDetailPdfTable: BuildDetailPdfTable

    @Autowired
    lateinit var requestSqlDataFromDB: RequestSqlDataFromDB

    @Value("\${reporting.document.header.text.en}")
    lateinit var textHeaderEn: String

    @Value("\${reporting.document.header.text.fr}")
    lateinit var textHeaderFr: String

    @Value("\${reporting.document.header.logo.path}")
    lateinit var logoImgPath: String

    @Value("\${reporting.save.report.dir}")
    lateinit var folderToSave: String


    private fun createPdfDocument(fileName: String, title: String) :Document{
        val sep = File.separator
        val document = com.lowagie.text.Document(com.lowagie.text.PageSize.A4)//instanciation du document pdf format paysage.

        val writer = PdfWriter.getInstance(document, FileOutputStream(fileName))
        if(writer.currentPageNumber!=1){
            val titlehead = Phrase(Chunk(title, FontFactory.getFont(FontFactory.TIMES, 7f, Font.NORMAL, null)))
            document.setHeader(HeaderFooter(titlehead, false))
        }
        document.addTitle(title) //"Imprime le : " +
        val phrase = Phrase(Chunk(StringDateFormaterUtils.DateTimeToString.formatFoot(LocalDateTime.now()) + "\t\t\t\t",
                FontFactory.getFont(FontFactory.TIMES, 7f, Font.NORMAL, null)))
        document.setFooter(HeaderFooter(phrase, true))
//        val event = HeaderFooterPageEvent()
//        val event = PDFBackground()
//        writer.setPageEvent(event)
        document.open()
        return document
    }

    @Throws(Exception::class)
    private fun buildDocHeader(document: Document, title: String?, params: Map<String, Any>, labelList: List<String>, fonttype: String, fontsize: Float)
            : com.lowagie.text.Table {
//        if (hm == null) hm = params["hm"] as HashVect
//        val sep = System.getProperty("file.separator")
        val presentation = com.lowagie.text.Table(7)
        presentation.borderWidth = 0f
        presentation.border = 0
        presentation.width = 100f
        presentation.padding = 3f
        presentation.spacing = 0f
        var p: Paragraph? = null

        val phrase = Phrase(Chunk(textHeaderFr, FontFactory.getFont(FontFactory.TIMES, 11f, Font.NORMAL, null)))
        val phraseanglais = Phrase(Chunk(textHeaderEn, FontFactory.getFont(FontFactory.TIMES, 11f, Font.NORMAL, null)))
        // construction de la ligne d’entête du tableau
        p = Paragraph(phrase)
        p.alignment = 0
        var cell = Cell(p)
        cell.rowspan = 5
        cell.colspan = 3
        cell.setHorizontalAlignment(Element.ALIGN_CENTER)
        cell.border = 0
        presentation.addCell(cell)

        val image3 = Image.getInstance(logoImgPath)
        image3.scaleAbsolute(150f, 150f)
        image3.setAbsolutePosition(400f, 550f)
        cell = Cell(image3)
        cell.rowspan = 5
        cell.colspan = 1
        cell.setHorizontalAlignment(Element.ALIGN_CENTER)
        cell.setVerticalAlignment(Element.ALIGN_CENTER)
        cell.border = 0
        presentation.addCell(cell)

        p = Paragraph(phraseanglais)
        p.alignment = 0
        cell = Cell(p)
        cell.rowspan = 5
        cell.colspan = 3
        cell.setHorizontalAlignment(Element.ALIGN_CENTER)
        cell.border = 0
        presentation.addCell(cell)

//        if (title == null) title = params["titre"].toString()
        val ph = Paragraph(Chunk(title, FontFactory.getFont(FontFactory.TIMES, 13f, Font.BOLD, null)))
        cell = Cell(ph)
        cell.rowspan = 2
        cell.colspan = 7
        cell.setHorizontalAlignment(Element.ALIGN_CENTER)
        cell.border = 0
        presentation.addCell(cell)

//        val subTable = com.lowagie.text.Table(9)
//        subTable.borderWidth = 0f
//        subTable.border = 0
//        subTable.width = 100f
//        subTable.padding = 2f
//        subTable.spacing = 0f

        var num =0
        labelList.forEach{key ->
            val phrase = Phrase(Chunk(translateUtils.translate(key), FontFactory.getFont(fonttype, fontsize, Font.BOLD, null)))
            cell = Cell(phrase)
            cell.setHorizontalAlignment(Element.ALIGN_LEFT)
            cell.border = 0
//            cell.colspan=2
            presentation.addCell(cell)
//            subTable.addCell(cell)

            val phraseVal = Phrase(Chunk(params.get(key).toString(), FontFactory.getFont(fonttype, fontsize, Font.NORMAL, null)))
            cell = Cell(phraseVal)
            if(num%3==0){
                cell.colspan= 2
            }//else
//                cell.colspan= 2
            cell.setHorizontalAlignment(Element.ALIGN_LEFT)
            cell.border = 0
            presentation.addCell(cell)
//            subTable.addCell(cell)
            num++
        }

        document.add(presentation)
//        document.add(subTable)

        return presentation
    }

    @Throws(Exception::class)
    private fun parseDetailReportProperties(reportDetail: String) : Map<String, Any>{
        val list = reportDetail.split(";")
        val props = hashMapOf<String, Any>()
        list.forEach { elt ->
            val detail = elt.split("=")
            props.put(detail.first(), detail.last())
        }
        val capts = props.get("captions").toString().split(",")
        props.put("captions", capts)
        val colsSize = props.get("colsize").toString().split(",")
        val cols = colsSize.map { it -> it.toInt() }
        props.put("colsize", cols.toIntArray())
        val fields = props.get("columns").toString().split(",")
        props.put("columns", fields)
        val headfontsize =props.get("headerfontsize")
        headfontsize?.let {
            props.put("headerfontsize", headfontsize.toString().toFloat())
        }

        val detailfontsize = props.get("detailfontsize")
        detailfontsize?.let{
            props.put("detailfontsize", detailfontsize.toString().toFloat())
        }

        val width = props.get("width")
        width?.let{
            props.put("width", width.toString().toFloat())
        }

        return props
    }

    @Throws(Exception::class)
    private fun parseDetailHeaderTable(headerLabel: String, headerVar : String, params: Map<String,Any?>, reportProp: Map<String,Any>) : Map<String, String>{

        var headerVarResult = headerVar
        params.forEach { key, value ->
            val valUsed = if((value as String?).isNullOrBlank()) null else value.toString()
            if(key.contains("date", true)){
                val date = StringDateFormaterUtils.DateToString.formatFr(LocalDate.parse(valUsed))
                headerVarResult = headerVarResult.replace(key, date?:"", true)
            }else{
                //If params don't have value, we write ALL
                headerVarResult = headerVarResult.replace(key, valUsed?:"ALL", true)
            }
        }

        val labelList = headerLabel.split("#")
        val varList = headerVarResult.split("#")
        val props = hashMapOf<String, String>()
        var index=0
        val size = labelList.size
        labelList.forEach { label ->
            props.put(label, varList.get(index))
            index++
        }

        return props
    }


    @Throws(Exception::class)
    fun buildReport(reportDetail: String, params: Map<String,Any?>, colsMap: Map<String,String>, paidNoticeTimeLate :String?) : Map<String, Any>{

        logger.info { reportDetail }
        val reportProp = parseDetailReportProperties(reportDetail)
        logger.info { "After transformation \n"+reportProp }
        val timeFile = StringDateFormaterUtils.DateTimeToString.formatFr(LocalDateTime.now())

        val filesNameList = mutableListOf<String>()
        val locale = LocaleContextHolder.getLocale()

        var baseFileName = reportProp.get("fileName").toString()
        if (locale.language.contains("EN", true)){
            baseFileName = baseFileName.replace("FR","EN", true)
        }

        val filename = folderToSave + baseFileName+"details_" + timeFile + ".pdf"
        filesNameList.add(filename)

        val document = createPdfDocument(filename, reportProp.get("title").toString())

        val headerTableVar = if(locale.language.contains("EN", true))
            reportProp.get("headerTableVar").toString().replace(" au ", " to ", true)
            else reportProp.get("headerTableVar").toString()

        val headerTableMap = translateUtils.translateMap(parseDetailHeaderTable(reportProp.get("headerTableLabel").toString(),
                headerTableVar, params, reportProp))

        val labelList = reportProp.get("headerTableLabel").toString().split("#")
        val title = translateUtils.translate(reportProp.get("title").toString())
        val headerTable = buildDocHeader(document, title, headerTableMap, labelList, reportProp.get("headerfonttype").toString(),
                reportProp.get("headerfontsize") as Float)
//        document.add(headerTable)


        // get result data from DB
        val mapDataList = requestSqlDataFromDB.requestDataFromDB(reportProp.get("viewname").toString(), params, colsMap,
                reportProp.get("orderby").toString(), reportProp, paidNoticeTimeLate)

        //Built MOdel Map DATA to HTML Page
        val noticeResponsesList = MapToModel.NoticeMapToModels.from(mapDataList, reportProp.get("columns") as List<String>)

        val viewBenef = if(!(params.get("beneficiary") as String?).isNullOrBlank()) "notice_beneficiary_reporting_view" else null

        val captions = translateUtils.translateList(reportProp.get("captions") as List<String>)
        val detailTable = buildDetailPdfTable.buildDetailPdfTable(mapDataList, reportProp.get("colsize") as IntArray, reportProp.get("columns") as List<String>,
                captions, reportProp.get("agg_cols").toString(), reportProp.get("detailfonttype").toString(),
                reportProp.get("detailfontsize") as Float, reportProp.get("alterncolor").toString(), reportProp, null,viewBenef)//alterncolor

        document.add(detailTable)
        document.close()

        //Generate CSV file from mapData Result
        val filenameCsv = folderToSave + baseFileName+"details_" + timeFile + ".csv"
        //val filenameCsv2 = folderToSave + reportProp.get("fileName").toString()+"details2_" + timeFile + ".csv"
        //generateCSVFile(filenameCsv, mapData, reportProp.get("columns") as List<String>, reportProp.get("captions") as List<String>)
        generateCSVFileWithCSVWriter(filenameCsv, title, mapDataList, reportProp.get("columns") as List<String>, captions, viewBenef)
        filesNameList.add(filenameCsv)

        val mapResult = hashMapOf<String, Any>()
        mapResult.put("ModelResponse", noticeResponsesList)
        mapResult.put("PDFFile", baseFileName+"details_" + timeFile + ".pdf")
        mapResult.put("CsvFile", baseFileName+"details_" + timeFile + ".csv")
//        return filename
        return mapResult
    }

    private fun generateCSVFileWithCSVWriter(filenameCsv: String, tittle: String, mapData: List<Map<String, Any>>, columns: List<String>,
                                             captions: List<String>, viewBenef: String?){
        var csvFile = File("$filenameCsv")

        if (csvFile.exists()) {
            csvFile.delete()
        }

        csvFile.createNewFile()

        val fileWriter = FileWriter(csvFile)
        val csvWriter = CSVWriter(fileWriter)
//        csvWriter.writeNext(captions.toTypedArray())
        csvWriter.writeNext(captions.toTypedArray())

        try {

            val dateFormat = SimpleDateFormat("dd-MM-yyyy")
            var numLign=1
            mapData.forEach {row ->
                val stringBuilder = StringBuilder()
                val lineData= mutableListOf<String>()
                columns.forEach { field ->
                    var st = ""
                    var align = Element.ALIGN_LEFT
                    val value = if(viewBenef.isNullOrBlank())
                            row.get(field)
                        else if(field.equals("string_agg")) {
                            row.get("beneficiary_name")
                        }else if(field.equals("payment_amount")) {
                            row.get("beneficiary_amount")
                        }else  row.get(field)

                    if (field.equals("num", true)) {
                        st = numLign.toString()
                        align = Element.ALIGN_CENTER
                    } else if (value == null) {
                        st = ""
                    } else if (value is Date) {
                        st = dateFormat.format(value as Date)
                    } else
                        st = value.toString()
                    lineData.add(st)
                }
                csvWriter.writeNext(lineData.toTypedArray())
                numLign++
            }
        } catch (ex:Exception) {
            ex.printStackTrace()
        } finally {
            try {
                csvWriter.flush()
                csvWriter.close()
            } catch (ex:Exception) {
                ex.printStackTrace()
            }
        }
    }


}
package com.nanobnk.epayment.reporting.service

import com.lowagie.text.*
import com.lowagie.text.pdf.PdfWriter
import com.nanobnk.epayment.service.utils.StringDateFormaterUtils
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
abstract class AbstractNoticeReportService {

    companion object : KLogging()

    @Autowired
    lateinit var env: Environment

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    @Qualifier(value = "messageSource")
    lateinit var messageSource: MessageSource

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
        val presentation = com.lowagie.text.Table(5)
        presentation.borderWidth = 0f
        presentation.border = 0
        presentation.width = 100f
        presentation.padding = 5f
        presentation.spacing = 0f
        var p: Paragraph? = null
//        val phrase =  Phrase("REPUBLIQUE DU CAMEROUN\nPaix-Travail-Patrie\n------------\nMINISTERE DES FINANCES\n------------\nDIRECTION GENERALE DES DOUANES");
//        val phraseanglais = Phrase("REPUBLIC OF CAMEROUN\nPeace-work-Fatherland\n------------\nMINISTERY OF FINANCE\n------------\nDIRECTORATE GENERAL OF CUSTOMS");
        val phrase = Phrase(Chunk(textHeaderFr, FontFactory.getFont(FontFactory.TIMES, 12f, Font.NORMAL, null)))
        val phraseanglais = Phrase(Chunk(textHeaderEn, FontFactory.getFont(FontFactory.TIMES, 12f, Font.NORMAL, null)))
        // construction de la ligne d’entête du tableau
        p = Paragraph(phrase)
        p.alignment = 0
        var cell = Cell(p)
        cell.rowspan = 5
        cell.colspan = 2
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
        cell.colspan = 2
        cell.setHorizontalAlignment(Element.ALIGN_CENTER)
        cell.border = 0
        presentation.addCell(cell)

//        if (title == null) title = params["titre"].toString()
        val ph = Paragraph(Chunk(title, FontFactory.getFont(FontFactory.TIMES, 15f, Font.BOLD, null)))
        cell = Cell(ph)
        cell.rowspan = 2
        cell.colspan = 5
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
            val phrase = Phrase(Chunk(key, FontFactory.getFont(fonttype, fontsize, Font.BOLD, null)))
            cell = Cell(phrase)
            cell.setHorizontalAlignment(Element.ALIGN_LEFT)
            cell.border = 0
//            cell.colspan=2
            presentation.addCell(cell)
//            subTable.addCell(cell)

            val phraseVal = Phrase(Chunk(params.get(key).toString(), FontFactory.getFont(fonttype, fontsize, Font.NORMAL, null)))
            cell = Cell(phraseVal)
            if(num%2==0){
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

    private fun initPdfTable(colsLength : IntArray) :Table{
        val table = Table(colsLength.size)
//        table.
        table.setWidths(colsLength)
        table.borderWidth = 1f
        table.border = 0
        table.width = 100f
        table.padding = 1f
        table.spacing = 0f

        return table
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
            val valUsed = if(value==null) null else value.toString()
            if(key.contains("date", true)){
                val date = StringDateFormaterUtils.DateToString.formatFr(LocalDate.parse(valUsed))
                headerVarResult = headerVarResult.replace(key, date?:"", true)
            }else{
                headerVarResult = headerVarResult.replace(key, valUsed?:"", true)
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
    fun buildReport(fileName: String, reportDetail: String, params: Map<String,Any?>, colsMap: Map<String,String>){

        logger.info { reportDetail }
        val reportProp = parseDetailReportProperties(reportDetail)
        logger.info { "After transformation \n"+reportProp }
        val document = createPdfDocument(fileName, reportProp.get("title").toString())

        val headerTableMap = parseDetailHeaderTable(reportProp.get("headerTableLabel").toString(),
                reportProp.get("headerTableVar").toString(), params, reportProp)
       val labelList = reportProp.get("headerTableLabel").toString().split("#")

        val headerTable = buildDocHeader(document, reportProp.get("title").toString(), headerTableMap, labelList, reportProp.get("headerfonttype").toString(),
                reportProp.get("headerfontsize") as Float)
//        document.add(headerTable)

        val mapData = requestSqlDataFromDB.requestDataFromDB(reportProp.get("viewname").toString(), params, colsMap,
                reportProp.get("orderby").toString(), reportProp)


        val detailTable = buildDetailPdfTable.buildDetailPdfTable(mapData, reportProp.get("colsize") as IntArray, reportProp.get("columns") as List<String>,
              reportProp.get("captions") as List<String>, reportProp.get("agg_cols").toString(), reportProp.get("detailfonttype").toString(),
                reportProp.get("detailfontsize") as Float, reportProp.get("alterncolor").toString(), reportProp)//alterncolor

        document.add(detailTable)
        document.close()
    }


    @Throws(Exception::class)
    fun buildReport(reportDetail: String, params: Map<String,Any?>, colsMap: Map<String,String>) : String{

        logger.info { reportDetail }
        val reportProp = parseDetailReportProperties(reportDetail)
        logger.info { "After transformation \n"+reportProp }
        val timeFile = StringDateFormaterUtils.DateTimeToString.formatFr(LocalDateTime.now())

        val filesNameList = mutableListOf<String>()
        val filename = folderToSave + reportProp.get("fileName").toString()+"details_" + timeFile + ".pdf"
        filesNameList.add(filename)

        val document = createPdfDocument(filename, reportProp.get("title").toString())

        val headerTableMap = parseDetailHeaderTable(reportProp.get("headerTableLabel").toString(),
                reportProp.get("headerTableVar").toString(), params, reportProp)
        val labelList = reportProp.get("headerTableLabel").toString().split("#")
        val headerTable = buildDocHeader(document, reportProp.get("title").toString(), headerTableMap, labelList, reportProp.get("headerfonttype").toString(),
                reportProp.get("headerfontsize") as Float)
//        document.add(headerTable)

        val mapData = requestSqlDataFromDB.requestDataFromDB(reportProp.get("viewname").toString(), params, colsMap,
                reportProp.get("orderby").toString(), reportProp)

        val detailTable = buildDetailPdfTable.buildDetailPdfTable(mapData, reportProp.get("colsize") as IntArray, reportProp.get("columns") as List<String>,
                reportProp.get("captions") as List<String>, reportProp.get("agg_cols").toString(), reportProp.get("detailfonttype").toString(),
                reportProp.get("detailfontsize") as Float, reportProp.get("alterncolor").toString(), reportProp)//alterncolor

        document.add(detailTable)
        document.close()

        //Generate CSV file from mapData Result
        val filenameCsv = folderToSave + reportProp.get("fileName").toString()+"details_" + timeFile + ".csv"
        //val filenameCsv2 = folderToSave + reportProp.get("fileName").toString()+"details2_" + timeFile + ".csv"
        //generateCSVFile(filenameCsv, mapData, reportProp.get("columns") as List<String>, reportProp.get("captions") as List<String>)
        generateCSVFileWithCSVWriter(filenameCsv, mapData, reportProp.get("columns") as List<String>, reportProp.get("captions") as List<String>)
        filesNameList.add(filenameCsv)

        //Build summary PDF
        val summaryDefinition = reportProp.get("summary-report-params") as String?
        if(!summaryDefinition.isNullOrBlank()){
            val summaryProp = parseDetailReportProperties(env.getProperty(summaryDefinition.toString()))
            val viewNameSum = summaryProp.get("base-viewname-summary").toString()

            val orderby = summaryProp.get("orderby") as String?
            val groupby = summaryProp.get("groupby") as String?
            val select_args = summaryProp.get("select_args") as String?

            val detailDataSummary = requestSqlDataFromDB.requestSummaryDataFromDB(viewNameSum, params, colsMap,
                    select_args, groupby, orderby, reportProp)

            val fieNameSum = folderToSave + reportProp.get("fileName").toString()+"summary_" + timeFile + ".pdf"
            filesNameList.add(fieNameSum)

            val summaryDoc = createPdfDocument(fieNameSum, reportProp.get("title").toString())

            val headerTable = buildDocHeader(summaryDoc, reportProp.get("title").toString(), headerTableMap, labelList,
                    reportProp.get("headerfonttype").toString(), reportProp.get("headerfontsize") as Float+1)

            val agg_cols = summaryProp.get("agg_cols") as String?
            val listAggCols = agg_cols?.let { agg_cols.split(",") }
            val group_column = summaryProp.get("group_column") as String?
            val width = summaryProp.get("width") as Float?
            val detailSumTable = buildDetailPdfTable.buildDetailSummaryPdfTable(detailDataSummary, summaryProp.get("colsize") as IntArray, width,
                    summaryProp.get("columns") as List<String>, summaryProp.get("captions") as List<String>, listAggCols, group_column,
                    reportProp.get("detailfonttype").toString(), reportProp.get("detailfontsize") as Float +2, reportProp.get("alterncolor").toString(), reportProp)//alterncolor

            summaryDoc.add(detailSumTable)
            summaryDoc.close()
        }

        //Create Zip file to return
//        val zipFilename = folderToSave + reportProp.get("fileName") + timeFile + ".zip"
//        val fileZip: File=createZipFile(filesNameList, zipFilename)

        return filename
//        return zipFilename
    }

    private fun createZipFile(filesNameList: MutableList<String>, zipFilename: String): File{

        //Create zip file
        val fileZip= File(zipFilename)
        val fos = FileOutputStream(fileZip)
        val zipOut = ZipOutputStream(fos)
        val filesList = filesNameList.map { filename ->
            File(filename)
        }.toList()
        filesList.forEach { file ->
            val fis = FileInputStream(file)
            val zipEntry = ZipEntry(file.name)
            zipOut.putNextEntry(zipEntry)

            val bytes = ByteArray(1024)
            var length= fis.read(bytes)
            while((length ) >= 0) {
                zipOut.write(bytes, 0, length)
                length= fis.read(bytes)
            }
            fis.close()
        }
        zipOut.flush()
        zipOut.close()
        fos.close()

        filesList.forEach { file ->
            file.delete()
        }

        return fileZip
    }

    private fun generateCSVFile(filenameCsv: String, mapData: List<Map<String, Any>>, columns: List<String>, captions: List<String>){
        var csvFile = File("$filenameCsv")

        if (csvFile.exists()) {
            csvFile.delete()
        }

        csvFile.createNewFile()

        val csvHeader = captions.stream().map(this::escapeSpecialCharacters)
                .collect(Collectors.joining(","))
        val csvWriter = FileWriter(csvFile)
        csvWriter.write(csvHeader)

        try {

            val dateFormat = SimpleDateFormat("dd-MM-yyyy")
            var numLign=1
            mapData.forEach {row ->
                val stringBuilder = StringBuilder()
                columns.forEach { field ->
                    var st = ""
                    var align = Element.ALIGN_LEFT
                    val value = row.get(field)
                    if (field.equals("num", true)) {
                        st = numLign.toString()
                        align = Element.ALIGN_CENTER
                    } else if (value == null) {
                        st = ""
                    } else if (value is Date) {
                        st = dateFormat.format(value as Date)
                    } else
                        st = value.toString()
                    stringBuilder.append(",").append(escapeSpecialCharacters(st))
                }
                if(stringBuilder.length>1)
                    csvWriter.write("\n"+stringBuilder.substring(1))
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

    private fun escapeSpecialCharacters(data: String): String {
        var data = data
        var escapedData = data.replace("\\R".toRegex(), " ")
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"")
            escapedData = "\"" + data + "\""
        }
        return escapedData
    }

    private fun generateCSVFileWithCSVWriter(filenameCsv: String, mapData: List<Map<String, Any>>, columns: List<String>, captions: List<String>){
        var csvFile = File("$filenameCsv")

        if (csvFile.exists()) {
            csvFile.delete()
        }

        csvFile.createNewFile()

        val fileWriter = FileWriter(csvFile)
        val csvWriter = CSVWriter(fileWriter)
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
                    val value = row.get(field)
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

    private fun translate(codeString: String):String{
        val locale = LocaleContextHolder.getLocale()
        return  messageSource.getMessage(codeString, emptyArray(), codeString, locale)
    }

    private fun translateList(list: List<String>): List<String>{
        val locale = LocaleContextHolder.getLocale()

        return  list.map { code ->
            messageSource.getMessage(code, emptyArray(), code, locale)
        }.toList()
    }
}
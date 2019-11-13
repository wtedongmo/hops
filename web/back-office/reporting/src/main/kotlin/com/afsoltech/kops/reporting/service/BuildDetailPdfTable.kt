package com.nanobnk.epayment.reporting.service

import com.lowagie.text.*
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import java.awt.Color
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
class BuildDetailPdfTable {

    fun initPdfTable(colsLength : IntArray) :Table{
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

    fun initPdfTable(colsLength : IntArray, width: Float?) :Table{
        val table = Table(colsLength.size)
//        table.
        table.setWidths(colsLength)
        table.borderWidth = 1f
        table.border = 0
        table.width = width?:100f
        table.padding = 1f
        table.spacing = 0f

        return table
    }


    @Throws(Exception::class)
    fun buildDetailPdfTable(listobjtab: MutableList<Map<String, Any>>, cols: IntArray, fields: List<String>, capts: List<String>,
                            agg_cols: String?, fonttype: String, fontsize: Float, alterncolor: String, reportProps: Map<String, Any>) : Table {

        val list = mutableListOf<Map<String, String>>()
        return buildDetailPdfTable(listobjtab, cols, fields, capts, agg_cols, fonttype, fontsize, alterncolor, reportProps, null, null)
    }


        @Throws(Exception::class)
    fun buildDetailPdfTable(listobjtab: MutableList<Map<String, Any>>, cols: IntArray, fields: List<String>, capts: List<String>,
                            agg_cols: String?, fonttype: String, fontsize: Float, alterncolor: String, reportProps: Map<String, Any>,
                            listMapValue: MutableList<Map<String, String>>?, viewBenef: String? ) : Table {

        val gr = initPdfTable(cols)
        for (st in capts) {
            val cell = Cell(Chunk(st, FontFactory.getFont(fonttype, fontsize, Font.BOLD, null)))//FontFactory.TIMES
            cell.backgroundColor = Color.LIGHT_GRAY
            cell.horizontalAlignment = Element.ALIGN_CENTER
            gr.addCell(cell)
        }
        gr.endHeaders()

        val dateFormat = SimpleDateFormat("dd-MM-yyyy")
        val decf = DecimalFormat("#,##0")
        var sumAgg= 0.0
        var color: Color?=null
        var numLign=1

        listobjtab.forEach { row ->
//            val mapValue = hashMapOf<String, String>()
            fields.forEach { field ->
                var st = ""
                var align = Element.ALIGN_LEFT

                val value =if(viewBenef.isNullOrBlank())
                                    row.get(field)
                                else if(field.equals("string_agg")) {
                                    row.get("beneficiary_name")
                                }else if(field.equals("payment_amount")) {
                                    row.get("beneficiary_amount")
                                }else  row.get(field)

                if (field.equals("num", true)) {
                    st = numLign.toString()
                    align = Element.ALIGN_CENTER
                } else if(value ==null){
                    st = ""
                }else if (value is Date) {
                    st = dateFormat.format(value as Date)
                } else if (value is Double) {
                    st = decf.format((value as Double))
                    align = Element.ALIGN_RIGHT
                } else if (value is BigDecimal) {
                    st = decf.format((value as BigDecimal).toDouble())
                    align = Element.ALIGN_RIGHT
                } else
                    st = value.toString()

//                mapValue.put(field, st)

                val cell = Cell(Chunk(st, FontFactory.getFont(fonttype, fontsize, Font.NORMAL, null))) //FontFactory.TIMES
                cell.backgroundColor=color
                cell.horizontalAlignment=align
                cell.verticalAlignment= Element.ALIGN_CENTER
                gr.addCell(cell)
                if(field.equals(agg_cols, true) && value!=null){
                    sumAgg += (value as BigDecimal).toDouble()
                }
            }
            numLign++
//            liatMapValue.add(mapValue)
            if(alterncolor.equals("1"))
                color = if (numLign % 2 == 0) Color.LIGHT_GRAY else null
        }
        if(!agg_cols.isNullOrBlank()) {
            var cell = Cell(Chunk("Total", FontFactory.getFont(FontFactory.TIMES, 13f, Font.BOLD, null)))
            cell.colspan = cols.size - 2
            cell.backgroundColor = color
            cell.horizontalAlignment = Element.ALIGN_CENTER
            gr.addCell(cell)
            cell = Cell(Chunk(decf.format(sumAgg), FontFactory.getFont(FontFactory.TIMES, 13f, Font.BOLD, null)))
            cell.colspan = 2
            cell.horizontalAlignment = Element.ALIGN_CENTER
            cell.backgroundColor = color
            gr.addCell(cell)
        }

        return gr
    }

    @Throws(Exception::class)
    fun buildDetailSummaryPdfTable(listobjtab: MutableList<Map<String, Any>>, cols: IntArray, width: Float?, fields: List<String>, capts: List<String>,
                                   listAggCols: List<String>?, group_column: String?, fonttype: String, fontsize: Float, alterncolor: String, reportProps: Map<String, Any>) : Table{

        val gr = initPdfTable(cols, width)
        for (st in capts) {
            val cell = Cell(Chunk(st, FontFactory.getFont(fonttype, fontsize, Font.BOLD, null)))//FontFactory.TIMES
            cell.backgroundColor = Color.LIGHT_GRAY
            cell.horizontalAlignment = Element.ALIGN_CENTER
            gr.addCell(cell)
        }
        gr.endHeaders()

        //Try to get number of line from group column
        val groupColMapNumber = mutableMapOf<String, Int>()
        listobjtab.forEach { row ->
            val value = row.get(group_column) as String?
            val number = groupColMapNumber.get(value)
            if(number==null)
                groupColMapNumber.put(value?:"",1)
            else
                groupColMapNumber.put(value?:"",number+1)
        }

        val dateFormat = SimpleDateFormat("dd-MM-yyyy")
        val decf = DecimalFormat("#,##0")
        var sumAamount= 0.0
        var sumTotalAmount= 0.0
        var color: Color?=null
        var sumNumber=0
        var sumTodalNumber=0
        var valueGroupCol=""
        var internSumMap = mutableMapOf<String,Double>()
        val totalSumMap = mutableMapOf<String,Double>()

        listobjtab.forEach { row ->
            fields.forEach { field ->
                var st = ""
                var align = Element.ALIGN_LEFT
                val value =row.get(field)

                if (value==null){
                    st = ""
                }else if (value is Date) {
                    st = dateFormat.format(value as Date)
                } else if (value is Int) {
                    st = decf.format((value as Double))
                    align = Element.ALIGN_RIGHT
                }else if (value is Double) {
                    st = decf.format((value as Double))
                    align = Element.ALIGN_RIGHT
                } else if (value is BigDecimal) {
                    st = decf.format((value as BigDecimal).toDouble())
                    align = Element.ALIGN_RIGHT
                } else if (field.startsWith("number", true)){
                    align = Element.ALIGN_RIGHT
                    st = value.toString()
                } else
                    st = value.toString()

                if(field.equals(group_column, true)){
                    if(!valueGroupCol.equals("") && !listAggCols.isNullOrEmpty() && !value.toString().equals(valueGroupCol)){
                        // Insert row of internal total
                        val cell = Cell(Chunk("Total", FontFactory.getFont(fonttype, fontsize, Font.BOLD, null))) //FontFactory.TIMES
                        cell.colspan = gr.columns - (listAggCols.size +1)
                        cell.backgroundColor = Color.LIGHT_GRAY
                        cell.horizontalAlignment = Element.ALIGN_CENTER
                        cell.verticalAlignment = Element.ALIGN_CENTER
                        gr.addCell(cell)
                        listAggCols?.forEach { it ->
                            val cell = Cell(Chunk(decf.format(internSumMap.get(it)), FontFactory.getFont(fonttype, fontsize, Font.BOLD, null))) //FontFactory.TIMES
//                            cell.colspan = gr.columns - (listAggCols.size +1)
                            cell.backgroundColor = Color.LIGHT_GRAY
                            cell.horizontalAlignment = Element.ALIGN_RIGHT
                            cell.verticalAlignment = Element.ALIGN_CENTER
                            gr.addCell(cell)
                        }
                    }
                    if(valueGroupCol.equals("") || !value.toString().equals(valueGroupCol)){
                        // Insert column of group
                        val nberLine = groupColMapNumber.get(value)
                        val cell = Cell(Chunk(st, FontFactory.getFont(fonttype, fontsize, Font.BOLD, null))) //FontFactory.TIMES
                        cell.rowspan = nberLine!!+1
//                        cell.backgroundColor=color
                        cell.horizontalAlignment=Element.ALIGN_CENTER
                        cell.verticalAlignment=Element.ALIGN_CENTER
                        gr.addCell(cell)
                        valueGroupCol = value.toString()
                            internSumMap = mutableMapOf<String,Double>()
                    }
                }else {
                    val cell = Cell(Chunk(st, FontFactory.getFont(fonttype, fontsize, Font.NORMAL, null))) //FontFactory.TIMES
//                    cell.backgroundColor = color
                    cell.horizontalAlignment = align
                    cell.verticalAlignment = Element.ALIGN_CENTER
                    gr.addCell(cell)
                    listAggCols?.let {
                        //check amd calcul sum
                        if(listAggCols.contains(field)){
                            val total = totalSumMap.get(field)
                            val valDouble = value.toString().toDouble()
                            if(total==null)
                                totalSumMap.put(field,valDouble)
                            else
                                totalSumMap.put(field,valDouble+total)

                            val interTotal = internSumMap.get(field)
                            if(interTotal==null)
                                internSumMap.put(field,valDouble)
                            else
                                internSumMap.put(field,valDouble+interTotal)
                        }
                    }
                }
            }

        }

        // Insert last internal Total
        if(!valueGroupCol.equals("") && !listAggCols.isNullOrEmpty()) {
            // Insert row of internal total
            val cell = Cell(Chunk("Total", FontFactory.getFont(fonttype, fontsize, Font.BOLD, null))) //FontFactory.TIMES
            cell.colspan = gr.columns - (listAggCols.size + 1)
            cell.backgroundColor = Color.LIGHT_GRAY
            cell.horizontalAlignment = Element.ALIGN_CENTER
            cell.verticalAlignment = Element.ALIGN_CENTER
            gr.addCell(cell)
            listAggCols?.forEach { it ->
                val cell = Cell(Chunk(decf.format(internSumMap.get(it)), FontFactory.getFont(fonttype, fontsize, Font.BOLD, null))) //FontFactory.TIMES
//                cell.colspan = gr.columns - (listAggCols.size + 1)
                cell.backgroundColor = Color.LIGHT_GRAY
                cell.horizontalAlignment = Element.ALIGN_RIGHT
                cell.verticalAlignment = Element.ALIGN_CENTER
                gr.addCell(cell)
            }
        }

        //General total
        if(!valueGroupCol.equals("") && !listAggCols.isNullOrEmpty()) {
            val cell = Cell(Chunk("Grand Total", FontFactory.getFont(fonttype, fontsize+2, Font.BOLD, null))) //FontFactory.TIMES
            cell.colspan = gr.columns - (listAggCols.size )
            cell.backgroundColor = Color.LIGHT_GRAY
            cell.horizontalAlignment = Element.ALIGN_CENTER
            cell.verticalAlignment = Element.ALIGN_CENTER
            gr.addCell(cell)
            listAggCols.forEach { it ->
                val cell = Cell(Chunk(decf.format(totalSumMap.get(it)), FontFactory.getFont(fonttype, fontsize+2, Font.BOLD, null))) //FontFactory.TIMES
//                cell.colspan = gr.columns - (listAggCols.size + 1)
                cell.backgroundColor = Color.LIGHT_GRAY
                cell.horizontalAlignment = Element.ALIGN_RIGHT
                cell.verticalAlignment = Element.ALIGN_CENTER
                gr.addCell(cell)
            }
        }

        return gr
    }
}
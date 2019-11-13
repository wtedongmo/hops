package com.nanobnk.epayment.reporting.utils

import com.nanobnk.epayment.model.inbound.NoticeReportResponseDto
import com.nanobnk.epayment.model.inbound.NoticeResponseDto
import java.lang.reflect.Field
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.kotlinProperty

class MapToModel {

    object NoticeMapToModels {
        fun from(datas: List<Map<String, Any>>, listCols: List<String>): List<NoticeReportResponseDto> {
            val models = mutableListOf<NoticeReportResponseDto>()
            val decf = DecimalFormat("#,##0")
            val dateFormat = SimpleDateFormat("dd-MM-yyyy")
            var cols: List<String>? = null
            datas.forEach { it ->
                if(cols==null)
                    cols = it.entries.mapNotNull { elt ->
                        elt.key }
                models.add(NoticeMapToModel.from(it, cols!!, decf, dateFormat))
            }
            return models
        }
    }


    object NoticeMapToModel {
        fun from(row: Map<String, Any>, listCols: List<String>, decf: DecimalFormat, dateFormat: SimpleDateFormat): NoticeReportResponseDto {

            val result = NoticeReportResponseDto()
            val fields = result::class.memberProperties
            listCols.forEach { col ->
                val property =  fields.find { it.name.equals(col, true) }
                property?.let {
                    if (property is KMutableProperty<*>) {
                        var value = row.get(col)

                        if(value ==null){
                            value=""
                        } else if(value is BigDecimal)
                            value = decf.format((value as BigDecimal).toDouble())
                        else if(value is Date)
                            value = dateFormat.format(value as Date)

                        property.setter.call(result, value.toString())
                    }
                }
            }

            return result
        }
    }


}
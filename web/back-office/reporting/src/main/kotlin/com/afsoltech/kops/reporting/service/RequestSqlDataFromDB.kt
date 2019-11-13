package com.nanobnk.epayment.reporting.service

import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.env.Environment
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.SqlTypeValue
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component
import java.sql.SQLType
import java.sql.Types
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class RequestSqlDataFromDB {

    companion object : KLogging()

//    @Autowired
//    lateinit var env: Environment

    @Autowired
    lateinit var jdbcTemplate: NamedParameterJdbcTemplate
//    lateinit var jdbcTemplate: JdbcTemplate

    @Value("\${reporting-requestdata-sql-base-filter}")
    lateinit var reportSqlFilter: String

    @Throws(Exception::class)
    fun requestDataFromDB(viewName: String, params: Map<String,Any?>, colsMap: Map<String,String>, orderby: String?, reportProps: Map<String, Any>)
            : MutableList<Map<String, Any>>{

        return requestDataFromDB(viewName, params, colsMap, orderby, reportProps, null)
    }

    @Throws(Exception::class)
    fun requestDataFromDB(viewName: String, params: Map<String,Any?>, colsMap: Map<String,String>, orderby: String?, reportProps: Map<String, Any>,
                          paidNoticeTimeLate: String?)
            : MutableList<Map<String, Any>>{

        val viewBenef = if(!(params.get("beneficiary") as String?).isNullOrBlank()) "notice_beneficiary_reporting_view" else null
        var query = "select * from ${viewBenef?:viewName} "
        val listVal = mutableListOf<Any>()
        val mapVal = hashMapOf<String,Any>()
        val stBuild = StringBuilder()
        val mapSqlParams = MapSqlParameterSource()
        //val paramsSQL = MapSqlParameterSource()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        params.forEach { key, value ->
            if(value == null || value.toString().isNullOrBlank()){
                //do nothing
            } else if(key.contains("startDate", true)){
//                stBuild.append(" and ").append(colsMap.get(key)).append(">='").append(value).append("'")
                stBuild.append(" and ").append(colsMap.get(key)).append(">= :").append(key.trim())
                mapSqlParams.addValue(key, value, Types.DATE)
//                mapSqlParams.addValue(colsMap.get(key)!!, LocalDateTime.parse((value as String)+" 00:00", formatter), Types.TIME)
                //paramsSQL.addValue(key, LocalDate.parse(value.toString()).atStartOfDay())
//                mapVal.add(LocalDate.parse(value.toString()).atStartOfDay())
            }else if(key.contains("endDate", true)){
//                stBuild.append(" and ").append(colsMap.get(key)).append("<='").append(value).append("'")
                val endDate = LocalDate.parse(value.toString())
                stBuild.append(" and ").append(colsMap.get(key)).append("<= :").append(key.trim())
                mapSqlParams.addValue(key, endDate.plusDays(1).toString(), Types.DATE)
//                mapSqlParams.addValue(colsMap.get(key)!!, LocalDateTime.parse((value as String)+" 00:00", formatter), Types.TIME)
                //paramsSQL.addValue(key, LocalDate.parse(value.toString()).atStartOfDay())
//                listVal.add(LocalDate.parse(value.toString()).atStartOfDay())
            } else {
//                stBuild.append(" and ").append(colsMap.get(key)).append("='").append(value).append("'")
                stBuild.append(" and ").append(colsMap.get(key)).append("= :").append(colsMap.get(key)?.trim())
                mapSqlParams.addValue(colsMap.get(key)!!, value as String)
                //paramsSQL.addValue(key.toString(), value.toString())
//                listVal.add(value)
            }
//            paramsSQL.addValue(colsMap.get(key), value)
        }
        val requestFilterMap = reportSqlFilter.split(";")
                .map { it ->
                    val detail = it.split("#")
                    detail.first() to detail.last()
                }.toMap()

        val requestFilter = requestFilterMap.get(reportProps.get("report-variable-condition-name")?:"")

        if(stBuild.length>6 && requestFilter != null) {
            query += " where " + stBuild.toString().substring(4) + " and $requestFilter"
        }else if (requestFilter != null){
            query += " where $requestFilter"
        }else {
            query += " where " + stBuild.toString().substring(4)
        }

        if(!paidNoticeTimeLate.isNullOrBlank()){
            if(paidNoticeTimeLate.equals("TIME")){
                query +=" and payment_date<=due_date "
            }else if(paidNoticeTimeLate.equals("LATE")){
                query +=" and payment_date>due_date "
            }
        }
        if(!orderby.isNullOrBlank())
            query += " order by "+orderby
//        val result = jdbcTemplate.queryForList(query, mapVal)
//        val connection = jdbcTemplate.dataSource.connection
//        val prepStat = connection.prepareStatement(query)
//        prepStat.setString(1, "FFGGG")
//        prepStat.setString(2, "FFGGG")
//        val resultPrep = prepStat.resultSet
//        val result = jdbcTemplate.queryForList(query)
        val result = jdbcTemplate.queryForList(query, mapSqlParams)
//        val result = jdbcTemplate.queryForList(query, paramsSQL)
        AbstractNoticeReportService.logger.info { "\nresult of query [$query] is: \n $result" }

        return result
    }

    @Throws(Exception::class)
    fun requestSummaryDataFromDB(viewName: String, params: Map<String,Any?>, colsMap: Map<String,String>, select_args: String?, groupby: String?,
                                 orderby: String?, reportProps: Map<String, Any>) : MutableList<Map<String, Any>>{

        var query = if(select_args.isNullOrBlank()) "select * from $viewName " else "select $select_args from $viewName "
        val listVal = mutableListOf<Map<String,Any>>()
        val mapVal = hashMapOf<String,Any>()
        val stBuild = StringBuilder()
        val paramsSQL = MapSqlParameterSource()

        params.forEach { key, value ->
            if(value == null || value.toString().isNullOrBlank()){
                //do nothing
            } else if(key.contains("startDate", true)){
//                stBuild.append(" and ").append(colsMap.get(key)).append(">='").append(value).append("'")
                stBuild.append(" and ").append(colsMap.get(key)).append(">= :").append(key.trim())
                paramsSQL.addValue(key, value, Types.DATE)
                //paramsSQL.addValue(key, LocalDate.parse(value.toString()).atStartOfDay())
//                mapVal.add(LocalDate.parse(value.toString()).atStartOfDay())
            }else if(key.contains("endDate", true)){
//                stBuild.append(" and ").append(colsMap.get(key)).append("<='").append(value).append("'")
                stBuild.append(" and ").append(colsMap.get(key)).append("<= :").append(key.trim())
                paramsSQL.addValue(key, value, Types.DATE)
//                paramsSQL.addValue(key, LocalDate.parse(value.toString()).atStartOfDay())
//                listVal.add(LocalDate.parse(value.toString()).atStartOfDay())
            } else {
//                stBuild.append(" and ").append(colsMap.get(key)).append("='").append(value).append("'")
                stBuild.append(" and ").append(colsMap.get(key)).append("= :").append(colsMap.get(key)?.trim())
                paramsSQL.addValue(colsMap.get(key)!!, value as String)
                //paramsSQL.addValue(key.toString(), value.toString())
//                listVal.add(value)
            }
//            paramsSQL.addValue(colsMap.get(key), value)
        }
        val requestFilterMap = reportSqlFilter.split(";")
                .map { it ->
                    val detail = it.split("#")
                    detail.first() to detail.last()
                }.toMap()

        val requestFilter = requestFilterMap.get(reportProps.get("report-variable-condition-name")?:"")

        if(stBuild.length>6 && requestFilter != null) {
            query += " where " + stBuild.toString().substring(4) + " and $requestFilter"
        }else if (requestFilter != null){
            query += " where $requestFilter"
        }else {
            query += " where " + stBuild.toString().substring(4)
        }

        if(!groupby.isNullOrBlank())
            query += " group by $groupby"
        if(!orderby.isNullOrBlank())
            query += " order by $orderby"
        val result = jdbcTemplate.queryForList(query, paramsSQL)
//        val result = jdbcTemplate.queryForList(query)
//        val result = jdbcTemplate.queryForList(query, paramsSQL)
        logger.info { "\nresult of query [$query] is: \n $result" }

        return result
    }
}
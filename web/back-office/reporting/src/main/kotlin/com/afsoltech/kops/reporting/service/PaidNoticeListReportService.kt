package com.nanobnk.epayment.reporting.service

import com.nanobnk.epayment.model.attribute.UserType
import com.nanobnk.epayment.reporting.utils.PaidNoticeAppName
import com.nanobnk.epayment.reporting.utils.PaidNoticeReportModel
import com.nanobnk.epayment.repository.InboundParticipantRepository
import com.nanobnk.epayment.repository.OutboundParticipantRepository
import com.nanobnk.epayment.repository.UserRepository
import com.nanobnk.epayment.service.utils.StringDateFormaterUtils
import com.nanobnk.util.rest.extentions.trimToNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.env.Environment
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class PaidNoticeListReportService(val userRepository: UserRepository, val inboundParticipantRepository: InboundParticipantRepository,
                                  val outboundParticipantRepository: OutboundParticipantRepository)
    : AbstractNoticeReportServiceTranslate() {

    @Value("\${paid-customs-list-report}")
    lateinit var reportDetail: String

    @Throws(Exception::class)
    fun paidNoticeListReport(startDate: String, endDate: String, office: String?): String { // Must return file name

        val filename = folderToSave + "FR_01_ap_list_details_" + StringDateFormaterUtils.DateTimeToString.formatFr(LocalDateTime.now()) + ".pdf"
        val mapVal = hashMapOf<String, Any?>()
        val mapCols = hashMapOf<String, String>()
        mapVal.put("endDate", endDate)
        mapCols.put("endDate", "payment_date")
        mapVal.put("startDate", startDate)
        mapCols.put("startDate", "payment_date")
        mapVal.put("office", office)
        if (office != null) {
            mapCols.put("office", "office_name")
        }

        buildReport(filename, reportDetail, mapVal, mapCols)
        return filename
    }

    @Throws(Exception::class)
    fun genericPaidNoticeListReport(paidNoticeModel: PaidNoticeReportModel) : List<String>{

        val listMap = parsePaidNoticeNameValue(paidNoticeModel)
        val mapVal = listMap.get(0)
        val mapCols = listMap.get(1)
        val paidNoticeAppName = PaidNoticeAppName.getInstance()
        val reportNameMap = paidNoticeAppName.reportNameMap
        val appReportName = env.getProperty(reportNameMap.get(paidNoticeModel.reportCode))

        val mapColsSt= mapCols.map { entry ->
            entry.key to entry.value.toString()
        }.toMap()
        return buildReport(appReportName, mapVal, mapColsSt)
    }


    private fun parsePaidNoticeNameValue(paidNoticeModel: PaidNoticeReportModel): List<Map<String, Any?>>{
        val mapVal = hashMapOf<String, Any?>()
        val mapCols = hashMapOf<String, String>()

        mapVal.put("endDate", paidNoticeModel.endDate)
        mapCols.put("endDate", "payment_date")
        mapVal.put("startDate", paidNoticeModel.startDate)
        mapCols.put("startDate", "payment_date")
//        var office = paidNoticeModel.office
        mapVal.put("office", paidNoticeModel.office) //!!.trimToNull()
        if (!paidNoticeModel.office.isNullOrBlank()) {
            mapCols.put("office", "office_name")
        }

        mapVal.put("notice_type", paidNoticeModel.noticeType)
        if (!paidNoticeModel.noticeType.isNullOrBlank()) {
            mapCols.put("notice_type", "notice_type")
        }

        mapVal.put("participant", paidNoticeModel.participant)
        if (!paidNoticeModel.participant.isNullOrBlank()) {
            mapCols.put("participant", "participant_name")
        }

        mapVal.put("beneficiary", paidNoticeModel.beneficiary)
        if (!paidNoticeModel.beneficiary.isNullOrBlank()) {
            mapCols.put("beneficiary", "beneficiary_name")
        }

        mapVal.put("taxpayer", paidNoticeModel.taxpayerNumber)
        if (!paidNoticeModel.taxpayerNumber.isNullOrBlank()) {
            mapCols.put("taxpayer", "taxpayer_number")
        }

        mapVal.put("payment_category", paidNoticeModel.paymentCategory)
        if (!paidNoticeModel.paymentCategory.isNullOrBlank()) {
            mapCols.put("payment_category", "payment_category")
        }

        mapVal.put("payment_method", paidNoticeModel.paymentMethod)
        if (!paidNoticeModel.paymentMethod.isNullOrBlank()) {
            mapCols.put("payment_method", "payment_method")
        }

        mapVal.put("declaration_type", paidNoticeModel.declarationType)
        if (!paidNoticeModel.declarationType.isNullOrBlank()) {
            mapCols.put("declaration_type", "declaration_type")
        }

        mapVal.put("cda_number", paidNoticeModel.taxpayerRepresentNumber)
        if (!paidNoticeModel.taxpayerRepresentNumber.isNullOrBlank()) {
            mapCols.put("cda_number", "cda_number")
        }

        mapVal.put("notice_number", paidNoticeModel.noticeNumber)
        if (!paidNoticeModel.noticeNumber.isNullOrBlank()) {
            mapCols.put("notice_number", "notice_number")
        }

        mapVal.put("payment_number", paidNoticeModel.transactionNumber)
        if (!paidNoticeModel.transactionNumber.isNullOrBlank()) {
            mapCols.put("payment_number", "payment_number")
        }

        //Filter request with participant code if user is a participant
        if(paidNoticeModel.reportCode==115){
            val auth = SecurityContextHolder.getContext().authentication
            val username = auth.principal.toString()
            val user = userRepository.findByUsername(username)
            if(user!=null && user.type!!.equals(UserType.PARTICIPANT)){
                val participantList = user.participantAssociation
                if(participantList.isNotEmpty()){
                    val participant = inboundParticipantRepository.getOne(participantList.get(0).participantId)
                    if(participant!=null){
                        mapVal.put("participant", participant.participantCode)
                        mapCols.put("participant", "participant_code")
                    }else{
                        mapVal.put("participant", "UNKNOW")
                        mapCols.put("participant", "participant_code")
                    }
                }else{
                    mapVal.put("participant", "UNKNOW")
                    mapCols.put("participant", "participant_code")
                }
            }
        }

        // Provider or Beneficiary User Report
        if(paidNoticeModel.reportCode==117){
            val auth = SecurityContextHolder.getContext().authentication
            val username = auth.principal.toString()
            val user = userRepository.findByUsername(username)
            if(user!=null && user.type!!.equals(UserType.PROVIDER)){
                val providerList = user.participantAssociation
                if(providerList.isNotEmpty()){
                    val provider = outboundParticipantRepository.getOne(providerList.get(0).participantId)
                    if(provider!=null){
                        mapVal.put("beneficiary", provider.participantCode)
                        mapCols.put("beneficiary", "beneficiary_code")
                    }else{
                        mapVal.put("beneficiary", "UNKNOW")
                        mapCols.put("beneficiary", "beneficiary_code")
                    }
                }else{
                    mapVal.put("beneficiary", "UNKNOW")
                    mapCols.put("beneficiary", "beneficiary_code")
                }
            }
        }

        return listOf(mapVal, mapCols)
    }

}


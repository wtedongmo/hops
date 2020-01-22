package com.afsoltech.hops.service.ws

import com.afsoltech.core.entity.app.fee.FeeAppliedType
import com.afsoltech.core.exception.BadRequestException
import com.afsoltech.core.repository.app.ProviderRepository
import com.afsoltech.core.repository.app.fee.ProviderFeeRepository
import com.afsoltech.core.service.utils.LoadSettingDataToMap
import com.afsoltech.core.util.enforce
import com.afsoltech.hops.core.entity.customs.temp.SelectedNotice
import com.afsoltech.hops.core.model.notice.BillFeeDto
import com.afsoltech.hops.core.repository.temp.SelectedNoticeBeneficiaryRepository
import com.afsoltech.hops.core.repository.temp.SelectedNoticeRepository
import mu.KLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class CalculateFeeNoticeService(val selectedNoticeRepository: SelectedNoticeRepository,
                                val selectedNoticeBenfRepository: SelectedNoticeBeneficiaryRepository,
                                val providerRepository: ProviderRepository, val providerFeeRepository: ProviderFeeRepository) {

    companion object : KLogging()

    //    @Value("\${app.bank.code.initial}")
//    private var bankCode: String=""

    //    @Value("\${app.provider.notice.code}")
//    private var providerNoticeCode: String=""

//    init{
//        bankCode = LoadBaseDataToMap.settingMap.get("app.bank.code.initial")?.value?: ""
//        providerNoticeCode = LoadBaseDataToMap.settingMap.get("app.provider.notice.code")?.value?: ""
//    }


    fun calculateFeeNotice(userLogin: String): BillFeeDto { //:Boolean

        val selectedNoticeList = selectedNoticeRepository.findByUserLogin(userLogin)

        return calculateFee(selectedNoticeList)
    }

    fun calculateFeeNotice(noticeNumberList: List<String>): BillFeeDto { //:Boolean

        val selectedNoticeList = selectedNoticeRepository.findListNoticeNumber(noticeNumberList)

        return calculateFee(selectedNoticeList)
    }

    fun calculateFee(selectedNoticeList: List<SelectedNotice>): BillFeeDto {

        if(selectedNoticeList.isEmpty()){
            throw BadRequestException("Error.Selected.NotFound")
        }
        var totalAmount =BigDecimal.ZERO
        var externalAmount =BigDecimal.ZERO

        val noticeIdList = mutableListOf<Long>()
        selectedNoticeList.forEach {notice ->
            totalAmount += notice.amount!!
            noticeIdList.add(notice.id!!)
//            notice.beneficiaryList.forEach { benef ->
//                externalAmount += if(benef.accountNumber!!.startsWith(LoadBaseDataToMap.bankCode)) BigDecimal.ZERO else benef.amount!!
//            }
        }

        val benefList = selectedNoticeBenfRepository.findBySelectedNoticeList(noticeIdList)
        benefList.forEach { benef ->
            externalAmount += if(benef.accountNumber!!.startsWith(LoadSettingDataToMap.bankCode)) BigDecimal.ZERO else benef.amount!!
        }

        return calculateFee(totalAmount, externalAmount)
    }

    @Transactional
    @Synchronized
    fun calculateFee(totalAmount: BigDecimal, externalAmount: BigDecimal) : BillFeeDto {
        enforce(totalAmount > BigDecimal.ZERO, listOf(totalAmount)) { "Invalid.Amount" }
        enforce(externalAmount >= BigDecimal.ZERO, listOf(externalAmount)) { "Invalid.Amount" }

//        bankCode = LoadBaseDataToMap.settingMap.get("app.bank.code.initial")?.value?: ""
//        providerNoticeCode = LoadBaseDataToMap.settingMap.get("app.provider.notice.code")?.value?: ""

        var feeAmount = BigDecimal.ZERO
        val providerOp = providerRepository.findOneByCode(LoadSettingDataToMap.providerNoticeCode)
        if(providerOp.isPresent) {
            val provider = providerOp.get()
            val providerFeeList = providerFeeRepository.findByAmountIntervalAndProviderAndFeeType(totalAmount, provider.id!!,
                    FeeAppliedType.SERVICE)
            val feeService = providerFeeList.map { fee ->
                fee.getFeeAmount(totalAmount)
            }.sumByDouble { it -> it.toDouble() }.toBigDecimal()
            feeAmount += feeService


            val providerFeeAddList = providerFeeRepository.findByAmountIntervalAndProviderAndFeeType(externalAmount,
                    provider.id!!, FeeAppliedType.ADDITIONAL)
            val feeAdd = providerFeeAddList.map { fee ->
                fee.getFeeAmount(externalAmount)
            }.sumByDouble { it -> it.toDouble() }.toBigDecimal()
            feeAmount += feeAdd

            return BillFeeDto(totalAmount, feeAmount, totalAmount + feeAmount)
        }

        throw BadRequestException("Error.Provider.NotFound", listOf(LoadSettingDataToMap.providerNoticeCode))
    }

}
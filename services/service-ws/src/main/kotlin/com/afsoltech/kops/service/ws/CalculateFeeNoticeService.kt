package com.afsoltech.kops.service.ws

import com.afsoltech.core.entity.fee.FeeAppliedType
import com.afsoltech.core.exception.BadRequestException
import com.afsoltech.core.repository.ProviderRepository
import com.afsoltech.core.repository.fee.ProviderFeeRepository
import com.afsoltech.core.util.enforce
import com.afsoltech.kops.core.entity.customs.temp.SelectedNotice
import com.afsoltech.kops.core.model.BillFeeDto
import com.afsoltech.kops.core.model.integration.UnpaidNoticeResponseDto
import com.afsoltech.kops.core.repository.temp.SelectedNoticeBeneficiaryRepository
import com.afsoltech.kops.core.repository.temp.SelectedNoticeRepository
import com.afsoltech.kops.service.mapper.NoticeModelToEntity
import com.afsoltech.kops.service.integration.ListUnpaidNoticeService
import mu.KLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class CalculateFeeNoticeService(val selectedNoticeRepository: SelectedNoticeRepository, val providerRepository: ProviderRepository,
                                val providerFeeRepository: ProviderFeeRepository) {

    companion object : KLogging()

    @Value("\${app.bank.code.initial}")
    lateinit var operatorCode: String

    @Value("\${app.notice.provider.code}")
    lateinit var providerNoticeCode: String


    fun calculateFeeNotice(userLogin: String): BillFeeDto { //:Boolean

        val selectedNoticeList = selectedNoticeRepository.findByUserLogin(userLogin)

        return calculateFee(selectedNoticeList)
    }

    fun calculateFee(selectedNoticeList: List<SelectedNotice>): BillFeeDto {

        if(selectedNoticeList.isEmpty()){
            throw BadRequestException("Kops.Error.Selected.NotFound")
        }
        var totalAmount =BigDecimal.ZERO
        var externalAmount =BigDecimal.ZERO

        selectedNoticeList.forEach {notice ->
            totalAmount += notice.amount!!
            notice.beneficiaryList.forEach { benef ->
                externalAmount += if(benef.accountNumber!!.startsWith(operatorCode)) BigDecimal.ZERO else benef.amount!!
            }
        }

        return calculateFee(totalAmount, externalAmount)
    }

    @Transactional
    @Synchronized
    fun calculateFee(totalAmount: BigDecimal, externalAmount: BigDecimal) : BillFeeDto{
        enforce(totalAmount <= BigDecimal.ZERO, listOf(totalAmount)) { "Invalid.Amount" }
        enforce(externalAmount < BigDecimal.ZERO, listOf(externalAmount)) { "Invalid.Amount" }

        var feeAmount = BigDecimal.ZERO
        val providerOp = providerRepository.findByParticipantCode(providerNoticeCode)
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

        throw BadRequestException("Kops.Error.Provider.NotFound", listOf(providerNoticeCode))
    }

}
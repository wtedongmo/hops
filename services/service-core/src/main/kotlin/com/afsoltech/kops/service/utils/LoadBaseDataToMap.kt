package com.afsoltech.kops.service.utils

import com.afsoltech.core.entity.BankAuthCode
import com.afsoltech.core.entity.ParameterData
import com.afsoltech.core.entity.PaymentMode
import com.afsoltech.core.model.attribute.BaseStatus
import com.afsoltech.core.repository.BankAuthCodeRepository
import com.afsoltech.core.repository.ParameterDataRepository
import com.afsoltech.core.repository.PaymentModeRepository
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
class LoadBaseDataToMap (val bankAuthCodeRepository: BankAuthCodeRepository, val paymentModeRepository: PaymentModeRepository,
                         val parameterDataRepository: ParameterDataRepository) {

    companion object {
        val bankAuthCodeMap = hashMapOf<String, BankAuthCode>()
        val paymentModeMap = hashMapOf<String, PaymentMode>()
        val parameterDataMap = hashMapOf<String, ParameterData>()
        var bankAuthCodeApproved: BankAuthCode?= null
    }

    init{
        if(bankAuthCodeMap.isEmpty()){
            val listbankAuthCode = bankAuthCodeRepository.findByStatus(BaseStatus.ACTIVE)
            listbankAuthCode.forEach { it ->
                bankAuthCodeMap.put(it.code!!, it)
                if(it.isApprouvedCode)
                    bankAuthCodeApproved = it
            }
        }

        if(paymentModeMap.isEmpty()){
            val listPaymentMode = paymentModeRepository.findByStatus(BaseStatus.ACTIVE)
            listPaymentMode.forEach { it ->
                paymentModeMap.put(it.code!!, it)
            }
        }

        if(parameterDataMap.isEmpty()){
            val listParameterData = parameterDataRepository.findByStatus(BaseStatus.ACTIVE)
            listParameterData.forEach { it ->
                parameterDataMap.put(it.code!!, it)
            }
        }
    }


}
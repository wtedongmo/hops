package com.nanobnk.epayment.portal.utils

import com.nanobnk.epayment.repository.DeclarationTypeRepository
import com.nanobnk.epayment.repository.IssuerOfficeRepository
import com.nanobnk.epayment.repository.NoticeTypeRepository
import com.nanobnk.epayment.repository.PaymentCategoryRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
class LoadBaseDataFromDB {

    @Autowired
    lateinit var noticeTypeRepository: NoticeTypeRepository

    @Autowired
    lateinit var declarationTypeRepository: DeclarationTypeRepository

    @Autowired
    lateinit var issuerOfficeRepository: IssuerOfficeRepository

    @Autowired
    lateinit var paymentCategoryRepository: PaymentCategoryRepository

    val noticeTypeMap = hashMapOf<String, String>()
    val declarationTypeMap = hashMapOf<String, String>()
    val issuerOfficeMap = hashMapOf<String, String>()
    val paymentCategoryMap = hashMapOf<String, String>()

    fun loadBaseData(){
        if(noticeTypeMap.isEmpty()){
            val listNoticeType = noticeTypeRepository.findAll()
            listNoticeType.forEach { it ->
                noticeTypeMap.put(it.code?:"", it.name?:"")
            }
        }

        if(declarationTypeMap.isEmpty()){
            val listDecType = declarationTypeRepository.findAll()
            listDecType.forEach { it ->
                declarationTypeMap.put(it.code?:"", it.name?:"")
            }
        }

        if(issuerOfficeMap.isEmpty()){
            val listOffice = issuerOfficeRepository.findAll()
            listOffice.forEach { it ->
                issuerOfficeMap.put(it.code?:"", it.name?:"")
            }
        }

        if(paymentCategoryMap.isEmpty()){
            val listCategory = paymentCategoryRepository.findAll()
            listCategory.forEach { it ->
                paymentCategoryMap.put(it.code?:"", it.name?:"")
            }
        }
    }


}
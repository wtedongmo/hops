//package com.nanobnk.epayment.portal.repository
//
//import com.nanobnk.epayment.model.attribute.BankStatus
//import com.nanobnk.epayment.portal.entity.Bank
//import com.nanobnk.util.jpa.repository.BaseRepository
//
//interface BankRepository : BaseRepository<Bank, Long> {
//    fun findByBankCode(bankCode: String): Bank?
//    fun findByBankStatus(bankStatus: BankStatus): List<Bank>
//}
//

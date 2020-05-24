package com.afsoltech.config

import com.afsoltech.core.entity.app.Provider
import com.afsoltech.core.entity.app.fee.FeeAppliedType
import com.afsoltech.core.entity.app.fee.ProviderFee
import com.afsoltech.core.model.attribute.BaseStatus
import com.afsoltech.core.repository.app.ProviderRepository
import com.afsoltech.core.repository.app.fee.ProviderFeeRepository
import com.afsoltech.core.service.app.utils.EncryptDecryptUtils
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.math.BigDecimal

@ConditionalOnProperty(name = ["spring.jpa.hibernate.ddl-auto"], havingValue = "create")
@Configuration
class CoreDevDataConfig {

    companion object : KLogging()

    @Autowired
    lateinit var feeRepository: ProviderFeeRepository

    @Autowired
    lateinit var providerRepo: ProviderRepository

    private var provider = Provider()

    @Bean
    fun initBillers() = CommandLineRunner {
        logger.info { "init OutboundParticipants" }
        val providerL = Provider()
        providerL.let {
            it.id = 1
            it.code = "CUSTOMS"
            it.name = "Cameroon Customs Information System ePayment"
            it.status = BaseStatus.ACTIVE
            it.providerKey = "4xZjMg3z3yMSAWKQ0sox2onUjZk8js9k"
            it.providerKeyPass = "APIKEY"
            it.providerBaseUrl = "http://localhost:42500/api/v1/epayment"
         }
        providerL.providerKey = EncryptDecryptUtils.Encryption.encrypt(providerL, providerL.providerKey!!)

        provider = providerRepo.save(providerL)
    }


    @Bean
    fun initFees() = CommandLineRunner {

        logger.info { "init Fees" }

        feeRepository.save(
                ProviderFee(id = 1, feeAppliedType = FeeAppliedType.SERVICE, feeFixed = BigDecimal(300), maxAmount = BigDecimal(500000000),
                        description = "Customs Services Fee fixe", provider = provider)
        )

        feeRepository.save(
                ProviderFee(id = 2, feeAppliedType = FeeAppliedType.ADDITIONAL, maxFee = BigDecimal(500), feeRate = 5.0, maxAmount = BigDecimal(500000000),
                        description = "Customs Additional Fee", provider = provider)
        )
    }

}
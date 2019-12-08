package com.nanobnk.config

import com.afsoltech.core.entity.Provider
import com.afsoltech.core.entity.fee.FeeAppliedType
import com.afsoltech.core.entity.fee.ProviderFee
import com.afsoltech.core.model.attribute.ParticipantStatus
import com.afsoltech.core.repository.ProviderRepository
import com.afsoltech.core.repository.fee.ProviderFeeRepository
import com.afsoltech.core.service.utils.EncryptDecryptUtils
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
            it.participantCode = "CUSTOMS"
            it.participantName = "Cameroon Customs Information System ePayment"
            it.participantStatus = ParticipantStatus.ACTIVE
            it.providerKey = "KEY"
            it.providerKeyPass = "KEY"
            it.providerBaseUrl = "http://localhost:42601/camcis/rest/"
         }
        providerL.providerKey = EncryptDecryptUtils.Encryption.encrypt(providerL, providerL.providerKey!!)

        provider = providerRepo.save(providerL)
    }


    @Bean
    fun initFees() = CommandLineRunner {

        logger.info { "init Fees" }

        feeRepository.save(
                ProviderFee(feeAppliedType =FeeAppliedType.SERVICE, feeFixed=BigDecimal(300), maxAmount= BigDecimal(50000000),
                        description = "Customs Service Fee fixe", provider = provider)
        )

        feeRepository.save(
                ProviderFee(feeAppliedType =FeeAppliedType.ADDITIONAL, maxFee=BigDecimal(500), feeRate=5.0, maxAmount= BigDecimal(50000000),
                        description = "Customs Additional Fee", provider = provider)
        )
    }

}
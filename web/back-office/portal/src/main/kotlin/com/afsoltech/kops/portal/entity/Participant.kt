package com.nanobnk.epayment.portal.entity

import com.nanobnk.epayment.model.attribute.BankStatus
import org.hibernate.annotations.Immutable
import javax.persistence.*

@Entity
@Table(name = "participant_view")
@Immutable
data class Participant(
        @Id
        @Column(name = "participant_id")
        var participantId: Long? = null,

//        @Basic(optional = false)
        @Column(name = "participant_code") // unique = true,
        var participantCode: String? = null,

        @Column(name = "participant_name")
        var participantName: String? = null,

//        @Basic(optional = false)
//        @Column(name = "BANK_ABREVIATION", columnDefinition = "varchar(20)")
//        var bankAbreviation: String? = null,

        @Column(name = "payment_link")
        var paymentLink: String? = null,

        @Column(name = "participant_status")
        var participantStatus: String?=null
)
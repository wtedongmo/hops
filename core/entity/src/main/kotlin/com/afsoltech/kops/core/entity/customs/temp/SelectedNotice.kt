package com.afsoltech.kops.core.entity.customs.temp

import com.afsoltech.core.entity.BaseAudit
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import java.math.BigDecimal
import java.time.LocalDate
import javax.persistence.*


//@EntityListeners(TransactionEntityListener::class)
@Entity
@Table(name = "SELECTED_NOTICE")
data class SelectedNotice(

        @SequenceGenerator(name = "SELECTED_NOTICE_ID", sequenceName = "SELECTED_NOTICE_ID", allocationSize = 1)
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SELECTED_NOTICE_ID")
        @Id
        @Column(name = "SELECTED_NOTICE_ID")
        var id: Long? = null,

        @Column(name = "REMOTE_NOTICE_ID")
        var remoteNoticeId: Long? = null,

        @Column(name = "NOTICE_NUMBER", columnDefinition = "varchar(100)", unique = true)
        @Basic(optional = false)
        var noticeNumber: String? = null,

        @Column(name = "NOTIFICATION_DATE")
        @Basic(optional = false)
        var notificationDate: LocalDate? = null,

        @Basic(optional = false)
        @Column(name = "NOTICE_TYPE", columnDefinition = "char(1)")
        var noticeType: String? = null,

        @Column(name = "REFERENCE_NUMBER", columnDefinition = "varchar(100)")
        var referenceNumber: String? = null,

        @Column(name = "DECLARATION_TYPE", columnDefinition = "char(3)")
        var declarationType: String? = null,

        @Column(name = "TAXPAYER_NUMBER", columnDefinition = "varchar(100)")
        @Basic(optional = false)
        var taxpayerNumber: String? = null,

        //@Basic(optional = false)
        @Column(name = "TAXPAYER_NAME")
        var taxpayerName: String? = null,

        @Column(name = "CDA_NUMBER", columnDefinition = "varchar(100)", nullable = true)
        //@Basic(optional = false)
        var cdaNumber: String? = null,

        @Column(name = "CDA_CODE", columnDefinition = "varchar(100)")
        var cdaCode: String? = null,

        @Column(name = "CDA_NAME")
        var cdaName: String? = null,

        @Column(name = "ISSUER_OFFICE", columnDefinition = "char(5)")
        var issuerOffice: String? = null,

        @Column(name = "DUE_DATE")
        var dueDate: LocalDate? = null,

        @Basic(optional = false)
        @Column(name = "AMOUNT")
        var amount: BigDecimal? = null,


        @Column(name = "USER_LOGIN")
        var userLogin: String?= null,

        @Fetch(FetchMode.JOIN)
        @OneToMany(mappedBy = "selectedNotice", cascade = [(CascadeType.ALL)])
        var beneficiaryList: MutableList<SelectedNoticeBeneficiary> = ArrayList()


) : BaseAudit()




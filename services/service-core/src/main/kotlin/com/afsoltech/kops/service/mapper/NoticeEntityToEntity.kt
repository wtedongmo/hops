package com.nanobnk.epayment.service.mapper

import com.afsoltech.kops.core.entity.customs.Notice
import com.afsoltech.kops.core.entity.customs.NoticeBeneficiary
import com.afsoltech.kops.core.entity.customs.temp.SelectedNotice
import com.afsoltech.kops.core.entity.customs.temp.SelectedNoticeBeneficiary

class NoticeEntityToEntity {

    object SelectedNoticeEntityToNoticeEntity {
        fun from(entity: SelectedNotice): Notice {
            val result = Notice(
                    noticeNumber = entity.noticeNumber,
                    notificationDate = entity.notificationDate,
                    noticeType = entity.noticeType,
                    referenceNumber = entity.referenceNumber,
                    declarationType = entity.declarationType,
                    taxpayerNumber = entity.taxpayerNumber,
                    taxpayerName = entity.taxpayerName,
                    cdaNumber = entity.cdaNumber,
                    cdaCode = entity.cdaCode,
                    cdaName = entity.cdaName,
                    issuerOffice = entity.issuerOffice,
                    dueDate = entity.dueDate,
                    amount = entity.amount
                    )
            //result.listNoticeBeneficiary = OutboundNoticeBeneficiaryEntityToEntities.from(entity.listNoticeBeneficiary)
            return result
        }

    }

    object SelectedNoticeEntityToNoticeEntities {
        fun from(entities: List<SelectedNotice>): MutableList<Notice> {
            val results = mutableListOf<Notice>()

            entities.forEach { it ->
                results.add(SelectedNoticeEntityToNoticeEntity.from(it))
            }

            return results
        }
    }

    object SelectedNoticeBeneficiaryEntityToEntity {
        fun from(entity: SelectedNoticeBeneficiary): NoticeBeneficiary {
            val result = NoticeBeneficiary(
                    beneficiaryName = entity.beneficiaryName,
                    beneficiaryCode = entity.beneficiaryCode,
                    bankCode = entity.bankCode,
                    accountNumber = entity.accountNumber,
                    amount = entity.amount)
            return result
        }
    }

    object SelectedNoticeBeneficiaryEntityToEntities {
        fun from(entities: List<SelectedNoticeBeneficiary>): MutableList<NoticeBeneficiary> {
            val results = mutableListOf<NoticeBeneficiary>()

            entities.forEach { it ->
                results.add(SelectedNoticeBeneficiaryEntityToEntity.from(it))
            }

            return results
        }
    }

}
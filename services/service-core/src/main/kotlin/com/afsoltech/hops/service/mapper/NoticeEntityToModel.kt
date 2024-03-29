package com.afsoltech.epayment.service.mapper

import com.afsoltech.hops.core.entity.customs.Notice
import com.afsoltech.hops.core.entity.customs.NoticeBeneficiary
import com.afsoltech.hops.core.entity.customs.temp.SelectedNotice
import com.afsoltech.hops.core.entity.customs.temp.SelectedNoticeBeneficiary
import com.afsoltech.hops.core.model.notice.NoticeBeneficiaryDto
import com.afsoltech.hops.core.model.notice.NoticeResponseDto
import com.afsoltech.hops.core.model.integration.UnpaidNoticeResponseDto

class NoticeEntityToModel {


    object NoticeEntityToModel {
        fun from(entity: Notice): NoticeResponseDto {
            val model = NoticeResponseDto(
                    noticeNumber = entity.noticeNumber!!,
                    notificationDate = entity.notificationDate.toString(),
                    noticeType = entity.noticeType!!,
                    referenceNumber = entity.referenceNumber!!,
                    taxPayerNumber = entity.taxpayerNumber!!,
                    taxPayerName = entity.taxpayerName,
                    taxPayerRepresentativeNumber = entity.cdaNumber,
                    taxPayerRepresentativeCode = entity.cdaCode,
                    taxPayerRepresentativeName = entity.cdaName,
                    issuerOffice = entity.issuerOffice,
                    paymentDate = entity.paymentDate.toString(),
                    amountReceived = entity.paymentAmount!!
            )
            model.beneficiaryList = NoticeBeneficiaryEntitiesToModel.from(entity.beneficiaryList)
            return model
        }

    }

    object NoticeEntitiesToModels {
        fun from(entities: List<Notice>): List<NoticeResponseDto> {
            val models = mutableListOf<NoticeResponseDto>()

            entities.forEach { it ->
                models.add(NoticeEntityToModel.from(it))
            }

            return models
        }
    }


    object NoticeBeneficiaryEntityToModel {
        fun from(entity: NoticeBeneficiary): NoticeBeneficiaryDto {
            val model = NoticeBeneficiaryDto(
                    beneficiaryName = entity.beneficiaryName,
                    beneficiaryCode = entity.beneficiaryCode,
                    bankCode = entity.bankCode,
                    accountNumber = entity.accountNumber,
                    amount = entity.amount)
            return model
        }
    }

    object NoticeBeneficiaryEntitiesToModel {
        fun from(entities: List<NoticeBeneficiary>): List<NoticeBeneficiaryDto> {
            val models = mutableListOf<NoticeBeneficiaryDto>()

            entities.forEach { it ->
                models.add(NoticeBeneficiaryEntityToModel.from(it))
            }

            return models
        }
    }

    object SelectedNoticeEntityToModel {
        fun from(entity: SelectedNotice): UnpaidNoticeResponseDto {
            val model = UnpaidNoticeResponseDto(
                    noticeId = entity.remoteNoticeId,
                    noticeNumber = entity.noticeNumber,
                    notificationDate = entity.notificationDate.toString(),
                    noticeType = entity.noticeType,
                    referenceNumber = entity.referenceNumber,
                    declarationType = entity.declarationType,
                    taxPayerNumber = entity.taxpayerNumber,
                    taxPayerName = entity.taxpayerName,
                    taxPayerRepresentativeNumber = entity.cdaNumber,
                    taxPayerRepresentativeCode = entity.cdaCode,
                    taxPayerRepresentativeName = entity.cdaName,
                    issuerOffice = entity.issuerOffice,
                    dueDate = entity.dueDate.toString(),
                    noticeAmount = entity.amount
            )
            model.beneficiaryList = SelectedNoticeBeneficiaryEntitiesToModels.from(entity.beneficiaryList) //
            return model
        }

    }


    object SelectedNoticeEntitiesToModels {
        fun from(entities: List<SelectedNotice>): List<UnpaidNoticeResponseDto> {
            val models = mutableListOf<UnpaidNoticeResponseDto>()

            entities.forEach { it ->
                models.add(SelectedNoticeEntityToModel.from(it))
            }

            return models
        }
    }

    object SelectedNoticeBeneficiaryEntityToModel {
        fun from(entity: SelectedNoticeBeneficiary): NoticeBeneficiaryDto {
            val model = NoticeBeneficiaryDto(
                    beneficiaryName = entity.name,
                    beneficiaryCode = entity.code,
                    bankCode = entity.bankCode,
                    accountNumber = entity.accountNumber,
                    amount = entity.amount)
            return model
        }
    }

    object SelectedNoticeBeneficiaryEntitiesToModels {
        fun from(entities: List<SelectedNoticeBeneficiary>): List<NoticeBeneficiaryDto> {
            val models = mutableListOf<NoticeBeneficiaryDto>()

            entities.forEach { it ->
                models.add(SelectedNoticeBeneficiaryEntityToModel.from(it))
            }

            return models
        }
    }
}

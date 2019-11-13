package com.nanobnk.epayment.portal.service.mapper

import com.nanobnk.epayment.model.inbound.*
import java.text.DecimalFormat


class PortalNoticeModelToModel {

    object NoticeModelToModel {
        fun from(model: NoticeResponseDto, decf: DecimalFormat): NoticePortalResponseDto {
            val result = NoticePortalResponseDto(
                    noticeNumber = model.noticeNumber,
                    notificationDate = model.notificationDate,
                    noticeType = model.noticeType,
                    declarationType = model.declarationType,
                    referenceNumber = model.referenceNumber,
                    taxPayerNumber = model.taxPayerNumber,
                    taxPayerName = model.taxPayerName,
                    taxPayerRepresentativeNumber = model.taxPayerRepresentativeNumber,
                    taxPayerRepresentativeCode = model.taxPayerRepresentativeCode,
                    taxPayerRepresentativeName = model.taxPayerRepresentativeName,
                    issuerOffice = model.issuerOffice,
                    paymentNumber = model.paymentNumber,
                    paymentDate = model.paymentDate,
                    amountReceived = decf.format(model.amountReceived?.toDouble()),
                    paymentCategory = model.paymentCategory
            )
            return result
        }

    }

    object NoticeModelsToModels {
        fun from(entities: List<NoticeResponseDto>): List<NoticePortalResponseDto> {
            val models = mutableListOf<NoticePortalResponseDto>()
            val decf = DecimalFormat("#,##0")
            entities.forEach { it ->
                models.add(NoticeModelToModel.from(it, decf))
            }
            return models
        }
    }


    object UnpaidNoticeModelToModel {
        fun from(model: UnpaidNoticeResponseDto, decf: DecimalFormat): UnpaidNoticePortalResponseDto {
            val result = UnpaidNoticePortalResponseDto(
                    noticeNumber = model.noticeNumber,
                    notificationDate = model.notificationDate,
                    noticeType = model.noticeType,
                    declarationType = model.declarationType,
                    referenceNumber = model.referenceNumber,
                    taxPayerNumber = model.taxPayerNumber,
                    taxPayerName = model.taxPayerName,
                    taxPayerRepresentativeNumber = model.taxPayerRepresentativeNumber,
                    taxPayerRepresentativeCode = model.taxPayerRepresentativeCode,
                    taxPayerRepresentativeName = model.taxPayerRepresentativeName,
                    issuerOffice = model.issuerOffice,
                    dueDate = model.dueDate,
                    noticeAmount = decf.format(model.noticeAmount?.toDouble())
            )
            return result
        }

    }

    object UnpaidNoticeModelsToModels {
        fun from(entities: List<UnpaidNoticeResponseDto>): List<UnpaidNoticePortalResponseDto> {
            val models = mutableListOf<UnpaidNoticePortalResponseDto>()

            val decf = DecimalFormat("#,##0")
            entities.forEach { it ->
                models.add(UnpaidNoticeModelToModel.from(it, decf))
            }
            return models
        }
    }


}
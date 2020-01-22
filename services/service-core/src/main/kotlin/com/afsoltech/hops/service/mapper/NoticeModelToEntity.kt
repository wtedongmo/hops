package com.afsoltech.hops.service.mapper

import com.afsoltech.core.entity.cap.Payment
import com.afsoltech.core.entity.cap.temp.TempPayment
import com.afsoltech.core.service.utils.LoadSettingDataToMap
import com.afsoltech.core.service.utils.StringDateFormatterUtils
import com.afsoltech.hops.core.entity.customs.PaymentOfNotice
import com.afsoltech.hops.core.entity.customs.temp.SelectedNotice
import com.afsoltech.hops.core.entity.customs.temp.SelectedNoticeBeneficiary
import com.afsoltech.hops.core.model.notice.NoticeBeneficiaryDto
import com.afsoltech.hops.core.model.integration.NoticeOfPaymentDto
import com.afsoltech.hops.core.model.integration.PaymentProcessRequestDto
import com.afsoltech.hops.core.model.integration.UnpaidNoticeResponseDto

/*
  * This class help to copy data from response model to entity data class before save
 */
class NoticeModelToEntity {

    object SelectedNoticeModelToEntity {
        fun from(model: UnpaidNoticeResponseDto): SelectedNotice {
            val entity = SelectedNotice(
                    remoteNoticeId = model.noticeId,
                    noticeNumber = model.noticeNumber,
                    notificationDate = StringDateFormatterUtils.StringToDate.parse(model.notificationDate),
                    noticeType = model.noticeType,
                    referenceNumber = model.referenceNumber,
                    taxpayerNumber = model.taxPayerNumber,
                    cdaNumber = model.taxPayerRepresentativeNumber,
                    taxpayerName = model.taxPayerName,
                    cdaCode = model.taxPayerRepresentativeCode,
                    cdaName = model.taxPayerRepresentativeName,
                    issuerOffice = model.issuerOffice,
                    dueDate = StringDateFormatterUtils.StringToDate.parse(model.dueDate),
                    amount = model.noticeAmount
            )
            return entity
        }

    }

    object SelectedNoticeModelsToEntities {
        fun from(models: List<UnpaidNoticeResponseDto>): MutableList<SelectedNotice> {
            val entities = mutableListOf<SelectedNotice>()

            models.forEach { it ->
                entities.add(SelectedNoticeModelToEntity.from(it))
            }

            return entities
        }
    }


    object SelectedNoticeBeneficiaryModelToEntity {
        fun from(model: NoticeBeneficiaryDto):  SelectedNoticeBeneficiary {
            val entity = SelectedNoticeBeneficiary(
                    name = model.beneficiaryName,
                    code = model.beneficiaryCode,
                    bankCode = model.bankCode,
                    accountNumber = model.accountNumber,
                    amount = model.amount)
            return entity
        }
    }

    object SelectedNoticeBeneficiaryModelToEntities {
        fun from(models: List<NoticeBeneficiaryDto>): MutableList<SelectedNoticeBeneficiary> {
            val entities = mutableListOf<SelectedNoticeBeneficiary>()

            models.forEach { it ->
                entities.add(SelectedNoticeBeneficiaryModelToEntity.from(it))
            }

            return entities
        }
    }


    object PaymentProcessToEntity {
        fun from(model: PaymentProcessRequestDto, tempPayment: TempPayment):  Payment {
            var payDateTime = StringDateFormatterUtils.ParsePaymentDate.parse(model.paymentDate, LoadSettingDataToMap.timeZoneId)
            val entity = Payment(
                    paymentNumber = tempPayment.internalPaymentNumber,
                    bankCode = tempPayment.bankCode,
                    bankName = tempPayment.bankName,
                    taxpayerNumber = model.taxpayerNumber,
                    payerAccountNumber = model.accountNumber,
                    payerAccountName = model.accountName,
                    paymentMode = tempPayment.paymentMode,
                    billAmount = tempPayment.amount!!,
                    feeAmount = tempPayment.feeAmount,
                    totalAmount = model.totalAmount,
                    paymentDate = payDateTime,
                    providerPaymentNumber = tempPayment.remotePaymentId.toString(),
                    userLogin = tempPayment.userLogin,
                    providerCode = tempPayment.providerCode,
                    bankAccNewBal = tempPayment.bankAccNewBal
            )
            return entity
        }
    }


    object PaymentOfNoticeModelToEntity {
        fun from(model: NoticeOfPaymentDto):  PaymentOfNotice{
            val entity = PaymentOfNotice(
                    noticeNumber = model.noticeNumber,
                    amount = model.noticeAmount
            )
            return entity
        }
    }

    object PaymentOfNoticeModelToEntities {
        fun from(models: List<NoticeOfPaymentDto>): MutableList<PaymentOfNotice> {
            val entities = mutableListOf<PaymentOfNotice>()

            models.forEach { it ->
                entities.add(PaymentOfNoticeModelToEntity.from(it))
            }

            return entities
        }
    }
}
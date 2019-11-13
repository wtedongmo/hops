//package com.afsoltech.core.service
//
//import com.afsoltech.core.entity.*
//import com.afsoltech.core.model.BaseEntityModel
//import com.afsoltech.core.repository.*
//import com.nanobnk.util.rest.util.ensureNotNull
//import org.springframework.data.domain.Page
//import org.springframework.data.domain.PageImpl
//import org.springframework.data.domain.PageRequest
//import org.springframework.data.domain.Sort
//import org.springframework.stereotype.Service
//
//@Service
//class BaseEntitiesService(val noticeTypeRepository: NoticeTypeRepository, val declarationTypeRepository: DeclarationTypeRepository,
//                          val paymentCategoryRepository: PaymentCategoryRepository, val paymentMethodRepository: PaymentMethodRepository,
//                          val noticeRepository: NoticeRepository, val beneficiaryRepository: BeneficiaryRepository,
//                          val noticePaymentBeneficiaryRepository: NoticePaymentBeneficiaryRepository,
//                          val issuerOfficeRepository: IssuerOfficeRepository) {
//
//
//    fun getModelEntityToEdit(entityId: Int?, editId: Long): BaseEntityModel? {
//
//        val modelEntity = if(entityId==null || entityId==1) {
//            val ent =  declarationTypeRepository.findOne(editId)
//            BaseEntityModel(ent.id, ent.code, ent.name, ent.status, entityId)
//        }else if (entityId==2) {
//            val ent =  noticeTypeRepository.findOne(editId)
//            BaseEntityModel(ent.id, ent.code, ent.name, ent.status, entityId)
//        }else if(entityId==3) {
//            val ent = paymentCategoryRepository.findOne(editId)
//            BaseEntityModel(ent.id, ent.code, ent.name, ent.status, entityId)
//        }else if(entityId==4) {
//            val ent = paymentMethodRepository.findOne(editId)
//            BaseEntityModel(ent.id, ent.code, ent.name, ent.status, entityId)
//        }else if(entityId==5) {
//            val ent = beneficiaryRepository.findOne(editId)
//            BaseEntityModel(ent.id, ent.code, ent.name, ent.status, entityId)
//        }else if(entityId==6) {
//            val ent = issuerOfficeRepository.findOne(editId)
//            BaseEntityModel(ent.id, ent.code, ent.name, ent.status, entityId)
//        }else {
////            val ent = noticeTypeRepository.findOne(editId)
//            null
//        }
//
//        return modelEntity
//    }
//
//    fun getModelEntitiesList(entityId: Int?): List<BaseEntityModel>{
//
//        val baseEntities = if(entityId==null || entityId==1) {
//            val ents =  declarationTypeRepository.findAll()
//            ents.map { it ->
//                BaseEntityModel(it.id, it.code, it.name, it.status, entityId?:1)
//            }.toList()
//        }else if (entityId==2) {
//            val ents =  noticeTypeRepository.findAll()
//            ents.map { it ->
//                BaseEntityModel(it.id, it.code, it.name, it.status, entityId)
//            }
//        }else if(entityId==3) {
//            val ents = paymentCategoryRepository.findAll()
//            ents.map { it ->
//                BaseEntityModel(it.id, it.code, it.name, it.status, entityId)
//            }
//        }else if(entityId==4) {
//            val ents = paymentMethodRepository.findAll()
//            ents.map { it ->
//                BaseEntityModel(it.id, it.code, it.name, it.status, entityId)
//            }
//        }else if(entityId==5) {
//            val ents = beneficiaryRepository.findAll()
//            ents.map { it ->
//                BaseEntityModel(it.id, it.code, it.name, it.status, entityId)
//            }
//        }else if(entityId==6) {
//            val ents = issuerOfficeRepository.findAll()
//            ents.map { it ->
//                BaseEntityModel(it.id, it.code, it.name, it.status, entityId)
//            }
//        }else {
//            emptyList<BaseEntityModel>()
//        }
//
//        return baseEntities
//    }
//
//    fun retrieveAllElements(entityId: Int?, pageNumber: Int, pageSize: Int): Page<BaseEntityModel> {
//
//        val pageRequest = PageRequest(pageNumber, pageSize, Sort.Direction.ASC, "id")
//        var totalElts = 0L
//        val baseEntities = ArrayList<BaseEntityModel>()
//        if(entityId==null || entityId==1) {
//            val ents =  declarationTypeRepository.findAll(pageRequest)
//            ents.forEach { it ->
//                baseEntities.add(BaseEntityModel(it.id, it.code, it.name, it.status, entityId?:1))
//            }
//            totalElts = ents.totalElements
//        }else if (entityId==2) {
//            val ents =  noticeTypeRepository.findAll(pageRequest)
//            ents.forEach { it ->
//                baseEntities.add(BaseEntityModel(it.id, it.code, it.name, it.status, entityId?:1))
//            }
//            totalElts = ents.totalElements
//        }else if(entityId==3) {
//            val ents = paymentCategoryRepository.findAll(pageRequest)
//            ents.forEach { it ->
//                baseEntities.add(BaseEntityModel(it.id, it.code, it.name, it.status, entityId?:1))
//            }
//        }else if(entityId==4) {
//            val ents = paymentMethodRepository.findAll(pageRequest)
//            ents.forEach { it ->
//                baseEntities.add(BaseEntityModel(it.id, it.code, it.name, it.status, entityId?:1))
//            }
//            totalElts = ents.totalElements
//        }else if(entityId==5) {
//            val ents = beneficiaryRepository.findAll(pageRequest)
//            ents.forEach { it ->
//                baseEntities.add(BaseEntityModel(it.id, it.code, it.name, it.status, entityId?:1))
//            }
//            totalElts = ents.totalElements
//        }else if(entityId==6) {
//            val ents = issuerOfficeRepository.findAll(pageRequest)
//            ents.forEach { it ->
//                baseEntities.add(BaseEntityModel(it.id, it.code, it.name, it.status, entityId?:1))
//            }
//            totalElts = ents.totalElements
//        }
//
//        return PageImpl<BaseEntityModel>(baseEntities, pageRequest, totalElts)
////        return baseEntities
//    }
//
//    fun saveBaseEntity( baseEntity: BaseEntityModel): Long?{
//
//        //var idSaved: Long?=null
//        if(baseEntity.code.isNullOrBlank())
//            baseEntity.code=null
//        if(baseEntity.name.isNullOrBlank())
//            baseEntity.name=null
//        ensureNotNull(baseEntity.code)
//        ensureNotNull(baseEntity.name)
//
//        if(baseEntity.entityId==null || baseEntity.entityId==1) {
//            val ent = DeclarationTypeEntity( baseEntity.id, baseEntity.code, baseEntity.name, baseEntity.status)
//            declarationTypeRepository.save(ent)
//        }else if (baseEntity.entityId==2)  {
//            val ent = NoticeTypeEntity( baseEntity.id, baseEntity.code, baseEntity.name, baseEntity.status)
//            noticeTypeRepository.save(ent)
//        }else if(baseEntity.entityId==3) {
//            val ent = PaymentCategoryEntity( baseEntity.id, baseEntity.code, baseEntity.name, baseEntity.status)
//            paymentCategoryRepository.save(ent)
//        }else if(baseEntity.entityId==4) {
//            val ent = PaymentMethodEntity( baseEntity.id, baseEntity.code, baseEntity.name, baseEntity.status)
//            paymentMethodRepository.save(ent)
//        }else if(baseEntity.entityId==5) {
//            val ent = UssdRequestEntity( baseEntity.id, baseEntity.code, baseEntity.name, baseEntity.status)
//            beneficiaryRepository.save(ent)
//        }else if(baseEntity.entityId==6) {
//            val ent = IssuerOfficeEntity( baseEntity.id, baseEntity.code, baseEntity.name, baseEntity.status)
//            issuerOfficeRepository.save(ent)
//        }
//
//        return null
//    }
//
//    fun delete(entityId: Int?, deleteId: Long) : Long {
//
//        val result = if(entityId==null || entityId==1) {
//            val ent =  declarationTypeRepository.findOne(deleteId)
//            if(ent.id!=null && ent.id==deleteId){
//                val notices = noticeRepository.findByDeclarationType(ent.code!!)
//                if(notices.size==0){
//                    declarationTypeRepository.delete(ent)
//                    deleteId
//                }else
//                    -1L
//            }else
//                -1L
//        }else if (entityId==2) {
//            val ent =  noticeTypeRepository.findOne(deleteId)
//            if(ent.id!=null && ent.id==deleteId){
//                val notices = noticeRepository.findByNoticeType(ent.code!!)
//                if(notices.size==0){
//                    noticeTypeRepository.delete(ent)
//                    deleteId
//                }else
//                    -1L
//            }else
//                -1L
//        }else if(entityId==3) {
//            val ent = paymentCategoryRepository.findOne(deleteId)
//            if(ent.id!=null && ent.id==deleteId){
//                val notices = noticeRepository.findByPaymentCategory(ent.code!!)
//                if(notices.size==0){
//                    paymentCategoryRepository.delete(ent)
//                    deleteId
//                }else
//                    -1L
//            }else
//                -1L
//        }else if(entityId==4) {
//            val ent = paymentMethodRepository.findOne(deleteId)
//            if(ent.id!=null && ent.id==deleteId){
//                val notices = noticeRepository.findByPaymentMethod(ent.code!!)
//                if(notices.size==0){
//                    paymentMethodRepository.delete(ent)
//                    deleteId
//                }else
//                    -1L
//            }else
//                -1L
//        }else if(entityId==5) {
//            val ent = beneficiaryRepository.findOne(deleteId)
//            if(ent.id!=null && ent.id==deleteId){
//                val notices = noticePaymentBeneficiaryRepository.findByBeneficiaryCode(ent.code!!)
//                if(notices.size==0){
//                    beneficiaryRepository.delete(ent)
//                    deleteId
//                }else
//                    -1L
//            }else
//                -1L
//        }else if(entityId==6) {
//            val ent = issuerOfficeRepository.findOne(deleteId)
//            if(ent.id!=null && ent.id==deleteId){
//                val notices = noticeRepository.findByIssuerOffice(ent.code!!)
//                if(notices.size==0){
//                    issuerOfficeRepository.delete(ent)
//                    deleteId
//                }else
//                    -1L
//            }else
//                -1L
//        }else {
//            -1L
//        }
//        return result
//    }
//}
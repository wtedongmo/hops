package com.afsoltech.hops.service.backoffice

import com.afsoltech.core.entity.cap.PaymentMode
import com.afsoltech.core.model.BaseEntityModel
import com.afsoltech.core.repository.cap.PaymentModeRepository
import com.afsoltech.core.service.search.SearchService
import com.afsoltech.core.util.ensureNotNull
import com.afsoltech.hops.core.entity.customs.Beneficiary
import com.afsoltech.hops.core.entity.customs.DeclarationType
import com.afsoltech.hops.core.entity.customs.NoticeType
import com.afsoltech.hops.core.repository.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class BaseEntitiesService(val noticeTypeRepository: NoticeTypeRepository, val declarationTypeRepository: DeclarationTypeRepository,
                          val paymentModeRepository: PaymentModeRepository,
                          val noticeRepository: NoticeRepository, val beneficiaryRepository: BeneficiaryRepository,
                          val noticeBeneficiaryRepository: NoticeBeneficiaryRepository) {

    @Autowired
    lateinit var searchService: SearchService

    fun getModelEntityToEdit(entityId: Int?, editId: Long): BaseEntityModel? {

        val modelEntity = if(entityId==null || entityId==1) {
            val ent =  declarationTypeRepository.getOne(editId)
            BaseEntityModel(ent.id, ent.code, ent.name, ent.status, entityId)
        }else if (entityId==2) {
            val ent =  noticeTypeRepository.getOne(editId)
            BaseEntityModel(ent.id, ent.code, ent.name, ent.status, entityId)
        }else if(entityId==3) {
            val ent = beneficiaryRepository.getOne(editId)
            BaseEntityModel(ent.id, ent.code, ent.name, ent.status, entityId)
        }else if(entityId==4) {
            val ent = paymentModeRepository.getOne(editId)
            BaseEntityModel(ent.id, ent.code, ent.name, ent.status, entityId)
        }else {
//            val ent = noticeTypeRepository.findOne(editId)
            null
        }

        return modelEntity
    }

    fun getModelEntitiesList(entityId: Int?): List<BaseEntityModel>{

        val baseEntities = if(entityId==null || entityId==1) {
            val ents =  declarationTypeRepository.findAll()
            ents.map { it ->
                BaseEntityModel(it.id, it.code, it.name, it.status, entityId)
            }.toList()
        }else if (entityId==2) {
            val ents =  noticeTypeRepository.findAll()
            ents.map { it ->
                BaseEntityModel(it.id, it.code, it.name, it.status, entityId)
            }
        }else if(entityId==3) {
            val ents = beneficiaryRepository.findAll()
            ents.map { it ->
                BaseEntityModel(it.id, it.code, it.name, it.status, entityId)
            }
        }else if(entityId==4) {
            val ents = paymentModeRepository.findAll()
            ents.map { it ->
                BaseEntityModel(it.id, it.code, it.name, it.status, entityId)
            }
        }else {
            emptyList<BaseEntityModel>()
        }

        return baseEntities
    }

    fun retrieveAllElements(entityId: Int?, pageNumber: Int, pageSize: Int): Page<BaseEntityModel> {

        val pageRequest = PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC, "id")
        var totalElts = 0L
        val baseEntities = ArrayList<BaseEntityModel>()
        if(entityId==null || entityId==1) {
            val ents =  declarationTypeRepository.findAll(pageRequest)
            ents.forEach { it ->
                baseEntities.add(BaseEntityModel(it.id, it.code, it.name, it.status, entityId))
            }
            totalElts = ents.totalElements
        }else if (entityId==2) {
            val ents =  noticeTypeRepository.findAll(pageRequest)
            ents.forEach { it ->
                baseEntities.add(BaseEntityModel(it.id, it.code, it.name, it.status, entityId))
            }
            totalElts = ents.totalElements
        }else if(entityId==3) {
            val ents = beneficiaryRepository.findAll(pageRequest)
            ents.forEach { it ->
                baseEntities.add(BaseEntityModel(it.id, it.code, it.name, it.status, entityId))
            }
            totalElts = ents.totalElements
        }else if(entityId==4) {
            val ents = paymentModeRepository.findAll(pageRequest)
            ents.forEach { it ->
                baseEntities.add(BaseEntityModel(it.id, it.code, it.name, it.status, entityId))
            }
            totalElts = ents.totalElements
        }

        return PageImpl<BaseEntityModel>(baseEntities, pageRequest, totalElts)
//        return baseEntities
    }

    fun saveBaseEntity( baseEntity: BaseEntityModel): Long?{

        //var idSaved: Long?=null
        if(baseEntity.code.isNullOrBlank())
            baseEntity.code=null
        if(baseEntity.name.isNullOrBlank())
            baseEntity.name=null
        ensureNotNull(baseEntity.code)
        ensureNotNull(baseEntity.name)

        if(baseEntity.entityId==null || baseEntity.entityId==1) {
            val ent = DeclarationType (id=baseEntity.id, code= baseEntity.code, name= baseEntity.name)
            ent.status= baseEntity.status
            declarationTypeRepository.save(ent)
        }else if (baseEntity.entityId==2)  {
            val ent = NoticeType( baseEntity.id, baseEntity.code, baseEntity.name)
            ent.status= baseEntity.status
            noticeTypeRepository.save(ent)
        }else if(baseEntity.entityId==3) {
            val ent = Beneficiary( baseEntity.id, baseEntity.code, baseEntity.name)
            ent.status= baseEntity.status
            beneficiaryRepository.save(ent)
        }else if(baseEntity.entityId==6) {
            val ent = PaymentMode( baseEntity.id)
            ent.code= baseEntity.code
            ent.name= baseEntity.name
            ent.status= baseEntity.status
            paymentModeRepository.save(ent)
        }

        return null
    }

    fun delete(entityId: Int?, deleteId: Long) : Long {

        val result = if(entityId==null || entityId==1) {
            val ent =  declarationTypeRepository.getOne(deleteId)
            if(ent.id!=null && ent.id==deleteId){
                val notices = noticeRepository.findByDeclarationType(ent.code!!)
                if(notices.size==0){
                    declarationTypeRepository.delete(ent)
                    deleteId
                }else
                    -1L
            }else
                -1L
        }else if (entityId==2) {
            val ent =  noticeTypeRepository.getOne(deleteId)
            if(ent.id!=null && ent.id==deleteId){
                val notices = noticeRepository.findByNoticeType(ent.code!!)
                if(notices.size==0){
                    noticeTypeRepository.delete(ent)
                    deleteId
                }else
                    -1L
            }else
                -1L
        }else if(entityId==3) {
            val ent = beneficiaryRepository.getOne(deleteId)
            if(ent.id!=null && ent.id==deleteId){
                val notices = noticeBeneficiaryRepository.findByBeneficiaryCode(ent.code!!)
                if(notices.size==0){
                    beneficiaryRepository.delete(ent)
                    deleteId
                }else
                    -1L
            }else
                -1L
        }else if(entityId==4) {
            val ent = paymentModeRepository.getOne(deleteId)
            if(ent.id!=null && ent.id==deleteId){
                val notices = noticeRepository.findByPaymentMode(ent.code!!)
                if(notices.size==0){
                    paymentModeRepository.delete(ent)
                    deleteId
                }else
                    -1L
            }else
                -1L
        }else {
            -1L
        }
        return result
    }

    fun search(pageRequest: PageRequest, column :String, value :String, entityId: Int?): Page<BaseEntityModel>? {

        var totalElts = 0L
        val baseEntities = ArrayList<BaseEntityModel>()
        if(entityId==null || entityId==1) {
            val ents =  searchService.search(DeclarationType(), column, value) as List<DeclarationType>
            ents.forEach { it ->
                baseEntities.add(BaseEntityModel(it.id, it.code, it.name, it.status, entityId))
            }
            totalElts = ents.size.toLong()
        }else if (entityId==2) {
            val ents =  searchService.search(NoticeType(), column, value) as List<NoticeType>
            ents.forEach { it ->
                baseEntities.add(BaseEntityModel(it.id, it.code, it.name, it.status, entityId))
            }
            totalElts = ents.size.toLong()
        }else if(entityId==3) {
            val ents = searchService.search(Beneficiary(), column, value) as List<Beneficiary>
            ents.forEach { it ->
                baseEntities.add(BaseEntityModel(it.id, it.code, it.name, it.status, entityId))
            }
            totalElts = ents.size.toLong()
        }else if(entityId==4) {
            val ents = searchService.search(PaymentMode(), column, value) as List<PaymentMode>
            ents.forEach { it ->
                baseEntities.add(BaseEntityModel(it.id, it.code, it.name, it.status, entityId))
            }
            totalElts = ents.size.toLong()
        }

        return PageImpl<BaseEntityModel>(baseEntities, pageRequest, totalElts)

    }
}
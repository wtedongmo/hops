package com.afsoltech.core.repository.criteria.builder

import org.springframework.transaction.annotation.Transactional

@Transactional(rollbackFor = [Exception::class])
class CriteriaBuilderEndPoint {

    companion object {
        const val wildcard = "*"
        const val like = "%"
    }
}
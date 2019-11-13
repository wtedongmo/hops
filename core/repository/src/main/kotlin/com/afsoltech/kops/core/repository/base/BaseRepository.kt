package com.afsoltech.core.repository.base

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.querydsl.QueryDslPredicateExecutor
import org.springframework.data.repository.NoRepositoryBean
import java.io.Serializable

@NoRepositoryBean
interface BaseRepository<T, ID : Serializable> : JpaRepository<T, ID>, JpaSpecificationExecutor<T>{

    fun findById(id: ID): T?

    fun findById(id: ID, clazz: T): T?
}

@NoRepositoryBean
interface ExtendedBaseRepository<T, ID : Serializable> : BaseRepository<T, ID>, QueryDslPredicateExecutor<T>
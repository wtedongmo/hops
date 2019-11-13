package com.nanobnk.epayment.entity.interceptor

import org.hibernate.EmptyInterceptor
import org.hibernate.type.Type
import org.springframework.security.core.context.SecurityContextHolder
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID



class EPaymentEntityInterceptor : EmptyInterceptor() {

    override fun onSave(entity: Any, id: Serializable, state: Array<Any>, propertyNames: Array<String>, types: Array<Type>): Boolean {
        var result = false
        for (i in propertyNames.indices) {
            if ("createdBy".equals(propertyNames[i], ignoreCase = true)) {
                val auth = SecurityContextHolder.getContext().authentication
                if (auth!=null)
                    state[i] = auth.name
//                else
//                    state[i] = "UNKWOMN"
                result = true
            }
            if ("modifiedBy".equals(propertyNames[i], ignoreCase = true)) {
                val auth = SecurityContextHolder.getContext().authentication
                if (auth!=null)
                    state[i] = auth.name
//                else
//                    state[i] = "UNKWOMN"
                result = true
            }
            if ("dateCreated".equals(propertyNames[i], ignoreCase = true)) {
                state[i] = LocalDateTime.now()
                result = true
            }
            if ("dateModified".equals(propertyNames[i], ignoreCase = true)) {
                state[i] = LocalDateTime.now()
                result = true
            }

        }
        return result
    }

    override fun onFlushDirty(entity: Any, id: Serializable, currentState: Array<Any>, previousState: Array<Any>, propertyNames: Array<String>,
                     types: Array<Type>): Boolean {
        var result = false
        for (i in propertyNames.indices) {
            if ("modifiedBy".equals(propertyNames[i], ignoreCase = true)) {
                val auth = SecurityContextHolder.getContext().authentication
                if (auth!=null)
                    currentState[i] = auth.name
//                else
//                    currentState[i] = "UNKWOMN"
                result = true
            }

            if ("dateModified".equals(propertyNames[i], ignoreCase = true)) {
                currentState[i] = LocalDateTime.now()
                result = true
            }

        }
        return result
    }

//    @Override
//    public void afterTransactionCompletion(Transaction tx) {
//        if (tx.wasCommitted()) {
//        logger.info("Transaction Completed");
//        }
//        if (tx.wasRolledBack()) {
//        logger.info("Transaction RolledBack");
//        }
//    }

}
package com.serodriguez.exposuresitenewsletter.base

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionCallback
import org.springframework.transaction.support.TransactionTemplate

@Component
class TransactionProvider(
    @Autowired val transactionManager: PlatformTransactionManager
) {

    fun <T> executeGettingResult(action: TransactionCallback<T>): T {
        val transactionTemplate = TransactionTemplate(transactionManager)
        /*
        * We could configure the transaction here. Configurable things are:
        * - transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
        * - transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        * - transactionTemplate.setTimeout(1000);
        * - transactionTemplate.setReadOnly(true);
        * */
        return transactionTemplate.execute(action)!!
    }
}
package com.mytiki.publish.client.clo

/**
 * Status of the transaction
 */
enum class TransactionStatusEnum {
    APPROVED,
    SETTLED,
    REVERSED,
    DECLINED,
    RETURNED
}
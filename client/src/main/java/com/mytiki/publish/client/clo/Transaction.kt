package com.mytiki.publish.client.clo

import java.util.Date

/**
 * Transaction data to be sent for CLO matching.
 *
 * @property amount
 * @property status
 * @property currency
 * @property description
 * @property description2
 * @property mcc
 * @property transactionDate
 * @property merchantId
 * @property merchantStoreId
 * @property merchantName
 * @property merchantAddrCity
 * @property merchantAddrState
 * @property merchantAddrZipcode
 * @property merchantAddrCountry
 * @property merchantAddrStreet
 * @property cardBIN
 * @property cardLastFour
 * @constructor Create empty Transaction
 */
data class Transaction(
    val amount: Int,
    val status: TransactionStatusEnum,
    val currency: String,
    val description: String,
    val description2: String,
    val mcc: String,
    val transactionDate: Date,
    val merchantId: String,
    val merchantStoreId: String?,
    val merchantName: String,
    val merchantAddrCity: String,
    val merchantAddrState: String,
    val merchantAddrZipcode: String,
    val merchantAddrCountry: String,
    val merchantAddrStreet: String,
    val cardBIN: String,
    val cardLastFour: String,
)

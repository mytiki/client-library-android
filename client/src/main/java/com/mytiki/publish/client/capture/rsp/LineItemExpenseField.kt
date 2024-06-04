package com.mytiki.publish.client.capture.rsp

data class LineItemExpenseField(
    val productCode: SummaryField?,
    val item: SummaryField?,
    val price: SummaryField?,
    val expenseRow: SummaryField?
)

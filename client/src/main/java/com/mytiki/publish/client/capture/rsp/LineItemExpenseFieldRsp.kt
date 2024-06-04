package com.mytiki.publish.client.capture.rsp

data class LineItemExpenseFieldRsp(
    val productCode: SummaryFieldRsp?,
    val item: SummaryFieldRsp?,
    val price: SummaryFieldRsp?,
    val expenseRow: SummaryFieldRsp?
)

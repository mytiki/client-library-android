package com.mytiki.publish.client.capture.rsp

data class ExpenseDocumentRsp(
    val blockRsps: Array<BlockRsp>,
    val expenseIndex: Int,
    val lineItemGroupRsps: Array<LineItemGroupRsp>,
    val summaryFieldsRsp: Array<Map<String, SummaryFieldRsp>>
)

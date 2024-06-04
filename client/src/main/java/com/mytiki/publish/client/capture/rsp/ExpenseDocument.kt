package com.mytiki.publish.client.capture.rsp

data class ExpenseDocument(
    val blocks: Array<Block>,
    val expenseIndex: Int,
    val lineItemGroups: Array<LineItemGroup>,
    val summaryFields: Array<Map<String, SummaryField>>
)

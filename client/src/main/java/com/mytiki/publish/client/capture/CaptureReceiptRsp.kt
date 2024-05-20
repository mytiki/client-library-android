package com.mytiki.publish.client.capture

import okhttp3.ResponseBody

data class CaptureReceiptRsp(
    val documentMetadata: DocumentMetadata,
    val expenseDocuments: Array<ExpenseDocument>
){
    companion object {
        fun from(response: ResponseBody): CaptureReceiptRsp {
            val dummyDocumentMetadata = DocumentMetadata(0)
            val dummyExpenseDocuments = arrayOf<ExpenseDocument>()
            return CaptureReceiptRsp(dummyDocumentMetadata, dummyExpenseDocuments)
        }
    }
}

data class DocumentMetadata(
    val pages: Int
)

data class ExpenseDocument(
    val blocks: Array<Block>,
    val expenseIndex: Int,
    val lineItemGroups: Array<LineItemGroup>,
    val summaryFields: Array<Map<String, SummaryField>>
)

data class Block(
    val confidence: Double,
    val text: String
)

data class LineItemGroup(
    val lineItemGroupIndex: Int,
    val lineItems: Array<LineItem>
)

data class LineItem(
    val lineItemExpenseFields: Array<LineItemExpenseField>
)

data class LineItemExpenseField(
    val productCode: SummaryField?,
    val item: SummaryField?,
    val price: SummaryField?,
    val expenseRow: SummaryField?
)

data class SummaryField(
    val confidenceKey: Int,
    val confidenceValue: Int,
    val value: String
)



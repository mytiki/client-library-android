package com.mytiki.publish.client.capture.rsp

import okhttp3.ResponseBody

data class CaptureReceiptRsp(
    val documentMetadata: DocumentMetadata,
    val expenseDocuments: Array<ExpenseDocument>
) {
  companion object {
    fun from(response: ResponseBody): CaptureReceiptRsp {
      val dummyDocumentMetadata = DocumentMetadata(0)
      val dummyExpenseDocuments = arrayOf<ExpenseDocument>()
      return CaptureReceiptRsp(dummyDocumentMetadata, dummyExpenseDocuments)
    }
  }
}

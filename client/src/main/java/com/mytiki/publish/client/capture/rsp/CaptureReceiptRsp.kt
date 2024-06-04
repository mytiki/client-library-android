package com.mytiki.publish.client.capture.rsp

import okhttp3.ResponseBody

data class CaptureReceiptRsp(
    val documentMetadataRsp: DocumentMetadataRsp,
    val expenseDocumentRsps: Array<ExpenseDocumentRsp>
) {
  companion object {
    fun from(response: ResponseBody): CaptureReceiptRsp {
      val dummyDocumentMetadataRsp = DocumentMetadataRsp(0)
      val dummyExpenseDocumentRsps = arrayOf<ExpenseDocumentRsp>()
      return CaptureReceiptRsp(dummyDocumentMetadataRsp, dummyExpenseDocumentRsps)
    }
  }
}

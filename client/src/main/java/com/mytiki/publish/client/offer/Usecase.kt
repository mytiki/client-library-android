package com.mytiki.publish.client.offer

class Usecase private constructor(val value: String) {
  constructor(usecase: UsecaseCommon) : this(usecase.value)

  companion object {
    fun custom(usecase: String): Usecase {
      return Usecase("custom:$usecase")
    }

    fun from(usecase: String): Usecase {
      val common: UsecaseCommon? = UsecaseCommon.from(usecase)
      return if (common != null) Usecase(common)
      else if (usecase.startsWith("custom:")) Usecase(usecase) else custom(usecase)
    }
  }
}

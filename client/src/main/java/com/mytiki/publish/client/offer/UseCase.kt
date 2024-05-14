package com.mytiki.publish.client.offer

sealed class UseCase private constructor(value: String) {
  data object USECASE1 : UseCase("USECASE1")

  data object USECASE2 : UseCase("USECASE2")

  data object USECASE3 : UseCase("USECASE3")

  data object USECASE4 : UseCase("USECASE4")

  data class CustomUseCase(val value: String) : UseCase(value)
}

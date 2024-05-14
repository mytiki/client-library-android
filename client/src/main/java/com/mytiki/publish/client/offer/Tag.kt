package com.mytiki.publish.client.offer

sealed class Tag private constructor(value: String) {
  data object TAG1 : Tag("TAG1")

  data object TAG2 : Tag("TAG2")

  data object TAG3 : Tag("TAG3")

  data object TAG4 : Tag("TAG4")

  data class CustomTag(val value: String) : Tag(value)
}

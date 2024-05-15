package com.mytiki.publish.client.offer

class Tag private constructor(val value: String) {
  constructor(tag: TagCommon) : this(tag.value)

  companion object {
    fun custom(tag: String): Tag {
      return Tag("custom:$tag")
    }

    fun from(tag: String): Tag {
      val common: TagCommon? = TagCommon.from(tag)
      return if (common != null) Tag(common)
      else if (tag.startsWith("custom:")) Tag(tag) else custom(tag)
    }
  }
}

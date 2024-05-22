/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in the root directory.
 */

package com.mytiki.publish.client.optIn

import android.content.Context
import android.content.Intent

class OptInService private constructor() {

  fun show(
      context: Context,
  ) {
    val intent = Intent(context, OptInActivity::class.java)
    context.startActivity(intent)
  }
}

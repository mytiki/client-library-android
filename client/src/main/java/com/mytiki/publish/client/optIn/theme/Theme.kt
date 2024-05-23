/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in the root directory.
 */

package com.mytiki.publish.client.optIn.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
fun OptInTheme(content: @Composable () -> Unit) {
  val colorScheme =
      lightColorScheme(
          primary = Colors().primary,
      )
  MaterialTheme(
      colorScheme = colorScheme, typography = Typography, shapes = Shapes, content = content)
}

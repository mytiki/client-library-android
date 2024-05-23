/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in the root directory.
 */

package com.mytiki.publish.client.optIn.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.mytiki.publish.client.R

val SfCompactDisplay =
    FontFamily(
        Font(R.font.sf_compact_display_medium, FontWeight.Medium),
    )

val Typography =
    Typography(
        displayLarge =
            TextStyle(
                fontFamily = SfCompactDisplay,
                fontWeight = FontWeight.Medium,
                fontSize = 29.sp,
                color = Colors().primary),
        displayMedium =
            TextStyle(
                fontFamily = SfCompactDisplay,
                fontWeight = FontWeight.Medium,
                fontSize = 21.sp,
                color = Colors().primary),
        displaySmall =
            TextStyle(
                fontFamily = SfCompactDisplay,
                fontWeight = FontWeight.Medium,
                fontSize = 19.36.sp,
                color = Colors().primary),
        titleLarge =
            TextStyle(
                fontFamily = SfCompactDisplay,
                fontWeight = FontWeight.Medium,
                fontSize = 47.86.sp,
                color = Colors().primary),
        titleSmall =
            TextStyle(
                fontFamily = SfCompactDisplay,
                fontWeight = FontWeight.Medium,
                fontSize = 14.9.sp,
                color = Colors().primary),
    )

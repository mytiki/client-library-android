/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in the root directory.
 */

package com.mytiki.publish.client.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.mytiki.publish.client.ui.TikiUI

// Set of Material typography styles to start with



val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = TikiUI.theme.fontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 42.sp,
        color = TikiUI.theme.colorScheme.primary
    ),
    displayMedium = TextStyle(
        fontFamily = TikiUI.theme.fontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
    ),
    displaySmall = TextStyle(
        fontFamily = TikiUI.theme.fontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = TikiUI.theme.colorScheme.primary
    ),

    headlineLarge = TextStyle(
        fontFamily = TikiUI.theme.fontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        color = TikiUI.theme.colorScheme.outline
    ),
    headlineMedium = TextStyle(
        fontFamily = TikiUI.theme.fontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        color = TikiUI.theme.colorScheme.outline
    ),
    headlineSmall = TextStyle(
        fontFamily = TikiUI.theme.fontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        color = TikiUI.theme.colorScheme.outline
    ),
    titleLarge = TextStyle(
        fontFamily = TikiUI.theme.fontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        color = TikiUI.theme.colorScheme.outline
    ),
    titleMedium = TextStyle(
        fontFamily = TikiUI.theme.fontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        color = TikiUI.theme.colorScheme.outlineVariant
    ),
    titleSmall = TextStyle(
        fontFamily = TikiUI.theme.fontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        color = TikiUI.theme.colorScheme.outline
    ),
    bodyLarge = TextStyle(
        fontFamily = TikiUI.theme.fontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        color = TikiUI.theme.colorScheme.outline
    ),

    bodyMedium = TextStyle(
        fontFamily = TikiUI.theme.fontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        color = TikiUI.theme.colorScheme.outline
    ),

    labelLarge = TextStyle(
        fontFamily = TikiUI.theme.fontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        color = TikiUI.theme.colorScheme.outlineVariant
    ),
    labelMedium = TextStyle(
        fontFamily = TikiUI.theme.fontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        color = TikiUI.theme.colorScheme.outlineVariant
    ),
    labelSmall = TextStyle(
        fontFamily = TikiUI.theme.fontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        color = TikiUI.theme.colorScheme.outlineVariant
    ),

    )
/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in the root directory.
 */

package com.mytiki.publish.client.optIn.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.mytiki.publish.client.optIn.OptInService

// Set of Material typography styles to start with

val Typography =
    Typography(
        displayLarge =
            TextStyle(
                fontFamily = OptInService.theme.fontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 42.sp,
                color = OptInService.theme.colorScheme.primary),
        displayMedium =
            TextStyle(
                fontFamily = OptInService.theme.fontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
            ),
        displaySmall =
            TextStyle(
                fontFamily = OptInService.theme.fontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = OptInService.theme.colorScheme.primary),
        headlineLarge =
            TextStyle(
                fontFamily = OptInService.theme.fontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = OptInService.theme.colorScheme.outline),
        headlineMedium =
            TextStyle(
                fontFamily = OptInService.theme.fontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = OptInService.theme.colorScheme.outline),
        headlineSmall =
            TextStyle(
                fontFamily = OptInService.theme.fontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = OptInService.theme.colorScheme.outline),
        titleLarge =
            TextStyle(
                fontFamily = OptInService.theme.fontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = OptInService.theme.colorScheme.outline),
        titleMedium =
            TextStyle(
                fontFamily = OptInService.theme.fontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = OptInService.theme.colorScheme.outlineVariant),
        titleSmall =
            TextStyle(
                fontFamily = OptInService.theme.fontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = OptInService.theme.colorScheme.outline),
        bodyLarge =
            TextStyle(
                fontFamily = OptInService.theme.fontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
                color = OptInService.theme.colorScheme.outline),
        bodyMedium =
            TextStyle(
                fontFamily = OptInService.theme.fontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = OptInService.theme.colorScheme.outline),
        labelLarge =
            TextStyle(
                fontFamily = OptInService.theme.fontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = OptInService.theme.colorScheme.outlineVariant),
        labelMedium =
            TextStyle(
                fontFamily = OptInService.theme.fontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = OptInService.theme.colorScheme.outlineVariant),
        labelSmall =
            TextStyle(
                fontFamily = OptInService.theme.fontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = OptInService.theme.colorScheme.outlineVariant),
    )

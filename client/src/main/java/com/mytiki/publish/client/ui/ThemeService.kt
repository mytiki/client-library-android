package com.mytiki.publish.client.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily

class ThemeService {
    var fontFamily: FontFamily = Theme().fontFamily
        private set

    /**
     * The current color scheme.
     */
    var colorScheme: ColorScheme = lightColorScheme(
        primary = Theme().accentColor,
        error = Color(0xFFC73000),
        background = Theme().primaryBackgroundColor,
        onBackground = Theme().secondaryBackgroundColor,
        outline = Theme().primaryTextColor,
        outlineVariant = Theme().secondaryTextColor,
    )
        private set

    fun theme(
        primaryTextColor: Color,
        secondaryTextColor: Color,
        primaryBackgroundColor: Color,
        secondaryBackgroundColor: Color,
        accentColor: Color,
        fontFamily: FontFamily
    ){
        colorScheme = lightColorScheme(
            primary = accentColor,
            error = Color(0xFFC73000),
            background = primaryBackgroundColor,
            onBackground = secondaryBackgroundColor,
            outline = primaryTextColor,
            outlineVariant = secondaryTextColor,
        )
        this.fontFamily = fontFamily
    }
}
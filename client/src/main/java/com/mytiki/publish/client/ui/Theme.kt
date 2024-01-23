package com.mytiki.publish.client.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.mytiki.publish.client.R

/**
 * Custom Theme definition
 *
 * @property primaryTextColor default 0xFF000000
 * @property secondaryTextColor default 0x99000000
 * @property primaryBackgroundColor default 0xFFFFFFFF
 * @property secondaryBackgroundColor default 0x15000000
 * @property accentColor default 0xFF00B272
 * @property fontFamily default Space Grotesk
 */
data class Theme(
    val primaryTextColor: Color = Color(0xFF000000),
    val secondaryTextColor: Color = Color(0x99000000),
    val primaryBackgroundColor: Color = Color(0xFFFFFFFF),
    val secondaryBackgroundColor: Color = Color(0x15000000),
    val accentColor: Color = Color(0xFF00B272),
    val fontFamily: FontFamily = FontFamily(
        Font(com.mytiki.tiki_sdk_android.R.font.space_grotesk_light, FontWeight.Light), //300
        Font(R.font.space_grotesk_regular, FontWeight.Normal), //400
        Font(R.font.space_grotesk_medium, FontWeight.Medium), //500
        Font(R.font.space_grotesk_semi_bold, FontWeight.SemiBold), //600
        Font(R.font.space_grotesk_bold, FontWeight.Bold), //700
    )
)
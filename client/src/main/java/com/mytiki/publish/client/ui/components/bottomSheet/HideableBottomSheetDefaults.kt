package com.mytiki.publish.client.ui.components.bottomSheet

import androidx.compose.animation.core.SpringSpec
import androidx.compose.ui.unit.dp

object HideableBottomSheetDefaults {
    val AnimationSpec = SpringSpec<Float>()

    val PositionalThreshold = { distance: Float -> distance * 0.2f }

    val VelocityThreshold = { 125.dp.toPixel }
}

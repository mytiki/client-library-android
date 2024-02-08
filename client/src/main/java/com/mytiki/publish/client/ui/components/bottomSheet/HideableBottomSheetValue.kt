package com.mytiki.publish.client.ui.components.bottomSheet


enum class HideableBottomSheetValue {
    Hidden,
    HalfExpanded,
    Expanded;

    val draggableSpaceFraction: Float
        get() = when (this) {
            Hidden -> 0f
            HalfExpanded -> 0.63f
            Expanded -> 1f
        }
}
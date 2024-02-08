package com.mytiki.publish.client.ui.components.bottomSheet

import android.content.res.Resources
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.lang.Float.min


@OptIn(ExperimentalFoundationApi::class)
@Stable
class HideableBottomSheetState(
    initialValue: HideableBottomSheetValue,
    val onDismiss: () -> Unit = {},
    private val animationSpec: AnimationSpec<Float> = HideableBottomSheetDefaults.AnimationSpec,
    private val confirmValueChange: (HideableBottomSheetValue) -> Boolean = { true },
) {

    val draggableState = AnchoredDraggableState(
        initialValue = initialValue,
        animationSpec = animationSpec,
        positionalThreshold = HideableBottomSheetDefaults.PositionalThreshold,
        velocityThreshold = HideableBottomSheetDefaults.VelocityThreshold,
        confirmValueChange = confirmValueChange
    )

    /**
     * The current value of the [HideableBottomSheetState].
     */
    val currentValue: HideableBottomSheetValue
        get() = draggableState.currentValue

    val targetValue: HideableBottomSheetValue
        get() = draggableState.targetValue

    /**
     * Whether the bottom sheet is visible.
     */
    val isVisible: Boolean
        get() = currentValue != HideableBottomSheetValue.Hidden

    /**
     * Whether the bottom sheet is expanded.
     */
    val isExpanded: Boolean
        get() = currentValue == HideableBottomSheetValue.Expanded

    /**
     * Whether the bottom sheet is half expanded.
     */
    val isHalfExpanded: Boolean
        get() = currentValue == HideableBottomSheetValue.HalfExpanded

    /**
     * Whether the bottom sheet is hidden.
     */
    val isHidden: Boolean
        get() = currentValue == HideableBottomSheetValue.Hidden

    private val hasHalfExpandedState: Boolean
        get() = draggableState.anchors.hasAnchorFor(HideableBottomSheetValue.HalfExpanded)

    /**
     * Show the bottom sheet with animation and suspend until it's shown.
     * If the sheet is taller than 50% of the parent's height, the bottom sheet will be half expanded.
     * Otherwise, it will be fully expanded.
     */
    suspend fun show() {
        val targetValue = when {
            hasHalfExpandedState -> HideableBottomSheetValue.HalfExpanded
            else -> HideableBottomSheetValue.Expanded
        }
        animateTo(targetValue)
    }

    /**
     * Expand the bottom sheet with an animation and suspend until the animation finishes or is cancelled.
     */
    suspend fun expand() {
        if (draggableState.anchors.hasAnchorFor(HideableBottomSheetValue.Expanded)) {
            animateTo(HideableBottomSheetValue.Expanded)
        }
    }

    /**
     * Half expand the bottom sheet with an animation and suspend until the animation finishes or is cancelled.
     */
    suspend fun halfExpand() {
        if (draggableState.anchors.hasAnchorFor(HideableBottomSheetValue.HalfExpanded)) {
            animateTo(HideableBottomSheetValue.HalfExpanded)
        }
    }

    /**
     * Hide the bottom sheet with an animation and suspend until the animation finishes or is cancelled.
     */
    suspend fun hide() {
        animateTo(HideableBottomSheetValue.Hidden)
    }

    fun requireOffset() = draggableState.requireOffset()

    fun updateAnchors(layoutHeight: Int, sheetHeight: Int) {
        val maxDragEndPoint = layoutHeight - 32.dp.toPixel
        val newAnchors = DraggableAnchors {
            HideableBottomSheetValue
                .values()
                .forEach { anchor ->
                    val fractionatedMaxDragEndPoint =
                        maxDragEndPoint * anchor.draggableSpaceFraction
                    val dragEndPoint =
                        layoutHeight - min(fractionatedMaxDragEndPoint, sheetHeight.toFloat())
                    anchor at dragEndPoint
                }
        }
        draggableState.updateAnchors(newAnchors)
    }

    fun isHidingInProgress() = isVisible && targetValue == HideableBottomSheetValue.Hidden

    private suspend fun animateTo(
        targetValue: HideableBottomSheetValue,
        velocity: Float = draggableState.lastVelocity
    ) = draggableState.animateTo(targetValue, velocity)

    companion object {
        /**
         * The default [Saver] implementation for [HideableBottomSheetState].
         */
        fun Saver(
            animationSpec: AnimationSpec<Float> = HideableBottomSheetDefaults.AnimationSpec,
            confirmValueChange: (HideableBottomSheetValue) -> Boolean = { true },
            onDismiss: () -> Unit = {},
        ): Saver<HideableBottomSheetState, HideableBottomSheetValue> =
            Saver(
                save = { it.currentValue },
                restore = {
                    HideableBottomSheetState(
                        initialValue = it,
                        animationSpec = animationSpec,
                        confirmValueChange = confirmValueChange,
                        onDismiss = onDismiss,
                    )
                }
            )
    }
}

val Dp.toPixel: Float
    get() = value * Resources.getSystem().displayMetrics.density
package com.mytiki.publish.client.ui.components.bottomSheet

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun rememberHideableBottomSheetState(
    initialValue: HideableBottomSheetValue,
    animationSpec: AnimationSpec<Float> = HideableBottomSheetDefaults.AnimationSpec,
    confirmValueChange: (HideableBottomSheetValue) -> Boolean = { true },
    onDismiss: () -> Unit = {},
): HideableBottomSheetState {
    return key(initialValue) {
        rememberSaveable(
            initialValue, animationSpec, confirmValueChange,
            saver = HideableBottomSheetState.Saver(
                animationSpec = animationSpec,
                confirmValueChange = confirmValueChange,
                onDismiss = onDismiss,
            )
        ) {
            HideableBottomSheetState(
                initialValue = initialValue,
                animationSpec = animationSpec,
                confirmValueChange = confirmValueChange,
                onDismiss = onDismiss,
            )
        }
    }
}

/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in the root directory.
 */

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.mytiki.apps_receipt_rewards.TestTag

@Composable
fun BottomSheet(
    content: @Composable () -> Unit,
) {
    val activity = LocalContext.current as Activity
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .background(Color.Transparent)
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
                .weight(1f)
                .clickable {
                    activity.finish()
                }
        )
        Surface(
            shape = RoundedCornerShape(40.dp, 40.dp, 0.dp, 0.dp),
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxWidth().testTag(TestTag.SURFACE_CONTAINER.name)
        ) {
            content()
        }
    }
}
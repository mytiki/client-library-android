/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in the root directory.
 */

package com.mytiki.publish.client.ui.components

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.mytiki.apps_receipt_rewards.utils.components.CloseButton
import com.mytiki.publish.client.ui.TestTag

@Composable
fun BottomSheetHeader(title: String, subTitle: String) {
    val activity = LocalContext.current as Activity
    Row(
        modifier = Modifier
            .padding(top = 24.dp, start = 24.dp, end = 24.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column {
            Text(text = title.uppercase(), Modifier.testTag(TestTag.TITLE.name), style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = subTitle, Modifier.testTag(TestTag.SUBTITLE.name), style = MaterialTheme.typography.titleMedium)
        }
        CloseButton {
            activity.finish()
        }
    }
}
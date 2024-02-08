/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in the root directory.
 */

package com.mytiki.publish.client.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DisplayCard(
    height: Dp = 0.dp,
    startPadding: Dp = 0.dp,
    topPadding: Dp = 0.dp,
    endPadding: Dp = 0.dp,
    bottomPadding: Dp = 0.dp,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(
                startPadding,
                topPadding,
                (endPadding.value - 4).dp,
                bottomPadding,
            )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(height)
                .padding(end = 4.dp),
            shape = MaterialTheme.shapes.extraSmall,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

        ) {
            content()
        }
    }
}

@Composable
fun DisplayCard(
    height: Dp = 0.dp,
    horizontalPadding: Dp = 0.dp,
    verticalPadding: Dp = 0.dp,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(
                horizontalPadding,
                verticalPadding,
                (horizontalPadding.value - 4).dp,
                verticalPadding,
            )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(height)
                .padding(end = 4.dp),
            shape = MaterialTheme.shapes.extraSmall,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

        ) {
            content()
        }
    }
}


@Composable
fun DisplayCard(
    height: Dp = 0.dp,
    padding: Dp = 0.dp,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(
                padding,
                padding,
                (padding.value - 4).dp,
                padding,
            )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(height)
                .padding(end = 4.dp),
            shape = MaterialTheme.shapes.extraSmall,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

        ) {
            content()
        }
    }
}
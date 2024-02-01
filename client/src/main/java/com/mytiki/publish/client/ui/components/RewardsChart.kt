/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in the root directory.
 */

package com.mytiki.apps_receipt_rewards.utils.components

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun RewardsChart(
    values: List<Double>,
    size: Dp = 100.dp,
    barWidth: Dp = 20.dp,
    animDuration: Int = 1500,
    color: Color = MaterialTheme.colorScheme.primary
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val radius = size / 2

    val extraDegrees =
        ((360 / (2 * Math.PI * (radius.value - barWidth.value / 2))) * (barWidth.value / 2)).toFloat()

    val borderColor = MaterialTheme.colorScheme.outlineVariant

    var percent = 0f


    val colors = listOf(
        color,
        Color(color.red, color.green, color.blue, 0.5f),
        Color(color.red, color.green, color.blue, 0.4f),
    )

    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }
    Box(
        modifier = Modifier.size(100.dp),
        contentAlignment = Alignment.Center

    ) {
        Canvas(
            modifier = Modifier
                .size(((radius.value * 2) - 1).dp)
        ) {
            drawArc(
                color = borderColor,
                0f,
                360f,
                useCenter = false,
                style = Stroke(1.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        Canvas(
            modifier = Modifier
                .size((((radius.value - barWidth.value) * 2) + 1).dp)
        ) {
            drawArc(
                color = borderColor,
                0f,
                360f,
                useCenter = false,
                style = Stroke(1.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        values.forEachIndexed { index, value ->
            percent += value.toFloat()

            val animateSize by animateFloatAsState(
                targetValue = if (animationPlayed) (percent * 360f - extraDegrees) else 0f,
                animationSpec = tween(
                    durationMillis = animDuration,
                    delayMillis = 0,
                    easing = LinearOutSlowInEasing
                ), label = ""
            )

            Canvas(
                modifier = Modifier
                    .size(((radius.value - barWidth.value / 2) * 2).dp)
                    .rotate(-90f + extraDegrees)
            ) {
                drawArc(
                    color = colors[index],
                    0f,
                    animateSize,
                    useCenter = false,
                    style = Stroke(barWidth.toPx(), cap = StrokeCap.Round)
                )
            }
        }

    }
}



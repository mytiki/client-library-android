/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in the root directory.
 */

package com.mytiki.apps_receipt_rewards.license

data class LicenseEarnings(
    val lifetime: Double,
    val monthCurrent: Double,
    val monthTotal: Double
)
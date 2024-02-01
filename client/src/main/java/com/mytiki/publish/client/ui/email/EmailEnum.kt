/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in the root directory.
 */

package com.mytiki.apps_receipt_rewards.email

import com.mytiki.apps_receipt_rewards.R

/**
 * [EmailEnum] enum represents different email providers.
 */
enum class EmailEnum {
    /**
     * Represents the Gmail email provider.
     */
    GMAIL;

    override fun toString(): String {
        return this.name
    }

    fun resId(): Int {
        when (this) {
            GMAIL -> return R.drawable.gmail
        }
    }
}

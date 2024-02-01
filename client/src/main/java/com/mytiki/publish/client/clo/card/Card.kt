package com.mytiki.apps_receipt_rewards.card

data class Card(
    val last4: String, // Last 4 digits of the credit card number
    val bin: String, // Must be a valid BIN of 6 digits; if over 6 digits, send the first 6
    val issuer: String, // Issuer name
    val network: CardNetwork // CardNetwork Enum
)
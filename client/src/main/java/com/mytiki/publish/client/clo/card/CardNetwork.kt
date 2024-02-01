package com.mytiki.apps_receipt_rewards.card

enum class CardNetwork {
    VISA,
    MASTER_CARD,
    AMERICAN;

     override fun toString() = this.name

    companion object{
        fun fromString(cardNetwork: String) = CardNetwork.values().firstOrNull{it.name == cardNetwork}
    }
}

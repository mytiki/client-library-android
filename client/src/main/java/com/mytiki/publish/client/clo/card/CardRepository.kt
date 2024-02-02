package com.mytiki.apps_receipt_rewards.card

class CardRepository {

    private val cards = mutableListOf<Card>()

    fun add(card: Card){
        cards.add(card)
    }
    fun all() = cards.toList()

    fun remove(card: Card) = cards.remove(card)

}
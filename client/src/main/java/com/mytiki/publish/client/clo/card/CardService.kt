package com.mytiki.apps_receipt_rewards.card

class CardService {

    private val repo = CardRepository()

    fun addCard(list: List<Card>){
        list.forEach{
            repo.add(it)
        }
    }
    fun getCards() = repo.all()

    fun removeCard(card: Card)= repo.remove(card)

}
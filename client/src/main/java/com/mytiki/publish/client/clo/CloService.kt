package com.mytiki.publish.client.clo

class CloService {

    /**
     * Adds a card to the user's account.
     * @param last4 Last 4 digits of the card.
     * @param bin Bank Identification Number.
     * @param issuer Card issuer.
     * @param network Card network (VISA, MASTERCARD, AMERICAN EXPRESS, or DISCOVERY).
     */
    fun card(last4: String, bin: String, issuer: String, network: String){}

    /**
     * Retrieves card-linked offers for the user.
     * @return List of card-linked offers.
     */
    fun offers(): List<Offer>{
        return listOf()
    }

    /**
     * Retrieves information about the user's rewards.
     * @return List of user rewards.
     */
    fun rewards(): List<Reward>{
        return listOf()
    }

    /**
     * Sends transaction information to match card-linked offers.
     * @param transaction The transaction information.
     */
    fun transaction(transaction: Transaction){}
}
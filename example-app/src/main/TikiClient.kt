package com.mytiki.publish.client
class TikiClient {

    /**
     * Initializes the TikiClient with the application context and sets its parameters.
     * @param context The application context.
     * @param providerId The TIKI Publishing ID of the data provider.
     * @param userId The user identification from the provider.
     * @param company The legal information of the company.
     */
    fun initialize(context: Context, providerId: String, userId: String, company: CompanyInfo)

    /**
     * Initiates the process of scanning a physical receipt and returns the receipt ID.
     * @return The ID of the scanned receipt.
     */
    fun scan(): String

    /**
     * Initiates the process of scraping receipts from emails.
     * @param provider The email provider (GOOGLE or OUTLOOK).
     */
    fun login(provider: EmailProviderEnum)

    /**
     * Removes a previously added email account.
     * @param email The email account to be removed.
     */
    fun logout(email: String)

    /**
     * Retrieves the list of connected email accounts.
     * @return List of connected email accounts.
     */
    fun accounts(): List<EmailAccount>

    /**
     * Initiates the process of scraping receipts from emails.
     */
    fun scrape()

    /**
     * Adds a card for card-linked offers.
     * @param last4 Last 4 digits of the card.
     * @param bin Bank Identification Number.
     * @param issuer Card issuer.
     * @param network Card network (VISA, MASTERCARD, AMERICAN EXPRESS, or DISCOVERY).
     */
    fun card(last4: String, bin: String, issuer: String, network: String)

    /**
     * Retrieves card-linked offers for the user.
     * @return List of card-linked offers.
     */
    fun offers(): List<Offer>

    /**
     * Submits a transaction for card-linked offer matching.
     * @param transaction The transaction information.
     */
    fun transaction(transaction: Transaction)

    /**
     * Retrieves information about the user's rewards.
     * @return List of user rewards.
     */
    fun rewards(): List<Reward>

    /**
     * Displays the widget for pre-built UIs with a custom theme.
     * @param theme The custom theme for the widget.
     */
    fun widget(theme: Theme?)
}

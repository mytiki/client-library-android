# TIKI Client Library - Android Specification

## Technologies Used

1. **Language:** Kotlin
2. **Networking:** Retrofit for HTTP communication
3. **Authentication:** AppAuth for OAuth2
4. **Encrypted Storage:** Secure storage with EncryptedSharedPreferences
5. **Storage** SharedPreferences will be used for small non-sensitive key-value pairs and the filesystem will be used for lengthy information, like index of email ids.
6. **Camera Integration:** Android Camera API for receipt image capture
7. **UI Components:** Jetpack Compose

## Library Structure

1. **TikiClient:** The primary entry point for the TIKI Client Library.

2. **TikiClient.auth:** Handles authentication, token retrieval, revocation, and refresh.

3. **TikiClient.license:** Manages user licenses, including creation, retrieval, revocation, and verification.

4. **TikiClinet.email:** Manages email-related operations such as adding, retrieving, and removing email accounts.

5. **TikiClient.capture:** Deals with capturing receipt images, downloading and publishing email receipt data, uploading receipt images, and checking publishing status.

6. **TikiClient.clo:** Handles card-linked offers, including adding cards, retrieving offers, getting user rewards, and publishing transaction information.

## Implementation

### TikiClient

- **Methods:**
  - `initialize(context: Context, providerId: String, userId: String, company: CompanyInfo)`: Initializes the TikiClient with the application context and sets its parameters.
  - `scan()`: Initiates the process of scanning a physical receipt and returns the receipt ID.
  - `login(provider: EmailProviderEnum)`: Initiates the process of scraping receipts from emails.
  - `logout(email: String)`: Removes a previously added email account.
  - `accounts()`: Retrieves the list of connected email accounts.
  - `scrape()`: Initiates the process of scraping receipts from emails.
  - `card(last4: String, bin: String, issuer: String, network: String)`: Adds a card for card-linked offers.
  - `offers()`: Retrieves card-linked offers for the user.
  - `transaction(transaction: Transaction)`: Submits a transaction for card-linked offer matching.
  - `rewards()`: Retrieves information about the user's rewards.
  - `widget(theme: Theme?)`: Displays the widget for pre-built UIs with custom theme.

### TikiClient.auth

- **Methods:**
  - `authenticate(): String`: Authenticates with TIKI and saves the auth and refresh tokens.
  - `token(): String`: Retrieves the authentication token, refreshing if necessary.
  - `revoke(): Unit`: Revokes the authentication token.
  - `refresh(): String`: Refreshes the authentication token.

### TikiClient.license

- **Methods:**
  - `create(): LicenseRecord`: Creates a new license for the user.
  - `get(): License`: Retrieves the user's active license.
  - `revoke(): License`: Revokes the user's existing license.
  - `verify(): Boolean`: Verifies the validity of the user's license.

### Email

- **Methods:**
  - `login(provider: EmailProvider)`: Authenticates with OAuth and adds an email account for scraping receipts.
  - `accounts(): List<EmailAccount>`: Retrieves the list of connected email accounts.
  - `logout(email: String): Unit`: Removes a previously added email account.

### TikiClient.capture

- **Methods:**
  - `camera(): Bitmap`: Captures an image of a receipt for processing.
  - `email(onPublish: (receiptId: String) -> Unit): Unit`: Downloads potential receipt data from known receipt email senders and publishes it.
  - `publish(data: ImageData): String`: Uploads receipt images or email data for receipt data extraction.
  - `status(receiptId: String): PublishingStatus`: Checks the publishing status of the data.

### TikiClient.clo

- **Methods:**
  - `card(last4: String, bin: String, issuer: String, network: String): Unit`: Adds a card to the user's account.
  - `offers(): List<Offer>`: Retrieves card-linked offers for the user.
  - `rewards(): List<Reward>`: Retrieves information about the user's rewards.
  - `transaction(transaction: Transaction): Unit`: Sends transaction information to match card-linked offers.

## Documentation

The library should provide comprehensive documentation including:

1. **Getting Started Guide:** Instructions on how to integrate the library into an Android project.
2. **API Reference:** Detailed documentation for each class, method, and parameter.
3. **Sample Code:** Examples demonstrating how to use key functionalities of the library.
4. **Configuration Guide:** Guidelines on setting up TIKI credentials and other necessary configurations.
5. **Error Handling:** Information on handling errors and exceptions.
6. **Example App** An example app with basic UI, for manual testing.

## Non Native Dependencies

- Retrofit for HTTP communication.
- OpenID AppAuth for OAuth2.
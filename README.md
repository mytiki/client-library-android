# TIKI Client Library - Android Usage Guide

The TIKI Client Library for Android provides a set of APIs that developers can use to publish data to TIKI. This library
simplifies the process of integrating your Android application with TIKI by providing convenient methods for
authorization, licensing, capture, and upload.

## Getting Started

To get started, visit mytiki.com and apply for beta access. Our team will then set up the provider ID and public key for
your project, which you'll use to configure the client.

## Installation

The TIKI Client Library is available in Maven Central reposittory.

1. Check if the Maven Central is included in your project `settings.gradle`:
   In Kotlin:

```kotlin
pluginManagement {
    repositories {
        mavenCentral()
        // ... other repos
    }
}
```

In Groovy

```groovy
pluginManagement {
    repositories {
        maven {
            url "https://plugins.gradle.org/m2/"
        }
        // ... other repos
    }
}
```

2. Add the dependency in the module `build.gradle`:
   In Kotlin:

```kotlin
implementation("com.mytiki:publish-client:0.2.0")
```

In Groovy

```groovy
implementation "com.mytiki:publish-client:0.2.0"
```

3. Sync the project

## Configuration

Before executing any commands with the TikiClient library, you need to configure it. This includes providing the
Provider ID and Public Key obtained during Provider Registration, as well as company information for generating license
terms.

```kotlin
val config = Config(
    providerId = "<PROVIDER-ID>", // Provided by TIKI
    publicKey = "<PUBLIC-KEY>", // Provided by TIKI
    companyName = "ACME Inc",
    companyJurisdiction = "Nashville, TN",
    tosUrl = "https://acme.inc/tos",
    privacyUrl = "https://acme.inc/privacy"
)
TikiClient.configure(config)
```

## How to Use

The TikiClient is a singleton used as the main entry point for all functionalities of the library. The configuration
sets the parameters used by all methods.

### Initialization

This method authenticates with the TIKI API and registers the user's device to publish data. It is an asynchronous
method due to the necessary API calls.

The user ID can be any arbitrary string that identifies the user in the application using the client library. It is not
recommended to use personally identifiable information, such as emails. If necessary, use a hashed version of it.

```kotlin
TikiClient.initialize("<the-client-user-id>")
```

To switch the active user, call the `TikiClient.initialize` method again.

### Receipt OCR

#### Data License

To successfully capture and upload receipt data to our platform, it is essential to establish a valid User Data License
Agreement (UDLA). This agreement serves as a clear, explicit addendum to your standard app terms of service, delineating
key aspects:

- User Ownership: It explicitly recognizes the user as the rightful owner of the data.
- Usage Terms: It outlines the terms governing how the data will be licensed and used.
- Compensation: It defines the compensation arrangements offered in exchange for the provided data.

Our Client Library streamlines this process by providing a pre-qualified agreement, filled with the company information
provided in the library configuration.

Retrieve the formatted terms of the license, presented in Markdown, using the `TikiClient.terms()` method. This allows
you to present users with a clear understanding of the terms before they agree to license their data. This agreement
comes , ensuring a seamless integration into the license registry.

Upon user agreement, generate the license using the `TikiClient.createLicense` method.

```kotlin
val license = TikiClient.createLicense()
```

This method needs to be invoked once for each device. Once executed, the license is registered in TIKI storage,
eliminating the need to recreate it in the future.

#### Data Capture

The Client Library offers an optional method for scanning physical receipts via the mobile device camera.

Use the `TikiClient.scan()` method to initiate the receipt scanning process. This method does not directly return the
scanned receipt data. Instead, it provides the data through a callback function that you supply.

Here's an example of how to use it:

```kotlin
TikiClient.scan(activity) { image: Bitmap ->
    // Handle the scanned bitmap here
}
```

In this example, `image` is the scanned receipt, and the code inside the callback function (
i.e., `// Handle the scanned bitmap here`) is where you can process or use the scanned data.

#### Data Upload

Utilize the `TikiClient.publish` method to upload receipt images to TIKI for processing. This method is versatile, as it
can receive results from the `TikiClient.scan` method, or your application can implement a custom scan extraction
method, sending the results to `TikiClient.publish`.

The `publish` method accepts a bitmap image or an array of bitmap images, providing flexibility to capture and scan
multiple images, ideal for processing lengthy receipts.

```kotlin
val data: Bitmap = ... // The scanned receipt
val result = TikiClient.publish(data)
```

Upon execution, this method returns a `CompletableDeferred` object that will be completed when the data has been
published.

#### Retrieve Results

Once you've uploaded receipt images to TIKI for processing using the `TikiClient.publish` method, you can retrieve the
extracted data associated with a specific receipt by calling the `TikiClient.receipt(receiptId)` method.

```typescript
// Assuming you have the receiptId stored in a variable named 'receiptId'
let receiptData = await TikiClient.receipt(receiptId);
console.log(receiptData);
```

**Note**: The data extraction from receipts is performed asynchronously by Amazon Textract. Processing typically takes a
few seconds, but it can occasionally take up to a minute. It's important to note that making subsequent calls
to `TikiClient.receipt(receiptId)` shortly after using `TikiClient.publish` might lead to unexpected results and
false `404` errors from the API. We recommend allowing sufficient time for the extraction process to complete before
attempting to retrieve the extracted data.

#### Permissions

The TIKI Client Library provides a set of APIs to handle permissions in your Android application. It simplifies the
process of requesting and checking permissions.

##### Requesting Permissions

To request permissions, use the `TikiClient.permissions` method. This method accepts a `ComponentActivity` instance, a
list of permissions to request, and a callback function to handle the result of the permissions request.

Here's an example of how to use it:

```kotlin
TikiClient.permissions(
    activity,
    listOf(Permission.CAMERA, Permission.MICROPHONE),
    { permissionsResult ->
        // Handle the result of the permissions request here
        // permissionsResult is a map where the keys are the requested permissions
        // and the values are Booleans indicating whether each permission was granted.
    }
)
```

In this example, activity is the current activity from which the permissions request is made. The permissionsResult is a
map where the keys are the requested permissions and the values are Booleans indicating whether each permission was
granted. This map is passed to the callback function where you can handle the result of the permissions request.

#### Checking Permissions

To check if a specific permission is authorized, use the `TikiClient.isPermissionAuthorized` method. This method accepts
a `Context` instance and a `Permission` instance representing the permission to check.

Here's an example of how to use it:

```kotlin
val isCameraAuthorized = TikiClient.isPermissionAuthorized(context, Permission.CAMERA)
```

In this example, `context` is the current activity or application context from which the check is made,
and `Permission.CAMERA` is the permission to check. The method returns a Boolean indicating whether the permission is
granted.

## API Reference

The central API interface in the library is the `TikiClient` object, designed to abstract the complexities of
authorization and API requests. While serving as the primary entry point, it's important to note that all APIs within
the library are public and accessible.

For detailed usage instructions, please consult the [TIKI Client API Documentation](https://android.client.mytiki.com).
This comprehensive resource provides direct insights into utilizing the various functionalities offered by the TIKI
Client Library.

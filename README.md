# monri-android
Our Android libraries let you easily accept mobile payments and manage customer information inside any Android app.

Monri has created a Java library for Android, allowing you to easily submit payments from an Android app. With our mobile library, we address PCI compliance by eliminating the need to send card data directly to your server. Instead, our libraries send the card data directly to our servers, where we can convert them to [tokens](https://monri.com/docs/api#tokens).

Your app will receive the token back, and can then send the token to an endpoint on your server, where it can be used to process a payment.

We support Android 4.4 (API level 19) and above.

## Installation[](https://monri.com/docs/mobile/android#installation)

Installing the Monri Android library is simple using  [Android Studio](https://developer.android.com/studio/intro)  and  [IntelliJ](https://www.jetbrains.com/help/idea/getting-started-with-android-development.html). You don’t need to clone a repo or download any files. Just add the following to your project’s  `build.gradle`  file, inside the dependencies section.

```gradle
implementation 'com.monri:monri-android:1.2.+'
```
# Payment API Integration

At some point in the flow of your app you'll obtain payment details from the user. After that you could:
- use obtained payment details and proceed with charge (confirmPayment)
- or tokenize obtained payment details for server side usage

In [Payment API Integration](https://github.com/MonriPayments/monri-android/wiki/Payment-API-Integration) it's explained how to:
- create payment
- collect payment details
- confirm payment
- get results back on your app and on your backend

If you want to tokenize obtained payment details then continue to the "Tokens API Integration"

# Tokens API Integration

After you've obtained payment details it's easy to securely transfer collected data via Tokens API.

In [Tokens API Integration](https://github.com/MonriPayments/monri-android/wiki/Tokens-API-Integration) it's explained how to:
- create token request
- create token
- how to use created token for transaction authorization on your backend

# Questions

If you have any questions about documentation/APIs/flow do not hesitate to contact us at support@monri.com

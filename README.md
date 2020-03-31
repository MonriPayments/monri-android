# monri-android
Our Android libraries let you easily accept mobile payments and manage customer information inside any Android app.

Monri has created a Java library for Android, allowing you to easily submit payments from an Android app. With our mobile library, we address PCI compliance by eliminating the need to send card data directly to your server. Instead, our libraries send the card data directly to our servers, where we can convert them to [tokens](https://monri.com/docs/api#tokens).

Your app will receive the token back, and can then send the token to an endpoint on your server, where it can be used to process a payment.

We support Android 4.4 (API level 19) and above.

## Installation[](https://monri.com/docs/mobile/android#installation)

Installing the Monri Android library is simple using  [Android Studio](https://developer.android.com/studio/intro)  and  [IntelliJ](https://www.jetbrains.com/help/idea/getting-started-with-android-development.html). You don’t need to clone a repo or download any files. Just add the following to your project’s  `build.gradle`  file, inside the dependencies section.

```gradle
implementation 'com.monri:monri-android:1.1.4'
```
## Collecting credit card information[](https://monri.com/docs/mobile/android#collecting-credit-card-information)

At some point in the flow of your app, you’ll want to obtain payment details from the user. There are a couple ways to do this:
-   [Use our built-in card input widget to collect card information](https://monri.com/docs/mobile/android#card-input-widget)
-   [Build your own credit card form](https://monri.com/docs/mobile/android#credit-card-form)

Instructions for each route follows, although you may want to write your app to offer support for both.

## Using the card input widget
To collect card data from your customers directly, you can use Monri’s [CardInputWidget](https://github.com/monri/monri-android/blob/master/monri/src/main/java/com/monri/android/view/CardInputWidget.java) in your application. You can include it in any view’s layout file.

```
<com.monri.android.view.CardInputWidget
    android:id="@+id/card_input_widget"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```
This allows your customers to input all of the required data for their card: the number, the expiration date, and the CVV code. Note that the value of the `Card` object is `null` if the data in the widget is either incomplete or fails client-side validity checks.

```
import com.monri.android.view.CardInputWidget;
CardInputWidget cardInputWidget = (CardInputWidget) findViewById(R.id.card_input_widget);

Card cardToSave = cardInputWidget.getCard();
if (cardToSave == null) {
    errorDialogHandler.showError("Invalid Card Data");
}
```
If you have any other data that you would like to associate with the card, such as name, address, or ZIP code, you can put additional input controls on your layout and add them directly to the `Card` object.

```
cardToSave = cardToSave.toBuilder().name("Customer Name").build();
cardToSave = cardToSave.toBuilder().addressZip("12345").build();
```
## Building your own form[](https://monri.com/docs/mobile/android#credit-card-form)

If you build your own payment form, you’ll need to collect at least your customers’ card numbers and expiration dates. Monri strongly recommends collecting the CVC. You can optionally collect the user’s name and billing address for additional fraud protection.

Once you’ve collected a customer’s information, you will need to exchange the information for a Monri token.

### Creating & validating cards from a custom form[](https://monri.com/docs/mobile/android#creating-cards)
To create a `Card` object from data you’ve collected from other forms, you can create the object with its constructor.

```
import com.monri.android.model.Card;

//...
//...
public void onAddCard(String cardNumber, String cardExpMonth,
                      String cardExpYear, String cardCVC) {
  final Card card = Card.create(
    cardNumber,
    cardExpMonth,
    cardExpYear,
    cardCVC
  );

  card.validateNumber();
  card.validateCVC();
}
```
As you can see in the example above, the `Card` instance contains some helpers to validate that the card number passes the Luhn check, that the expiration date is the future, and that the CVC looks valid. You’ll probably want to validate these three things at once, so we’ve included a `validateCard` function that does so.

```
// The Card class will normalize the card number
final Card card = Card.create("4242-4242-4242-4242", 12, 2020, "123");
if (!card.validateCard()) {
  // Show errors
}
```
### Securely sending payment information to Monri

```java
final Card card = Card.create("4111111111111111", 12, 2020, "123");
// Remember to validate the card object before you use it to save time.
if (!card.validateCard()) {
  // Do not continue token creation.
}
```
You can also simply take the data from a `CardInputWidget`.

```java
// Remember that the card object will be null if the user inputs invalid data.
Card card = cardInputWidget.getCard();
if (card == null) {
  // Do not continue token creation.
}
```
However you create your `Card` object, you can now use it to collect payment.
### Tokens api

```java
final Monri monri = new Monri(
    getContext(),
    "authenticity_token",
);

final TokenRequest tokenRequest = new TokenRequest(
  "random-token", // Random UUID
  "digest", // SHA512{merchant.key}\#{random-token}\#{timestamp}()
  "timestamp" // valid ISO date string
);

monri.createToken(
	tokenRequest,
    	card,
    	new TokenCallback() {
        	public void onSuccess(@NonNull Token token) {
	        // Send token to your server
        	}

	        public void onError(@NonNull Exception error) {
	        // Show localized error message
            	Toast.makeText(
			getContext(),
                	error.getLocalizedString(),
                	Toast.LENGTH_LONG
            	).show();
        }
    }
)
```
> Authenticity token should be replaced with live authenticity token in production.

## Using tokens[](https://monri.com/docs/mobile/android#using-tokens)

Using the payment token, however it was obtained, requires an API call from your server using your secret merchant key. (For security purposes, you should never embed your secret merchant key in your app.)

Set up an endpoint on your server that can receive an HTTP POST call for the token. In the `onSuccess`  callback (when using your own form), you’ll need to POST the supplied token to your server. Make sure any communication with your server is  [SSL secured](https://monri.com/docs/security) to prevent eavesdropping.

1\. Why integrate Ometria in a mobile app?
------------------------------------------

Ometria helps your marketing department understand and better engage with your customers by delivering personalised emails and push notifications.

The app has two key objectives:

1. Getting information about customers (what do they like?)
2. Reaching out to customers (saying the right thing to the right people).

For your mobile app, this means:

1. Tracking customer behaviour through events - handled by Ometria.
2. Sending and displaying push notifications - requires the app developers.

2\. Before you begin
----------------------

See [Setting up your mobile app with Firebase credentials](https://support.ometria.com/hc/en-gb/articles/360013658478-Setting-up-your-mobile-app-with-Firebase-credentials) in the Ometria help centre and follow the steps there to get an API key.

3\. Install the library
-----------------------

### Step 1 - Add the Ometria-android library as a gradle dependency:

We publish builds of our library to the **Maven central repository** as an .aar file. 

This file contains all of the classes, resources, and configurations that you'll need to use the library. 

To install the library inside **Android Studio**, declare it as dependency in your app level `build.gradle` file: 

```gradle
dependencies {
    implementation 'com.ometria:android-sdk:1.0.8'
}
```

### Step 2 - Perform Gradle Sync:

Perform a **Gradle Sync** to build your project and incorporate the dependency additions noted above:

![Screenshot 2020-08-13 at 16 47 21](https://user-images.githubusercontent.com/8706456/91582697-d51c0a80-e958-11ea-96eb-46f0d49c3d59.png)

This downloads the aar dependency, giving you access to the Ometria library API calls. 

If it can’t find the dependency, you should make sure you've specified `mavenCentral()` as a repository in your `build.gradle`.

4\. Initialise the library
--------------------------
Once you've set up your build system or IDE to use the Ometria library, you can initialise it in your code. 

We recommend initialising the SDK in your Application subclass. You’ll need to provide:
⋅⋅* the application context;
⋅⋅* your Ometria API token, and;
⋅⋅* the notifications icon.

```kotlin
Ometria.initialize(this, "YOUR_API_TOKEN", R.mipmap.ic_launcher)
```

Ometria logs any errors encountered during runtime by default. 

You can enable advanced logging if you want more information on what’s happening in the background. Just add the following line after initialising the library:

```kotlin
Ometria.initialize(this, "YOUR_API_TOKEN", R.mipmap.ic_launcher)
            .loggingEnabled(true)
```

5\. Event tracking guide
------------------------
You need to be aware of your users’ behaviour on your platforms in order to understand them. Some behaviour is automatically detectable, other events need work from the app developer to track.

Many of these methods have analogous events in a server-to-server API called the [Ometria Data API] (https://support.ometria.com/hc/en-gb/articles/360011511017-Data-API-introduction), and through a separate JavaScript API. 

**Be aware**: If your business already integrates with Ometria in any way, it is very important that the values sent here correspond to those in other integrations.

E.g., the customer identified event takes a customer ID - that ID must be the same here as it is in the data API. 

The events are merged on Ometria's side into one big cross-channel view of your customer behaviour, which will otherwise get very messy.

### Manually tracked events

Once the SDK is initialised, you can track an event by calling its dedicated method:

```kotlin
val myItem = OmetriaBasketItem(
        productId = "product-1",
        sku = "sku-product-1",
        quantity = 1,
        price = 12.0f)
val myItems = listOf(myItem)
val basket = OmetriaBasket(totalPrice = 12.0f, currency = "USD", items = myItems)

Ometria.instance().trackBasketUpdatedEvent(basket = basket)
```

#### Profile identified

An app user has just identified themselves, i.e. logged in.

```kotlin
trackProfileIdentifiedByCustomerIdEvent(customerId: String)
```

Their **customer ID** is their **user ID** in your database.

Sometimes a user only supplies their email address without fully logging in or having an account. In that case, Ometria can profile match based on email:

```kotlin
trackProfileIdentifiedByEmailEvent(email: String)
```

Having a **customerId** makes profile matching more robust. 

It’s not mutually exclusive with sending an email event; for optimal integration you should send either event as soon as you have the information.

These two events are pivotal to the functioning of the SDK, so make sure you send them as early as possible.

#### Profile deidentified

Undo a **profileIdentified** event.

Use this if a user logs out, or otherwise signals that this device is no longer attached to the same person.

```kotlin
trackProfileDeidentifiedEvent()
```

#### Product Viewed

A visitor clicks / taps / views / highlights or otherwise shows interest in a product.

E.g. the visitor searches for a term and selects one of the product previews from a set of results, or browses a category of clothes, and clicks on a specific shirt to see a bigger picture. 

This event is about capturing interest from the visitor for this product.


```kotlin
trackProductViewedEvent(productId: String)
```

The product details must be sent to Ometria separately, e.g. using the server-to-server data API, or an eCommerce platform integration (like Shopify).

The value is opaque, and is only used to create segments and automation campaigns in the Ometria app. 

Use non-localised, predictable, human readable slugs. E.g. "womens-footwear".

#### Wishlist events

The user has added this product to their wishlist:

```kotlin
trackWishlistAddedToEvent(productId: String)
```

... or removed it:

```kotlin
trackWishlistRemovedFromEvent(productId: String)
```

#### Basket viewed

The user has viewed a dedicated page, screen or modal with the contents of the shopping basket.

```kotlin
trackBasketViewedEvent()
```

#### Basket updated

The user has changed their shopping basket.

```kotlin
trackBasketUpdatedEvent(basket: OmetriaBasket)
```

This event takes the full current basket as a parameter - not just the updated parts.

This helps recover from lost or out of sync basket events: the latest update is always authoritative.

#### Checkout started

The user has started the checkout process.

```kotlin
trackCheckoutStartedEvent(orderId: String)
```

#### Order completed

The order has been completed and paid for.

```kotlin
trackOrderCompletedEvent(orderId: String, basket: OmetriaBasket)
```

#### Deep link opened
Based on the implementation status of interaction with notifications that contain deeplinks, this event can be automatically tracked or not.

The default implementation will automatically log a deep link opened event every time the user interacts with a notification that has a deep link. This is possible because we know that the default implementation will open the link in a browser.

If you chose to handle deep links yourself (see: [Handling interaction with notifications that contain URLs](#handling_interaction_with_notifications_that_contain_urls)) then you should manually track this event when you have enough information about the screen (or other destination) that the app will open.

```kotlin
trackDeepLinkOpenedEvent(link: String, page: String)
```

#### View home page

The user views the "home page" or landing screen of your app:

```kotlin
trackHomeScreenViewedEvent()
```

#### View list of products

The visitor clicks/taps/views/highlights or otherwise shows interest in a product listing. This kind of screen includes search results, listings of products in a group, category, collection or any other screen that presents a list of products.

E.g., A store sells clothing, and the visitor taps on "Women's Footwear" to see a list of products in that category, or they search for "blue jumper" and see a list of products in that category.

This event should be triggered on:

⋅⋅* search results
⋅⋅* category lists
⋅⋅* any similar screens

```kotlin
trackProductListingViewedEvent(listingType: String? = null, listingAttributes: Map<String, Any> = mapOf())
```

#### Screen viewed

Tracking a visitor’s independent screen views helps us track their engagement with the app, as well as where they are in a journey. 

An analogous event on a website would be to track independent page views.

The common eCommerce screens all have their own top-level event: basket viewed, list of products viewed, etc. 

Your app may have a specific type of page that is useful for marketers to track engagement with. 

E.g. if you’re running a promotion, and viewing a specific screen indicates interest in the promotion, which marketing might later want to follow up on. 
To track these custom screens, use the **Screen viewed** event:

```kotlin
trackScreenViewedEvent(screenName: String, additionalInfo: Map<String, Any> = mapOf())
```

For example:

```kotlin
trackScreenViewedEvent("promotion", mapOf("promotion-id" to "summer-2020"))
```

#### Custom events

Your app might have specific flows or pages that are of interest to the marketing team.

E.g. Marketing might want to send an email or notification to any user who signed up for a specific promotion, or interacted with a button or specific element of the app. 

If you send a custom event corresponding to that action, they will be able to trigger an [automation campaign](https://support.ometria.com/hc/en-gb/articles/360011378398-Automation-campaigns-overview) on it.

Check with the marketing team about the specifics, and what they might need. Especially if they're already using Ometria for email, they will know about automation campaigns and custom events.

```kotlin
trackCustomEvent(customEventType: String, additionalInfo: Map<String, Any> = mapOf())
```

### Automatically tracked events

The following events are automatically tracked by the SDK.

Linking and initialising the SDK is enough to take advantage of these; no further integration is required.

| Events        | Description   |
| ------------- |---------------| 
| **Application installed**| The app was just installed. Usually can't be sent when the app is actually installed, but instead only sent the first time the app is launched.|
| **Application launched**| Someone has just launched the app.|  
| **Application foregrounded** | The app was already launched, but it was in the background. It has just been brought to the foreground.|
| **Application backgrounded** | The app was in active use and has just been sent to the background.|
| **Push token refreshed** | The push token generated by Firebase has been updated.|
| **Notification recieved** | A Push notification was received by the system.|
| **Notification interacted** | The user has just clicked on / tapped on / opened a notification.|
|**Error occurred** | An error occurred on the client side. We try to detect any problems with actual notification payload on our side, so we don't expect any errors which need to be fed back to end users.|

### Flush tracked events

In order to reduce power and bandwidth consumption, the Ometria library doesn’t send the events one by one unless you request it to do so. 

Instead, it composes batches of events that are periodically sent to the backend during application runtime. 

You can request the library to send all remaining events to the backend whenever you want by calling:

```kotlin
Ometria.instance().flush()
```

The library will automatically call this method every time the application is brought to foreground or sent to background.

### Clear tracked events

You can completely clear all the events that have been tracked and not yet flushed. In order to do so you simply have to call the following method:

```kotlin
Ometria.instance().clear()
```

6\. Push notifications guide
----------------------------

Ometria relies on Firebase Cloud Messaging in order to send push notifications to the mobile devices.

Follow [Firebase's Get Started tutorial](https://firebase.google.com/docs/android/setup) or use the Firebase Cloud Messaging wizard in Android studio. 
Ometria requires firebase-messaging, we recommend using the latest version. 

Add the following to your build.gradle, if not already present:

```gradle
implementation 'com.google.firebase:firebase-messaging:20.2.4'
```

### Option 1 - Reference OmetriaFirebaseMessagingService in AndroidManifest file

You can use this implementation in a scenario where the only push notifications received by your app are the Ometria ones.

Then, all you have to do is to reference the **OmetriaFirebaseMessagingService** inside your **AndroidManifest** file:

```xml
<service
    android:name="com.android.ometriasdk.notification.OmetriaFirebaseMessagingService"
    android:exported="false">
    <intent-filter>
        <action android:name="com.google.firebase.MESSAGING\_EVENT" />
    </intent-filter>
</service>
```

Ometria SDK will handle intercepting and displaying push notifications out of the box.

### Option 2

You can use this implementation when your app should receive other push notifications as well as the Ometria ones.

Create your own `Service` class that extends the `OmetriaFirebaseMessagingService`. 

You will have to override the next base class methods and call `super`:

```kotlin
override fun onMessageReceived(remoteMessage: RemoteMessage) {
    super.onMessageReceived(remoteMessage)
}

override fun onNewToken(token: String) {
    super.onNewToken(token)
}
```
Now reference your `Service` inside the `AndroidManifest` file:

```xml
<service
    android:name=".YourFirebaseMessagingService"
    android:exported="false">
    <intent-filter>
        <action android:name="com.google.firebase.MESSAGING\_EVENT" />
    </intent-filter>
</service>
```
Ometria SDK handles intercepting and displaying Ometria specific push notifications and you can manage the rest of them.

**In case your own** `Service` **needs to extend another** `FirebaseMessagingService` **subclass and you cannot extend** `OmetriaFirebaseMessagingService`, you will have to provide Ometria SDK with the `remoteMessage` and `token` so it can handle Ometria specific push notifications. 

Do this by calling `onMessageReceived(remoteMessage)` and `onNewToken(token)` from your overridden base class methods:

```kotlin
override fun onMessageReceived(remoteMessage: RemoteMessage) {
    super.onMessageReceived(remoteMessage)

    Ometria.instance().onMessageReceived(remoteMessage)
}

override fun onNewToken(token: String) {
    super.onNewToken(token)

    Ometria.instance().onNewToken(token)
}
```

### Handling interaction with notifications that contain URLs

Ometria allows you to send URLs alongside your push notifications and allows you to handle them on the device. 

By default, the Ometria SDK automatically handles any interaction with push notifications that contain URLs by opening them in a browser.

However, it enables developers to handle those URLs as they see fit (e.g. take the user to a specific screen in the app).

To get access to those interactions and the URLs, implement the `OmetriaNotificationInteractionHandler`

There is only one method that is required, and it will be triggered every time the user taps on a notification that has a deepLink action URL. 

This is what it would look like in code:

```kotlin
class SampleApp : Application(), OmetriaNotificationInteractionHandler {
    companion object {
        lateinit var instance: SampleApp
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Initializing Ometria SDK with application context, api token and notifications icon resource id
        // Note: Replace api token with your own
        Ometria.initialize(
            this,
            "YOUR_API_TOKEN",
            R.mipmap.ic_launcher
        ).loggingEnabled(true)

        // Set the notificationInteractionDelegate in order to provide actions for
        // notifications that contain a deepLink URL.
        // The default functionality when you don't assign a delegate is opening urls in a browser
        Ometria.instance().notificationInteractionHandler = this
    }

    /**
     * This method will be called each time the user interacts with a notification from Ometria
     * which contains a deepLinkURL. Write your own custom code in order to
     * properly redirect the app to the screen that should be displayed.
     */
    override fun onDeepLinkInteraction(deepLink: String) {
        Log.d(SampleApp::class.java.simpleName, "URL: $deepLink")
    }
}
```

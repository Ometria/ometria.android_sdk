1\. Why integrate Ometria in a mobile app?
------------------------------------------

Ometria helps the marketing department understand, and better engage with, your customers. This is done through e-mails, and now push notifications. At its core, integrating Ometria in your mobile app is about helping the marketing department do their job.

For this to work optimally, there are two parts to Ometria:

1. Getting information about customers (what do they like?)
2. Reaching out to customers (say the right thing to the right people)

For your mobile app, this means:

1. Tracking customer behaviour through the given events
2. Sending and displaying push notifications

The sending and displaying of push notifications is handled by Ometria behind the scenes, and requires little configuration. Tracking customer behaviour, however, needs help from the app developers. That's what most of this document is about.

2\. Prerequisite Steps
----------------------

In order to obtain an API token please follow the instructions [here](https://support.ometria.com/hc/en-gb/articles/360013658478-Setting-up-your-mobile-app-with-Firebase-credentials)

3\. Install the Library
-----------------------

### Step 1 - Add the Ometria-android library as a gradle dependency:

We publish builds of our library to the Maven central repository as an .aar file. This file contains all of the classes, resources, and configurations that you'll need to use the library. To install the library inside Android Studio, you can simply declare it as dependency in your app level `build.gradle` file.

```gradle
dependencies {
    implementation 'com.ometria:android-sdk:1.+'
}
```
### Step 2 - Perform Gradle Sync:

Be sure to perform a **Gradle Sync** to build your project and incorporate the dependency additions noted above.

![Screenshot 2020-08-13 at 16 47 21](https://user-images.githubusercontent.com/8706456/91582697-d51c0a80-e958-11ea-96eb-46f0d49c3d59.png)

This should download the aar dependency at which point you'll have access to the Ometria library API calls. If it cannot find the dependency, you should make sure you've specified `mavenCentral()` as a repository in your `build.gradle`.

4\. Initialise the Library
--------------------------

Once you've set up your build system or IDE to use the Ometria library, you can initialise it in your code. We recommend initialising the SDK in your `Application` subclass. You will have to provide the application context, your Ometria API token and the notifications icon.
```kotlin
Ometria.initialize(this, "YOUR_API_TOKEN", R.mipmap.ic_launcher)
```
By default, Ometria logs any errors encountered during runtime. You can enable advanced logging if you want more information on what is happening in the background. In order to enable logging, you will be required to add the following line after initialising the library:
```kotlin
Ometria.initialize(this, "YOUR_API_TOKEN", R.mipmap.ic_launcher)
            .loggingEnabled(true)
```
5\. Event Tracking Guide
------------------------

To better understand your users, it is necessary to be aware of their behaviour on your platforms (in this case, the app). Some of that behaviour is automatically detectable, other events need the help of the app developer to track.

Many of these methods have analogous events in a server-to-server API called the "Ometria Data API", and through a separate JavaScript API. _If your business already integrates with Ometria in any way, it is very important that the values sent here correspond to those in other integrations!_

For example, the customer identified event takes a customer ID: that ID must be the same here as it is in the data API. Not e.g. a firebase ID here, and a Postgres autoincrement ID there. The events are merged on Ometria's side into one big cross-channel view of your customer behaviour, which will otherwise get very messy.

### Manually Tracked Events

This section is the meat and potatoes of the Ometria SDK integration. The quality of the integration of these events will translate to direct gains for the marketing department. The richer and more accurate the events, the better the marketing team can leverage them into efficient and rewarding customer outreach.

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


#### Profile Identified

An app user has just identified themselves. This basically means: a user has logged in.

```kotlin
trackProfileIdentifiedByCustomerIdEvent(customerId: String)
```

Their customer ID being their user ID in your database.

Sometimes a user only supplies their e-mail address, without fully logging in or having an account. In that case, Ometria can do profile matching based on e-mail:

```kotlin
trackProfileIdentifiedByEmailEvent(email: String)
```

But having a customerId makes profile matching more robust. It is not mutually exclusive with sending an e-mail event: send either event as soon as you have the information, for optimal integration.

These two events are absolutely pivotal to the functioning of the SDK, so take care to send them as early as possible.

#### Profile Deidentified

Undo a profileIdentified event.

Use this if a user logs out, or otherwise signals that this device is no longer attached to the same person.

```kotlin
trackProfileDeidentifiedEvent()
```

#### Product Viewed

A visitor clicks / taps / views / highlights or otherwise shows interest in a product.

Think for example searching for a term, and selecting one of the product previews from a set of results. Or browsing a category of clothes, and clicking on a specific shirt to see a larger picture. This event is about capturing interest from the visitor for this product.

```kotlin
trackProductViewedEvent(productId: String)
```

The product details must be sent to Ometria separately, using for example the server-to-server data API, or an e-commerce platform integration (like Shopify).


The value is opaque, and is only used to create segments and automation campaigns in the Ometria app. Stick to non-localised, predictable, human readable slugs. E.g. "womens-footwear".

#### Wishlist events

The user has added this product to their wishlist:

```kotlin
trackWishlistAddedToEvent(productId: String)
```

... or removed it:

```kotlin
trackWishlistRemovedFromEvent(productId: String)
```

#### Basket Viewed

The user has viewed a dedicated page, screen or modal with the contents of the shopping basket.

```kotlin
trackBasketViewedEvent()
```

#### Basket Updated

The user has changed their shopping basket.

```kotlin
trackBasketUpdatedEvent(basket: OmetriaBasket)
```

This event takes the full current basket as a parameter; not just the updated parts. This is laborious, but it helps recover from lost or out of sync basket events: the latest update is always authoritative.

#### Order Completed

The order has been completed and paid for.

```kotlin
trackOrderCompletedEvent(orderId: String, basket: OmetriaBasket)
```

#### View Home Page

The user views the "home page" or landing screen of your app.
```kotlin
trackHomeScreenViewedEvent()
```

#### View List of Products

The user clicks / taps / views / highlights or otherwise shows interest in a product listing. This kind of screen includes search results, listing of products in a group, category, collection or any other screen that presents a list of products.

For example, a store sells clothing, and they tap on "Women's Footwear" to see a list of products in that category.

Another exmple: they search for "blue sweater" and see a list of products in that category.

Concretely, this event should at least be triggered on:

* search results
* category lists
* any similar such screen
```kotlin
trackProductListingViewedEvent()
```

#### Screen viewed

Tracking a user's independent screen views helps us track engagement of a user with the app, as well as where they are in a journey. An analogous event on a website would be to track independent page views.

The common ecommerce screens all have their own top-level event: basket viewed, list of products viewed, etc. However, your app may have a specific type of page that is useful for marketers to track engagement of. Imagine a type of promotion, where viewing the screen indicates interest in the promotion, which marketing might later want to follow up on. To track these custom screens, use the _Screen Viewed_ event:

```kotlin
trackScreenViewedEvent(screenName: String, additionalInfo: Map<String, Any> = mapOf())
```

For example:

```kotlin
trackScreenViewedEvent("promotion", mapOf("promotion-id" to "summer-2020"))
```

#### Custom events

Your app may have specific flows or pages that are of interest to the marketing department. They may want to send an e-mail or notification to any user who e.g. signed up for a specific promotion, or interacted with a button or specific element of the app. If you send a custom event corresponding to that action, they will be able to trigger what's called an "automation campaign" on it.

Check with the marketing team about the specifics, and what they might need. Especially if they're already using Ometria for e-mail, they will know about automation campaigns and custom events.

```kotlin
trackCustomEvent(customEventType: String, additionalInfo: Map<String, Any>)
```

### Automatically Tracked Events

For the record, here are the events that are automatically tracked by the SDK. Linking and initialising the SDK is enough to take advantage of these; no further integration is required.

*   **Application Installed** - The app was just installed. Usually can't be sent when the app is actually installed, but instead only sent the first time the app is launched.
*   **Application Launched** - Someone has just launched the app.
*   **Application Foregrounded** - The app was already launched, but it was in the background. It has just been brought to the foreground.
*   **Application Backgrounded** - The app was in active use and has just been sent to the background.
*   **Push Token Refreshed** - The push token generated by Firebase has been updated.
*   **Notification Received** - A Push notification was received by the system.
*   **Notification Interacted** - The user has just clicked on / tapped on / opened a notification.
*   **Error Occurred** - An error occurred on the client side. We try to detect any problems with actual notification payload on our side, so we don't expect any errors which need to be fed back to end users.

### Flush Tracked Events

In order to reduce power and bandwidth consumption, the Ometria library doesn’t send the events one by one (unless you request it to do so). Instead it composes batches of events that are sent perioadically to the backend, during application runtime. You can request the library to send all remaining events to the backend whenever you want, by calling:

```kotlin
Ometria.instance().flush()
```

The library will automatically call this method every time the application brought to foreground or sent to background.

### Clear Tracked Events

You can completely clear all the events that have been tracked and not yet flushed. In order to do so you simply have to call the following method:

```kotlin
Ometria.instance().clear()
```

6\. Push Notifications Guide
----------------------------

Ometria relies on Firebase Cloud Messaging in order to send push notifications to the mobile devices. Please follow [Firebase's Get Started tutorial](https://firebase.google.com/docs/android/setup) or use the Firebase Cloud Messaging wizard in Android studio. Ometria requires `firebase-messaging`, We highly recommend to use the latest version when possible. Add the following to your `build.gradle`, if not already present:

```gradle
implementation 'com.google.firebase:firebase-messaging:20.2.4'
```

### Option 1 - Reference OmetriaFirebaseMessagingService in AndroidManifest file

You can use this implementation in a scenario where the only push notifications received by your app are the Ometria ones. Then, all you have to do is to reference the `OmetriaFirebaseMessagingService` inside your `AndroidManifest` file:

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

You can use this implementation when your app should receive push notifications in addition to the Ometria ones.

Create your own `Service` class that extents the `OmetriaFirebaseMessagingService`. You will have to override the next base class methods and call `super`:

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
Ometria SDK will handle intercepting and displaying Ometria specific push notifications and you can manage the rest of them.

**In case your own** `Service` **needs to extend another** `FirebaseMessagingService` **subclass and you cannot extend** `OmetriaFirebaseMessagingService`, you will have to provide Ometria SDK with the `remoteMessage` and `token` so it can handle Ometria specific push notifications. Do this by calling `onMessageReceived(remoteMessage)` and `onNewToken(token)` from your overridden base class methods:

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

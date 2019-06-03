# android-coroutine-messenger
A messenger solution using [Kotlin coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) to easily communicate between Android components.

The goal of this library is to make it easier for Android applications that are already using Kotlin coroutines to communicate between Android components (services and activities). This can be used either internally in an application or to communicate between different applications.

This project was inspired by (Android Rx-Messenger)[https://github.com/Aevi-UK/android-rxmessenger], an open source project I've contributed to from working with AEVI.

**Note** - This project is currently in a proof of concept stage only and is not ready for real use nor are the artifacts published anywhere yet.

## Motivation
There is a lot of boilerplate code required to bind to services and set up a mechanism to send messages between services. In addition, it is crucial that a sensible threading strategy is in place to ensure the main thread can focus on UI and not I/O.

This library will integrate nicely with applications that already use coroutines to manage asynchronous tasks, which will be increasingly common now that Google themselves endorse Kotlin and provide coroutine support in their Jetpack libraries.

## Technical details
[Bound services](https://developer.android.com/guide/components/bound-services) are used to establish a connection between services, after which [Messengers](https://developer.android.com/guide/components/bound-services#Messenger) are used to send messages.

[Channels](https://kotlinlang.org/docs/reference/coroutines/channels.html) are exposed by the library to allow the application to receive and send messages.

## Client

The client can virtually be driven from any Android component (service, activity/fragment, content provider, etc). The lifetime of the coroutine scope is entirely down to the application and its use case.

### Client set up

The below code illustrates a basic use from an Android Activity where we launch a coroutine scope, loop whilst the scope is active (cancelled upon onStop / onDestroy), and use a coroutine select to listen to messages from the other end as well as UI input events. The scope we launch is on the main/UI thread to allow us to update and listen to views. The library under the hood dispatches all communication onto separate worker threads. Remember that none of this code is blocking the main thread - calls like `connect`, etc are _suspending_ functions.

```kotlin
private val scope = CoroutineScope(Dispatchers.Main)

private fun execute() {
  scope.launch {
      val ancomeClient = AncomeClient(androidContext)
      // Connect (bind) to an Android service
      val ancomeMessenger = ancomeClient.connect(ComponentName("package", "service class"))
      while (isActive) { // While the scope is active
          // Select allows us to listen to multiple channels
          select<Unit> {
              // Listen to incoming messages from the other service
              ancomeMessenger.receiveChannel().onReceive {
                  Log.i("Ancome", "Received message: $it)
              }

              // Listen to click events from Android UI and send messages to the other service
              sendMessageButton.listen().onReceive {
                  Log.i("Ancome", "Send message: $it")
                  ancomeMessenger.sendChannel().send($it)
              }
          }
      }
  }
}
```

## Server
On the receiving end, we must implement an Android service extending from the library base class `AncomeService`. (In the first release, it's possible that this inheritance structure won't be required).

```kotlin
class ExampleService : AncomeService() {

    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onNewClient(messenger: AncomeMessenger) {
        scope.launch {
            // Simply loop (and suspend) for incoming messages, and reply back after a 1 second delay
            for (message in messenger.receiveChannel()) {
                Log.i("ServerService", "server received message")
                delay(1000)
                messenger.sendChannel().send("Hello I received: $message")
                Log.i("ServerService", "Sent response")
            }
        }
    }
}

```

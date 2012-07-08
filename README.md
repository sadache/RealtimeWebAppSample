**One of the goals of [Play2](www.playframework.org) [architecture](http://sadache.tumblr.com/post/26258782102/bitsbout-play2-architecture) is to provide a programming model for what is called Realtime Web Applications.**

### Realtime Web Applications

Realtime Web Applications are applications making use of Websockets, Server Sent Events, Comet or other protocols offering/simulating an open socket between the browser and the server for continuous communication. Basically, these applications offer to users delivery of information as it is published without having the user periodically pinging the service.

There are quite a few web frameworks that target the development of this type of applications. Mostly, however, the solution is by providing an API that allows developers to push/receive messages from/to an open channel, something like:

    channel.push(message)
    //and
    channel.onMessage { ... }

Though this kind of API offers an opportunity to get started doing Realtime Web, it doesn't offer a _programming model_ for dealing with the challenges encountered when programming with streams of data, including creating, adapting, manipulating, filtering and merging streams of data and all of the synchronization involved.

### A Programming Model

Since development of Realtime Web Apps is mostly built around manipulation of streams of data, it crucial to have a programming model which identifies clearly what a stream of data is and defines composable components to deal with it.

It is obvious that the above channel api falls short when manipulating a rich set of streams of data, but even the classic InputStream/OutputStreams interfaces are not sufficient. Forgetting about their inefficient blocking runtime properties, they don't carry enough signals/information to allow building rich stream manipulation api.

[Play2](www.playframework.org) uses Iteratees together with Futures for dealing with streams of data, providing a very rich model for programming rich Realtime Web Applications.

### A sample Realtime Web App

The goal of this text isn't to provide a detailed description of what Iteratees and pals are. Nevertheless I will go into a fast introduction and then move into an example illustrating few aspects of how powerful this approach is.

- An `Iteratee[E,A]` is an immutable interface that represents a consumer, it consumes chunks of data each of type `E` and eventually produces a computed value of type `A`. `Iteratee[String,Int]` is an iteratee that consumes chunks of strings and eventually produces an `Int` (that could be for instance number of charecters in the passed chunks)

  An iteratee can choose to terminate before the `EOF` sent from the stream, or could wait for `EOF` before it terminates, returning the computed `A` value.

  You can compose different `Iteratee`s together and this can be an opportunity for partitioning consuming logic into different parts.

- An `Enumerator[E]` represents a stream that is pushing chunks of data of type `E`. An `Enumerator[String]` is a stream of strings. `Enumerator`s can be composed one after the other, or interleaved concurrently providing means of streams management.

- An `Enumeratee[From,To]` is an adapter from a stream of `From`s to a stream of `To`s. Note that an `Enumeratee` can rechunk differently, add or remove chunks or parts of them. `Enumeratee`s as we will see are instrumental for stream manipulation.

- There are convenient methods for creating different kinds of `Enumerator`s `Iteratee`s and `Enumeratee`s for different scenarios.

Our sample application features two streams, one is a stream of financial operations:

    val operations: Enumerator[Event] = Enumerator.generateM[Event] {
      Promise.timeout(
        Some(Operation( if(Random.nextBoolean) "public" else "private",
        Random.nextInt(1000))), Random.nextInt(500))
    }

Here we are generating random values at random distances (of maximum 500ms). In the real world this stream could be coming from a datastore or an open socket with another server.

An `Operation` is _private_ or _public_ and curries an _amount_ of type `Int`:

    case class Operation(level: String, amout: Int) extends Event


The other stream we have is a stream of system messages, messages that talk about the status of the system:

    case class SystemStatus(message: String) extends Event

    val noise: Enumerator[Event] = Enumerator.generateM[Event] {
      Promise.timeout(
        Some(SystemStatus("System message")),
        Random.nextInt(5000)
      )
    }

This stream can be coming from another server or datastore. With these two streams at hand, we can prooduce one single stream that contains messages of both by interleaving them:

    val events: Enumerator[Event] = operations >- noise


Actually those not comfortable using symbolic operators can use `interleave` method:

    val events: Enumerator[Event] = operations.interleave(noise)

Now the model part of our application looks like:

<script src="https://gist.github.com/3072893.js?file=Streams.scala"></script>

### Our Realtime Web App features:

Our application will publish this stream of `Event` as [Server Sent Event](http://dev.w3.org/html5/eventsource/) or [Comet](http://en.wikipedia.org/wiki/Comet_(programming)) (both are protocols for uni-directional socket from the server to the browser) and will be providing two simple features: 

#### Authorization:

You can only see `Event`s that are allowed for your role. _Managers_ can see _private_ operations and system status whereas normal _users_ can see only _public_ operations, for this purpose we create an `Enumeratee` which collects appropriate messages:

    def feed(role: String) = Action {

      val secure: Enumeratee[Event, Event] = 
        Enumeratee.collect[Event] {
          case e@SystemStatus(_) if role == "MANAGER" => e
          case e@Operation("private", _) if role == "MANAGER" => e
          case e@Operation("public", _) => e
        }

#### Filtering:

You can filter the stream by range of interest in the amount of the operation. By providing an _upper_ and _lower_ bounds you get only corresponding operations, for this we create another `Enumeratee` collecting appropriate operations:

    def feed(role: String, lowerBound: Int, higherBound: Int) = Action {
    
      val secure: Enumeratee[Event, Event] = ...

      val inBounds: Enumeratee[Event, Event] =
        Enumeratee.collect[Event] {
          case e@Operation(_, amout) 
            if amout > lowerBound && amout < higherBound => e
          case e@SystemStatus(_) => e
        }

    }

#### JSON:

Our App will be pushing JSON messages to the browser, that's why we need one more `Enumeratee` for transforming `Event`s to `JSON` values, ie, `Enumeratee[Event,JsValue]`:

    val asJson: Enumeratee[Event, JsValue] =
      Enumeratee.map[Event] { 
        case Operation(visibility, amount) =>
          toJson(Map("type" -> toJson("operation"),
                            "amount" -> toJson(amount),
                            "visibility" -> toJson(visibility)))

        case SystemStatus(msg) =>
          toJson(Map("type" -> "status", "message" -> msg))
    }

For convenience, let's produce one single adapter out of the three we got, for that we can use the `compose` method or its symbolic equivalent `><>`:

    val finalAdpater = secure ><> inBounds ><> asJson

We're almost done. Now all what we need is to respond to the browser with an `Ok` status wrapping each message into the Server Sent Event (`EventSource`) protocol. Actually there is already an `Enumeratee` which adapts a stream and wraps its chunks into the SSE protocol:

    Ok.feed(Stream.events &> finalAdpater ><> EventSource())
        .as("text/event-stream")

Here we pass our stream through the `finalAdapter` and then through the `EventSource` adapter, applying then the appropriate header to the response.

Our application now looks like:
<script src="https://gist.github.com/3072893.js?file=Application.scala"></script>

All what we need now from the client side is to connect to the stream using the following javascript:

   feed = new EventSource('/feed?role=@role&lower=' + min + '&higher=' + max)

Snapshots of our Realtime Web App:

![](http://media.tumblr.com/tumblr_m6v2w8i3Ti1qde3be.png)

![](http://media.tumblr.com/tumblr_m6v2wjdXts1qde3be.png)

### Bottom Line

Realtime Web involves dealing with different streams of data from different sources. It is hard to do any non-trivial application without having a programming model that contains an appropriate representation of a stream and necessary API for creating, adapting, filtering and consuming streams of data. Play2 uses `Iteratee`s to offer a programming model and a rich API.

*Note: [Full working application source](https://github.com/sadache/RealtimeWebAppSample) was created by me @sadache and @guillaumebort for one of our talks*
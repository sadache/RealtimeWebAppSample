package models

trait Event

case class Operation(level: String, amout: Int) extends Event
case class SystemStatus(message: String) extends Event

object Stream {
  
  import scala.util.Random
  
  import play.api.libs.iteratee._
  import play.api.libs.concurrent._
  
  val operations: Enumerator[Event] = Enumerator.generateM[Event] {
    Promise.timeout(Some(Operation( if(Random.nextBoolean) "public" else "private", Random.nextInt(1000))), Random.nextInt(500))
  }
  
  val noise: Enumerator[Event] = Enumerator.generateM[Event] {
    Promise.timeout(Some(SystemStatus("System message")), Random.nextInt(5000))
  }
  
  val events: Enumerator[Event] = operations >- noise
  
}

package cluster

import java.util.UUID

import akka.actor.SupervisorStrategy.Stop
import akka.actor.{ Actor, ActorLogging, Props, ReceiveTimeout }
import akka.cluster.sharding.ShardRegion
import akka.cluster.sharding.ShardRegion.Passivate
import cluster.CacheDataActor.Get

class CacheDataActor extends Actor with ActorLogging {

  override def receive: Receive = {
    case Get(id) => sender ! s"cached data for id: $id"
    case ReceiveTimeout =>
      log.info(s"sending Passivate to metadata parent: {${context.parent.path.name}} for ${self.path.name}")
      context.parent ! Passivate(stopMessage = Stop)
    case Stop =>
      context.stop(self)
      log.info(s"Passivating metadata actor for ${self.path.name}")
  }
}

object CacheDataActor {
  final val numOfShards = 50 // Planned num of cluster nodes
  val extractEntityId: ShardRegion.ExtractEntityId = {
    case msg@Get(id) => (id.toString, msg)
  }
  val extractShardId: ShardRegion.ExtractShardId = {
    case Get(id) => (id.hashCode() % numOfShards).toString
  }

  case class Get(id: UUID)

  def props: Props = Props(new CacheDataActor())
}

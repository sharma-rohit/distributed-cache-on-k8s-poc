package http

import akka.actor.ActorSystem
import akka.cluster.sharding.ClusterSharding
import akka.http.scaladsl.marshalling.Marshaller.StringMarshaller
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.util.Timeout
import cluster.CacheDataActor.Get
import cluster.ClusterShardRegion

import scala.concurrent.duration._
import scala.util.{ Failure, Success }

class Route(system: ActorSystem) {
  implicit val timeout: Timeout = 3.seconds
  val shardRegionActor = new ClusterShardRegion(system).clusterShardRegion

  def routes: akka.http.scaladsl.server.Route = path("health") {
    get {
      complete(StatusCodes.OK)
    }
  } ~ path("cache-data" / JavaUUID) { id =>
    get {
      onComplete((shardRegionActor ? Get(id)).mapTo[String]) {
        case Success(s) => complete(s)
        case Failure(f) => complete(f)
      }
    }
  }

}


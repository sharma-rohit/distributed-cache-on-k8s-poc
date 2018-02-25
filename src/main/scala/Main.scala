import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.{ ActorMaterializer, ActorMaterializerSettings }
import cluster.ClusterStateInformer
import com.typesafe.config.{ Config, ConfigFactory, ConfigValueFactory }
import http.Route

import scala.util.{ Failure, Success }
import scala.concurrent.ExecutionContext.Implicits.global

object Main {

  def main(args: Array[String]): Unit = {
    println("Starting containers.....")
    val config: Config = {
      import scala.collection.JavaConverters._
      val seedNodes = ClusterSetup.seedNodes()
      ConfigFactory.empty()
        .withValue("akka.cluster.seed-nodes", ConfigValueFactory.fromIterable(seedNodes.map(seedNode => s"akka.tcp://${ClusterSetup.actorSystemName()}@$seedNode").asJava))
        .withValue("akka.remote.netty.tcp.hostname", ConfigValueFactory.fromAnyRef(ClusterSetup.podName() + "." + ClusterSetup.domain()))
        .withValue("akka.remote.netty.tcp.port", ConfigValueFactory.fromAnyRef(ClusterSetup.remoteBindingPort()))
        .withFallback(ConfigFactory.load())
        .resolve()
    }

    println(config)

    implicit val system: ActorSystem = ActorSystem(ClusterSetup.actorSystemName(), config)
    implicit val mat = ActorMaterializer(materializerSettings = Some(ActorMaterializerSettings(system)))
    val routes = new Route(system)
    Http().bindAndHandle(routes.routes, "0.0.0.0", 9000).onComplete {
      case Success(s) => println("Successfully started..")
      case Failure(f) => println(f)
    }

    system.actorOf(ClusterStateInformer.props(), "cluster-informer")
  }

}

object ClusterSetup {
  def seedNodes(): Iterable[String] = sys.env.get("AKKA_SEED_NODES").map(_.split(",")).get.toIterable

  def domain(): String = sys.env.getOrElse("AKKA_REMOTING_BIND_DOMAIN", throw new RuntimeException("No domain found."))

  def podName(): String = sys.env.getOrElse("POD_NAME", throw new RuntimeException("No podname found."))

  def remoteBindingPort(): String = sys.env.getOrElse("AKKA_REMOTING_BIND_PORT", throw new RuntimeException("No port found."))

  def actorSystemName(): String = sys.env.getOrElse("AKKA_ACTOR_SYSTEM_NAME", throw new RuntimeException("No actorsystem name found."))
}

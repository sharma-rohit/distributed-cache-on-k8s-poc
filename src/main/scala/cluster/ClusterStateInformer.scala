package cluster

import akka.actor.{ Actor, ActorLogging, Props }
import akka.cluster.ClusterEvent._
import akka.cluster.{ Cluster, ClusterEvent }

class ClusterStateInformer extends Actor with ActorLogging {
  val cluster = Cluster(context.system)

  override def preStart(): Unit = {
    cluster.subscribe(
      subscriber = self,
      initialStateMode = ClusterEvent.InitialStateAsEvents,
      to = classOf[MemberEvent], classOf[UnreachableMember]
    )
  }

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive: Receive = {
    case MemberJoined(member) => log.info(s"Member ${member.address} Joined")
    case MemberUp(member) => log.info("Member is Up: {}", member.address)
    case UnreachableMember(member) => log.info("Member detected as unreachable: {}", member)
    case MemberRemoved(member, previousStatus) =>
      log.info(
        "Member is Removed: {} after {}",
        member.address, previousStatus)
    case me: MemberEvent ⇒ log.info(s"Received Member event $me for Member: ${me.member.address}")
  }
}

object ClusterStateInformer {
  def props():Props = Props(new ClusterStateInformer)
}
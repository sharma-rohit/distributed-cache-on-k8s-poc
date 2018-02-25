package cluster

import akka.actor.{ ActorRef, ActorSystem }
import akka.cluster.sharding.{ ClusterSharding, ClusterShardingSettings }

class ClusterShardRegion(actorSystem: ActorSystem) {
  val clusterShardRegion: ActorRef = ClusterSharding(actorSystem).start(
    typeName = ClusterShardRegion.SHARD_REGION_NAME,
    entityProps = CacheDataActor.props,
    settings = ClusterShardingSettings(actorSystem),
    extractEntityId = CacheDataActor.extractEntityId,
    extractShardId = CacheDataActor.extractShardId
  )
}

object ClusterShardRegion {
  val SHARD_REGION_NAME = "cache-data"
}

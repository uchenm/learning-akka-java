package com.cffex.chapter5.sharding;

import akka.actor.ActorIdentity;
import akka.actor.ActorPath;
import akka.actor.ActorPaths;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Identify;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.sharding.ClusterSharding;
import akka.cluster.sharding.ClusterShardingSettings;
import akka.cluster.singleton.ClusterSingletonManager;
import akka.cluster.singleton.ClusterSingletonManagerSettings;
import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;
import akka.pattern.Patterns;
import akka.persistence.journal.leveldb.SharedLeveldbJournal;
import akka.persistence.journal.leveldb.SharedLeveldbStore;
import akka.util.Timeout;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import scala.Option;
import scala.concurrent.Future;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ming on 2016/6/4.
 */
public class ClusterShardingApp {
    public static void main(String[] args) throws Exception {
        Arrays.asList(new String[]{"2551", "2552", "0"}).forEach(
                port -> {
                    // Override the configuration of the port
                    Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).
                            withFallback(ConfigFactory.load("sharding"));

                    // Create an Akka system
                    ActorSystem system = ActorSystem.apply("ClusterSystem", config);

                    startupSharedJournal(system, new Boolean(port == "2551"),
                            ActorPaths.fromString("akka.tcp://ClusterSystem@127.0.0.1:2551/user/store"));

                    ClusterSharding.get(system).start(
                            Counter.shardName,
                            Counter.props,
                            ClusterShardingSettings.apply(system),
                            Counter.messageExtractor);

                    if (port != "2551" && port != "2552") {
                        Cluster.get(system).registerOnMemberUp(new Runnable() {
                            public void run() {
                                system.actorOf(Frontend.props, "frontend");
                            }
                        });
                    }
                }
        );
    }

    public static void startupSharedJournal(ActorSystem system, Boolean startStore, ActorPath path) {
        // Start the shared journal one one node (don't crash this SPOF)
        // This will not be needed with a distributed journal
        if (startStore)
            system.actorOf(Props.create(SharedLeveldbStore.class), "store");
        // register the shared journal
        Timeout timeout = Timeout.apply(15, TimeUnit.SECONDS);
        Future<Object> f = Patterns.ask(system.actorSelection(path), new Identify(null), timeout);
        f.onSuccess(new OnSuccess<Object>() {
            public final void onSuccess(Object t) {
                if (t instanceof ActorIdentity) {
                    SharedLeveldbJournal.setStore(((ActorIdentity) t).getRef(), system);
                } else {
                    system.log().info("Shared journal not started at {}" + path);
                    system.terminate();
                }
            }
        }, system.dispatcher());
        f.onFailure(new OnFailure() {
            public void onFailure(Throwable failure) throws Throwable {
                system.log().error("failure", failure);
                system.log().info("Lookup of shared journal at " + path + " timed out");
                system.terminate();
            }
        }, system.dispatcher());
    }
}

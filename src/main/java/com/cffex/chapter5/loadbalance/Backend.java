package com.cffex.chapter5.loadbalance;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import com.cffex.chapter5.commons.Add;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Created by Ming on 2016/6/5.
 */
public class Backend extends UntypedActor {

    private Cluster cluster = Cluster.get(getContext().system());

    public void preStart() {
        cluster.subscribe(self(), ClusterEvent.MemberUp.class);
    }

    public void postStop() {
        cluster.unsubscribe(self());
    }

    public void onReceive(Object message) throws Exception {
        if (message instanceof Add) {
            getContext().system().log().info("I'm a backend with path: " + self() + " and I received add operation.");
        }
    }

    public static ActorRef initiate(int port) {
        Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port)
                .withFallback(ConfigFactory.load().getConfig("Backend"))
                .withFallback(ConfigFactory.load("loadbalancer"));
        ActorSystem system = ActorSystem.apply("ClusterSystem", config);
        return system.actorOf(Props.create(Backend.class), "backend");
    }
}

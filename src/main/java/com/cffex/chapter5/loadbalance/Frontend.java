package com.cffex.chapter5.loadbalance;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.routing.FromConfig;
import com.cffex.chapter5.commons.Add;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.duration.Duration;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ming on 2016/6/5.
 */
public class Frontend extends UntypedActor {

    private int upToN = 200;

    private ActorRef backendRouter = getContext().system().actorOf(new FromConfig().props(), "backendRouter");

    static ActorRef _frontend;

    public void preStart() {
        getContext().system().scheduler().schedule(Duration.create(3, TimeUnit.SECONDS), Duration.create(3, TimeUnit.SECONDS), new Runnable() {
            public void run() {
                self().tell(new Add(new Random().nextInt(), new Random().nextInt()), ActorRef.noSender());
            }
        }, getContext().system().dispatcher());
    }

    public void onReceive(Object message) throws Exception {
        if (message instanceof Add) {
            getContext().system().log().info("Frontend: I'll forward add operation to backend node to handle it.");
            backendRouter.forward(message, getContext());
        }
    }

    public static void initiate() {
        Config config = ConfigFactory.parseString("akka.cluster.roles = [frontend]").
                withFallback(ConfigFactory.load("loadbalancer"));

        ActorSystem system = ActorSystem.apply("ClusterSystem", config);
        system.log().info("Frontend will start when 2 backend members in the cluster.");
        //#registerOnUp
        Cluster.get(system).registerOnMemberUp(new Runnable() {
            public void run() {
                _frontend = system.actorOf(Props.create(Frontend.class), "frontend");
            }
        });
    }
}

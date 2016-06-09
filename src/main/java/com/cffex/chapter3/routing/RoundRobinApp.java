package com.cffex.chapter3.routing;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.FromConfig;
import akka.routing.RoundRobinPool;

/**
 * Created by Ming on 2016/5/30.
 */
public class RoundRobinApp {
    public static void main(String[] args) throws Exception {
        ActorSystem system = ActorSystem.create("Round-Robin-Router");

        ActorRef routerPool = system.actorOf(new RoundRobinPool(3).props(Props.create(Worker.class)), "round-robin-pool");

        routerPool.tell(new Work(), ActorRef.noSender());
        routerPool.tell(new Work(), ActorRef.noSender());
        routerPool.tell(new Work(), ActorRef.noSender());
        routerPool.tell(new Work(), ActorRef.noSender());

        Thread.sleep(100);

        system.actorOf(Props.create(Worker.class), "w1");
        system.actorOf(Props.create(Worker.class), "w2");
        system.actorOf(Props.create(Worker.class), "w3");


        ActorRef routerGroup = system.actorOf(new FromConfig().props(), "round-robin-group");

        routerGroup.tell(new Work(), ActorRef.noSender());

        routerGroup.tell(new Work(), ActorRef.noSender());
        routerGroup.tell(new Work(), ActorRef.noSender());
        routerGroup.tell(new Work(), ActorRef.noSender());

        Thread.sleep(100);

        system.terminate();
    }
}

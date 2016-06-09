package com.cffex.chapter3.routing;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.FromConfig;
import akka.routing.RandomGroup;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Ming on 2016/5/30.
 */
public class RandomRouteApp {
    public static void main(String[] args) throws Exception{
        ActorSystem system = ActorSystem.create("Random-Router");

        ActorRef routerPool = system.actorOf(new FromConfig().props(Props.create(Worker.class)), "random-router-pool");

        routerPool.tell(new Work(),ActorRef.noSender());
        routerPool.tell(new Work(),ActorRef.noSender());
        routerPool.tell(new Work(),ActorRef.noSender());
        routerPool.tell(new Work(),ActorRef.noSender());

        Thread.sleep(100);

        system.actorOf(Props.create(Worker.class), "w1");
        system.actorOf(Props.create(Worker.class), "w2");
        system.actorOf(Props.create(Worker.class), "w3");

        String[] array=new String[]{"/user/w1", "/user/w2", "/user/w3"};
        List<String> paths = Arrays.asList(array);

        ActorRef routerGroup = system.actorOf(new RandomGroup(paths).props(), "random-router-group");

        routerGroup.tell(new Work(),ActorRef.noSender());

        routerGroup.tell(new Work(),ActorRef.noSender());
        routerGroup.tell(new Work(),ActorRef.noSender());
        routerGroup.tell(new Work(),ActorRef.noSender());

        Thread.sleep(100);

        system.terminate();
    }
}

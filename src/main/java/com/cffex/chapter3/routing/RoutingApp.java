package com.cffex.chapter3.routing;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by Ming on 2016/5/30.
 */
public class RoutingApp {
    public static void main(String[] args) throws Exception {
        ActorSystem system = ActorSystem.create("router");

        ActorRef router = system.actorOf(Props.create(RouterPool.class));

        router.tell(new Work(), ActorRef.noSender());

        router.tell(new Work(), ActorRef.noSender());

        router.tell(new Work(), ActorRef.noSender());

        Thread.sleep(100);

        system.actorOf(Props.create(Worker.class), "w1");
        system.actorOf(Props.create(Worker.class), "w2");
        system.actorOf(Props.create(Worker.class), "w3");
        system.actorOf(Props.create(Worker.class), "w4");
        system.actorOf(Props.create(Worker.class), "w5");

        String[] workPath = new String[]{"/user/w1",
                "/user/w2",
                "/user/w3",
                "/user/w4",
                "/user/w5"};
        List<String> workers = Arrays.asList(workPath);
        ActorRef routerGroup = system.actorOf(Props.create(RouterGroup.class, workers));

        routerGroup.tell(new Work(), ActorRef.noSender());

        routerGroup.tell(new Work(), ActorRef.noSender());

        Thread.sleep(100);

        system.terminate();
    }
}

class Worker extends UntypedActor {
    public void onReceive(Object msg) {
        if (msg instanceof Work) {
            System.out.println("I received Work Message and My ActorRef:" + self());
        }
    }
}

class Work {
}

class RouterPool extends UntypedActor {

    private List<ActorRef> routees = new ArrayList<>();

    public void preStart() {
        for (int i = 0; i < 5; i++)
            routees.add(getContext().actorOf(Props.create(Worker.class)));
    }

    public void onReceive(Object msg) {
        if (msg instanceof Work) {
            routees.get(new Random().nextInt(routees.size())).forward(msg, getContext());
        }
    }
}

class RouterGroup extends UntypedActor {
    private List<String> routees;

    public RouterGroup(List<String> routees) {
        this.routees = routees;
    }

    public void onReceive(Object msg) {
        if (msg instanceof Work) {
            System.out.println("I'm a Router Group and I receive Work Message....");
            int index=new Random().nextInt(routees.size());
            getContext().actorSelection(routees.get(index)).forward(msg, getContext());
        }
    }
}
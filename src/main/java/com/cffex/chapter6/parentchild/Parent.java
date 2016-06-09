package com.cffex.chapter6.parentchild;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

/**
 * Created by Ming on 2016/6/5.
 */
public class Parent extends UntypedActor {
    final ActorRef child = context().actorOf(Props.create(Child.class), "child");
    boolean ponged = false;

    @Override public void onReceive(Object message) throws Exception {
        if ("pingit".equals(message)) {
            child.tell("ping", self());
        } else if ("pong".equals(message)) {
            ponged = true;
        } else {
            unhandled(message);
        }
    }
}

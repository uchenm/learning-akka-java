package com.cffex.chapter6.parentchild;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

/**
 * Created by Ming on 2016/6/5.
 */
public class DependentParent extends UntypedActor {
    final ActorRef child;
    boolean ponged = false;

    public DependentParent(Props childProps) {
        child = context().actorOf(childProps, "child");
    }

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
package com.cffex.chapter6.parentchild;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

/**
 * Created by Ming on 2016/6/5.
 */
public class DependentChild extends UntypedActor {
    private final ActorRef parent;

    public DependentChild(ActorRef parent) {
        this.parent = parent;
    }

    @Override public void onReceive(Object message) throws Exception {
        if ("ping".equals(message)) {
            parent.tell("pong", self());
        } else {
            unhandled(message);
        }
    }
}

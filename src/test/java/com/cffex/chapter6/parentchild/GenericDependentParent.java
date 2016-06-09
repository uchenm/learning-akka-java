package com.cffex.chapter6.parentchild;

import akka.actor.ActorRef;
import akka.actor.ActorRefFactory;
import akka.actor.UntypedActor;
import akka.japi.Function;

/**
 * Created by Ming on 2016/6/5.
 */
public class GenericDependentParent extends UntypedActor {
    final ActorRef child;
    boolean ponged = false;

    public GenericDependentParent(Function<ActorRefFactory, ActorRef> childMaker)
            throws Exception {
        child = childMaker.apply(context());
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if ("ping".equals(message)) {
            child.tell("ping", self());
        } else if ("pong".equals(message)) {
            ponged = true;
        } else {
            unhandled(message);
        }
    }
}
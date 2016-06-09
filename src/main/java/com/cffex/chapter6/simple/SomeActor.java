package com.cffex.chapter6.simple;import akka.actor.ActorRef;
import akka.actor.UntypedActor;

/**
 * Created by Ming on 2016/6/5.
 */
public class SomeActor extends UntypedActor {
    ActorRef target = null;

    public void onReceive(Object msg) {

        if (msg.equals("hello")) {
            getSender().tell("world", getSelf());
            if (target != null) target.forward(msg, getContext());

        } else if (msg instanceof ActorRef) {
            target = (ActorRef) msg;
            getSender().tell("done", getSelf());
        }
    }
}
package com.cffex.chapter6.parentchild;

import akka.actor.UntypedActor;

/**
 * Created by Ming on 2016/6/5.
 */
public class Child extends UntypedActor {
    @Override public void onReceive(Object message) throws Exception {
        if ("ping".equals(message)) {
            context().parent().tell("pong", self());
        } else {
            unhandled(message);
        }
    }
}
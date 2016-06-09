package com.cffex.chapter3.hotswap.behavior;

import akka.actor.UntypedActor;
import akka.japi.Procedure;

/**
 * Created by Ming on 2016/5/30.
 */
public class HotSwapActor extends UntypedActor {
    Procedure<Object> angry = new Procedure<Object>() {
        @Override
        public void apply(Object message) {
            if (message.equals("bar")) {
                getSender().tell("I am already angry?", getSelf());
            } else if (message.equals("foo")) {
                getContext().become(happy);
            }
        }
    };

    Procedure<Object> happy = new Procedure<Object>() {
        @Override
        public void apply(Object message) {
            if (message.equals("bar")) {
                getSender().tell("I am already happy :-)", getSelf());
            } else if (message.equals("foo")) {
                getContext().become(angry);
            }
        }
    };

    public void onReceive(Object message) {
        if (message.equals("bar")) {
            getContext().become(angry);
        } else if (message.equals("foo")) {
            getContext().become(happy);
        } else {
            unhandled(message);
        }
    }
}

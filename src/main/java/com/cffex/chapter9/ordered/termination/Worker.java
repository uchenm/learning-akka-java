package com.cffex.chapter9.ordered.termination;

import akka.actor.UntypedActor;

/**
 * Created by Ming on 2016/6/9.
 */
public class Worker extends UntypedActor {
    public void preStart() {
        System.out.println("%s is running".format(self().path().name()));
    }

    public void postStop() {
        System.out.println("%s has stopped".format(self().path().name()));
    }

    public void onReceive(Object msg) {
        System.out.println("Cool, I got a message: " + msg);
    }
}

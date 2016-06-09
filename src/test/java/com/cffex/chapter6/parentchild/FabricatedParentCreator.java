package com.cffex.chapter6.parentchild;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;
import akka.testkit.TestProbe;

/**
 * Created by Ming on 2016/6/5.
 */
public class FabricatedParentCreator implements Creator<Actor> {
    private final TestProbe proxy;

    public FabricatedParentCreator(TestProbe proxy) {
        this.proxy = proxy;
    }

    @Override
    public Actor create() throws Exception {
        return new UntypedActor() {
            final ActorRef child = context().actorOf(Props.create(Child.class), "child");

            @Override
            public void onReceive(Object x) throws Exception {
                if (sender().equals(child)) {
                    proxy.ref().forward(x, context());
                } else {
                    child.forward(x, context());
                }
            }
        };
    }
}
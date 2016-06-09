package com.cffex.chapter6.parentchild;

import akka.actor.ActorRef;
import akka.actor.ActorRefFactory;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.Function;
import akka.testkit.JavaTestKit;
import akka.testkit.TestProbe;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by Ming on 2016/6/5.
 */
public class ParentSpec {
    static ActorSystem system;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
    }

    @AfterClass
    public static void teardown() {
        JavaTestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void test() throws Exception {
        //JavaTestKid or TestProbe either works
        final JavaTestKit proxy = new JavaTestKit(system);
        //TestProbe proxy = new TestProbe(system);
        Function<ActorRefFactory, ActorRef> maker = new Function<ActorRefFactory, ActorRef>() {
            @Override
            public ActorRef apply(ActorRefFactory f) throws Exception {
                // return f.actorOf(Props.create(Child.class));
                // return proxy.ref();
                return proxy.getRef();
            }
        };

        ActorRef parent = system.actorOf(Props.create(GenericDependentParent.class, maker));
        proxy.send(parent, "ping");
        //  proxy.expectMsg("ping");
        proxy.expectMsgEquals("ping");
    }

}

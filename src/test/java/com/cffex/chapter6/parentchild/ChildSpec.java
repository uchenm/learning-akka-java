package com.cffex.chapter6.parentchild;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import akka.testkit.TestProbe;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by Ming on 2016/6/5.
 */
public class ChildSpec {
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
    public void test(){
        TestProbe proxy = new TestProbe(system);
        ActorRef parent = system.actorOf(Props.create(new FabricatedParentCreator(proxy)));

        proxy.send(parent, "ping");
        proxy.expectMsg("pong");
    }
}

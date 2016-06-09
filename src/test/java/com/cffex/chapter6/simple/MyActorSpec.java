package com.cffex.chapter6.simple;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import akka.testkit.TestActorRef;
import com.cffex.chapter6.simple.MyActor;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by Ming on 2016/6/5.
 */
public class MyActorSpec {
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
    public void demonstrateTestActorRef() {
        final Props props = Props.create(MyActor.class);
        final TestActorRef<MyActor> ref = TestActorRef.create(system, props, "testA");
        final MyActor actor = ref.underlyingActor();
        Assert.assertTrue(actor.testMe());
        try {
            ref.receive(new Exception("expected"));
            Assert.fail("expected an exception to be thrown");
        } catch (Exception e) {
            Assert.assertEquals("expected", e.getMessage());
        }
    }
}

package com.cffex.chapter6.fsm;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import akka.testkit.TestActorRef;
import akka.testkit.TestFSMRef;
import com.cffex.chapter3.fsm.UserStorageFSM;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by Ming on 2016/6/6.
 */
public class UserStorageFSMSpec {
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
//        TestFSMRef<UserStorageFSM.State,UserStorageFSM.Data,UserStorageFSM> storage = TestFSMRef.apply(new UserStorageFSM(),system);
//        storage.stequal(UserStorageFSM.State.Disconnected)
//        storage.stateData must equal(EmptyData)
    }
}

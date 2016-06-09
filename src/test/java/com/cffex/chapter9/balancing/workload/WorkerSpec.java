package com.cffex.chapter9.balancing.workload;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by Ming on 2016/6/9.
 */
public class WorkerSpec {
    static ActorSystem system;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create("balancing");
    }

    @AfterClass
    public static void teardown() {
        JavaTestKit.shutdownActorSystem(system);
        system = null;
    }

    public ActorRef getWorker(String name) {
        //String actorPath = "akka://%s/user/%s".format(system.name(), name);
        String actorPath="akka://"+system.name()+"/user/"+name;
        System.out.println(actorPath);

        return system.actorOf(Props.create(TestWorker.class, system.actorFor(actorPath).path()));
    }
    public ActorRef getBadWorker(String name) {
        //String actorPath = "akka://%s/user/%s".format(system.name(), name);
        String actorPath="akka://"+system.name()+"/user/"+name;
        System.out.println(actorPath);

        return system.actorOf(Props.create(BadTestWorker.class, system.actorFor(actorPath).path()));
    }

    @Test
    public void testIt() {
        new JavaTestKit(system) {{
            ActorRef master = system.actorOf(Props.create(Master.class),"master");
            ActorRef w1 = getWorker("master");
            ActorRef w2 = getWorker("master");
            ActorRef w3 = getWorker("master");
            master.tell("Hithere", getRef());
            master.tell("Guys", getRef());
            master.tell("So", getRef());
            master.tell("What's", getRef());
            master.tell("Up?", getRef());
            expectMsgAllOf("Hithere", "Guys", "So", "What's", "Up?");

//            new Within(duration("3 seconds")) {
//
//                protected void run() {
//
//                }
//            };

        }};
    }

    @Test
    public void testFailure() {
        new JavaTestKit(system) {{
            ActorRef master = system.actorOf(Props.create(Master.class),"master2");
            ActorRef w1 = getWorker("master2");
            ActorRef w2 = getBadWorker("master2");
            master.tell("Hithere", getRef());
            master.tell("Guys", getRef());
            master.tell("So", getRef());
            master.tell("What's", getRef());
            master.tell("Up?", getRef());
            expectMsgAllOf("Hithere", "Guys", "So", "What's", "Up?");

        }};
    }
}

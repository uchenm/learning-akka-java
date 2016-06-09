package com.cffex.chapter9.scheduling;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

/**
 * Created by Ming on 2016/6/9.
 */
public class SchedulingMessages {
    public static void main(String[] args) throws Exception {
        ActorSystem system = ActorSystem.create("Scheduling-Messages");

        ActorRef scheduler1 = system.actorOf(Props.create(ScheduleInConstructor.class), "schedule-in-constructor");


        ActorRef scheduler = system.actorOf(Props.create(ScheduleInReceive.class), "schedule-in-receive");
        Thread.sleep(5000);
        system.shutdown();
    }
}

package com.cffex.chapter9.throttling;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.Option;

import java.util.concurrent.TimeUnit;

/**
 * Created by Ming on 2016/6/9.
 */
public class ThottlerApp {
    public static void main(String[] args) throws Exception {
        ActorSystem system = ActorSystem.create("Thottler-Messages");

        ActorRef target = system.actorOf(Props.create(Target.class), "target");

        TimerBasedThrottler.Rate rate = new TimerBasedThrottler.RateInt(2).msgsPer(1, TimeUnit.SECONDS);

        ActorRef throttler = system.actorOf(Props.create(TimerBasedThrottler.class, rate));

        throttler.tell(new TimerBasedThrottler.SetTarget(Option.some(target)), ActorRef.noSender());
        ;

        throttler.tell("1", ActorRef.noSender());
        throttler.tell("2", ActorRef.noSender());
        throttler.tell("3", ActorRef.noSender());
        throttler.tell("4", ActorRef.noSender());
        throttler.tell("5", ActorRef.noSender());

        Thread.sleep(5000);
        system.shutdown();
    }
}

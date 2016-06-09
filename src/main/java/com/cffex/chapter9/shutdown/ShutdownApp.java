package com.cffex.chapter9.shutdown;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;

/**
 * Created by Ming on 2016/6/9.
 */
public class ShutdownApp {
    public static void main(String[] args){
        ActorSystem system = ActorSystem.create("shutdown");

        ActorRef reaper = system.actorOf(Props.create(Reaper.class), Reaper.name);

        ActorRef target = system.actorOf(Props.create(Target.class), "target");

        target.tell("Hello World",ActorRef.noSender());

        target.tell(PoisonPill.getInstance(),ActorRef.noSender());
    }
}

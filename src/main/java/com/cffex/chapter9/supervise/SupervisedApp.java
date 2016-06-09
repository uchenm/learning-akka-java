package com.cffex.chapter9.supervise;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.util.Timeout;
import scala.concurrent.duration.Duration;

import java.util.Date;
import java.util.concurrent.TimeUnit;


/**
 * Created by Ming on 2016/6/9.
 */
public class SupervisedApp {
    public static void main(String[] args) throws Exception{
        ActorSystem system = ActorSystem.create("SupervisedApp");

        ActorRef target = system.actorOf(Props.create(Target.class), "target");

        SupervisedAskSpec.execute(Target.class,"hello", Timeout.apply(3,TimeUnit.SECONDS),system);
//        target.tell("Hello World",ActorRef.noSender());

//        target.tell(PoisonPill.getInstance(),ActorRef.noSender());
    }
}

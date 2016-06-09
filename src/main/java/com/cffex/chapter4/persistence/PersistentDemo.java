package com.cffex.chapter4.persistence;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Procedure;
import akka.persistence.RecoveryCompleted;
import akka.persistence.SaveSnapshotFailure;
import akka.persistence.SaveSnapshotSuccess;
import akka.persistence.SnapshotOffer;
import akka.persistence.UntypedPersistentActor;

import java.io.Serializable;

/**
 * Created by Ming on 2016/5/31.
 */

public class PersistentDemo{
    public static void main(String[] args) throws Exception {
        ActorSystem system = ActorSystem.apply("persistent-actors");

        ActorRef counter = system.actorOf(Props.create(Counter.class),"counter");
//        Thread.sleep(20000);

        counter.tell(new Counter.Cmd(new Counter.Increment(3)), ActorRef.noSender());
        counter.tell(new Counter.Cmd(new Counter.Increment(5)), ActorRef.noSender());
        counter.tell(new Counter.Cmd(new Counter.Increment(5)), ActorRef.noSender());
        counter.tell(new Counter.Cmd(new Counter.Increment(5)), ActorRef.noSender());
        counter.tell(new Counter.Cmd(new Counter.Increment(5)), ActorRef.noSender());
        counter.tell(new Counter.Cmd(new Counter.Increment(5)), ActorRef.noSender());
        counter.tell(new Counter.Cmd(new Counter.Increment(5)), ActorRef.noSender());
        counter.tell(new Counter.Cmd(new Counter.Decrement(3)), ActorRef.noSender());

        counter.tell("print", ActorRef.noSender());

        Thread.sleep(10000);

        system.terminate();
    }
}








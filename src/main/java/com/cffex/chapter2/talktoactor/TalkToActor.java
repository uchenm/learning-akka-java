package com.cffex.chapter2.talktoactor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

/**
 * Created by Ming on 2016/5/29.
 */
public class TalkToActor {
    public static void main(String[] args) throws Exception{
        // Create the 'talk-to-actor' actor system
        ActorSystem system = ActorSystem.apply("talk-to-actor");

        // Create the 'checker' actor
        ActorRef checker = system.actorOf(Props.create(Checker.class), "checker");

        // Create the 'storage' actor
        ActorRef storage = system.actorOf(Props.create(Storage.class), "storage");

        // Create the 'recorder' actor
        ActorRef recorder = system.actorOf(Props.create(Recorder.class, checker, storage), "recorder");

        //send NewUser Message to Recorder
        recorder.tell(new Recorder.NewUser(new User("Jon", "jon@packt.com")), ActorRef.noSender());

        Thread.sleep(1000);
        //shutdown system
        system.terminate();
    }
}















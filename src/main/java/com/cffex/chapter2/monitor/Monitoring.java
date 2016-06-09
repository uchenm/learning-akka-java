package com.cffex.chapter2.monitor;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * Created by Ming on 2016/5/30.
 */
public class Monitoring {
    public static void main(String[] args) {
        // Create the 'monitoring' actor system
        ActorSystem system = ActorSystem.apply("monitoring");

        ActorRef athena = system.actorOf(Props.create(Athena.class), "athena");

        ActorRef ares = system.actorOf(Props.create(Ares.class, athena), "ares");

        athena.tell("Hi",ActorRef.noSender());

        system.terminate();
    }
}

class Athena extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    public void onReceive(Object message) {
        log.debug("Athena received message:{}" , message);
        getContext().stop(self());
    }
}


class Ares extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private ActorRef athena;

    public Ares(ActorRef athena) {
        this.athena = athena;
    }

    public void preStart() {
        log.debug("Ares preStart...");
        getContext().watch(athena);
    }

    public void postStop() {
        log.debug("Ares postStop...");
    }

    public void onReceive(Object message) {
        if(message instanceof Terminated){
            getContext().stop(self());
        }
    }
}
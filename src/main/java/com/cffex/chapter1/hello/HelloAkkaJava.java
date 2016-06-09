package com.cffex.chapter1.hello;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.impl.Stages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Ming on 2016/5/28.
 */

class Greeter extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public static class WhoToGreet {
        public final String who;
        public WhoToGreet(String who){
            this.who=who;
        }
    }

    @Override
    public void onReceive(Object message) {
        if(message instanceof WhoToGreet){
            String who=((WhoToGreet)message).who;
            log.info("hello {} !",who);
        }
    }
}

public  class HelloAkkaJava{
    private static final Logger logger = LoggerFactory.getLogger(HelloAkkaJava.class);
    public static void main(String[] args){
        //create akka 'hello-akka' actor system
        ActorSystem system=ActorSystem.apply("hello-akka");
        //create the greet actor
        Props props = Props.create(Greeter.class);
        ActorRef greeterActor=system.actorOf(props,"greeter");

        logger.debug("this is a log test");
        greeterActor.tell(new Greeter.WhoToGreet("John"),greeterActor);
    }
}
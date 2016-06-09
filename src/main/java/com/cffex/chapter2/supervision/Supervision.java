package com.cffex.chapter2.supervision;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Function;
import scala.Option;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

/**
 * Created by Ming on 2016/5/30.
 */
public class Supervision {
    public static void main(String[] args) throws Exception {
// Create the 'supervision' actor system
        ActorSystem system = ActorSystem.apply("supervision");

        // Create Hera Actor
        ActorRef hera = system.actorOf(Props.create(Hera.class), "hera");

//        hera.tell("Resume", ActorRef.noSender());
//        Thread.sleep(1000);
//        System.out.println();

        hera.tell("Restart", ActorRef.noSender());
        Thread.sleep(1000);
        System.out.println();

//        hera.tell("Stop", ActorRef.noSender());
//        Thread.sleep(1000);
//        System.out.println();

        system.terminate();

    }
}


class Aphrodite extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    public void preStart() {
        log.debug("Aphrodite preStart hook....");
    }

    public void preRestart(Throwable reason, Option<Object> message) throws Exception {
        log.debug("Aphrodite preRestart hook...");
        super.preRestart(reason, message);
    }

    public void postRestart(Throwable reason) throws Exception {
        log.debug("Aphrodite postRestart hook...");
        super.postRestart(reason);
    }

    public void postStop() {
        log.debug("Aphrodite postStop...");
    }


    public void onReceive(Object message) throws Exception {
        if ("Resume".equals(message)) throw new ResumeException();
        else if ("Stop".equals(message)) throw new StopException();
        else if ("Restart".equals(message)) throw new RestartException();
        else throw new Exception();
    }
}

class ResumeException extends Exception {
}

class StopException extends Exception {
}

class RestartException extends Exception {
}

class Hera extends UntypedActor {

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private ActorRef childRef;

    public SupervisorStrategy supervisorStrategy() {
        return supervisorStrategy;
    }

    private static SupervisorStrategy supervisorStrategy =
            new OneForOneStrategy(10, Duration.create(5, TimeUnit.SECONDS),
                    new Function<Throwable, SupervisorStrategy.Directive>() {
                        public SupervisorStrategy.Directive apply(Throwable t) {
                            if (t instanceof ResumeException) return SupervisorStrategy.resume();
                            else if (t instanceof RestartException) return SupervisorStrategy.restart();
                            else if (t instanceof StopException) return SupervisorStrategy.stop();
                            else return SupervisorStrategy.escalate();
                        }
                    });

    public void preStart() throws Exception {
        // Create Aphrodite Actor
        childRef = getContext().actorOf(Props.create(Aphrodite.class), "Aphrodite");
        Thread.sleep(100);
    }

    public void onReceive(Object msg) throws Exception {
        log.debug("Hera received "+msg);
        childRef.tell(msg, self());
        Thread.sleep(100);
    }
}


package com.cffex.chapter3.actor.path;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Ming on 2016/5/30.
 */
public class ActorPathDemo {
    private static final Logger logger = LoggerFactory.getLogger(ActorPathDemo.class);
    public static void main(String[] args) throws Exception{

        ActorSystem system = ActorSystem.apply("Actor-path");
        ActorRef watcher = system.actorOf(Props.create(Watcher.class), "watcher");
        ActorRef counter1 = system.actorOf(Props.create(Counter.class), "counter");
        ActorSelection counterSelection1=system.actorSelection("counter");
        logger.debug("Actor Reference of counter1 is {}",counter1);
        logger.debug("Actor selection of counter1 is {}",counterSelection1);
        counter1.tell(PoisonPill.getInstance(),ActorRef.noSender());
        Thread.sleep(1000);
        ActorRef counter2 = system.actorOf(Props.create(Counter.class), "counter");
        ActorSelection counterSelection2=system.actorSelection("counter");
        logger.debug("Actor Reference of counter2 is {}",counter2);
        logger.debug("Actor selection of counter2 is {}",counterSelection2);
        counter2.tell(PoisonPill.getInstance(),ActorRef.noSender());

        //watch
        system.terminate();
    }
}

class Counter extends UntypedActor {

    final class Inc {
        public final int num;
        public Inc(int num) {
            this.num = num;
        }
    }

    final class Dec {
        public final int num;
        public Dec(int num) {
            this.num = num;
        }
    }

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private int count = 0;

    public void onReceive(Object message) {
        if (message instanceof Inc) {
            count += ((Inc) message).num;
        } else if (message instanceof Dec) {
            count -= ((Dec) message).num;
        }
    }
}

class Watcher extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private ActorRef counterRef;

    private ActorSelection selection = getContext().actorSelection("/user/counter");

    //
    public Watcher(){
        selection.tell(Identify.apply(null),self());
    }

    public void onReceive(Object msg) {
        if (msg instanceof ActorIdentity) {
            ActorRef actorRef = ((ActorIdentity) msg).getRef();
            if (actorRef != null) {
                log.debug("Actor Reference for counter is {}",actorRef);
            } else {
                log.debug("Actor selection for actor doesn't live :( ");
            }
        }
    }
}
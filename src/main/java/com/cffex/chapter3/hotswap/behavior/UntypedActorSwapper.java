package com.cffex.chapter3.hotswap.behavior;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Procedure;

/**
 * Created by Ming on 2016/5/30.
 */
public class UntypedActorSwapper {
    public static class Swap {
        public static Swap SWAP = new Swap();
        private Swap() {
        }
    }
    public static class Swapper extends UntypedActor {
        LoggingAdapter log = Logging.getLogger(getContext().system(), this);
        public void onReceive(Object message) {
            if (message == Swap.SWAP) {
                log.info("Hi");
                getContext().become(new Procedure<Object>() {
                    @Override
                    public void apply(Object message) {
                        log.info("Ho");
                        getContext().unbecome(); // resets the latest 'become'
                    }
                }, false); // this signals stacking of the new behavior
            } else {
                unhandled(message);
            }
        }
    }

    public static void main(String... args) {
        ActorSystem system = ActorSystem.create("MySystem");
        ActorRef swap = system.actorOf(Props.create(Swapper.class));
        swap.tell(Swap.SWAP, ActorRef.noSender()); // logs Hi
        swap.tell(Swap.SWAP, ActorRef.noSender()); // logs Ho
        swap.tell(Swap.SWAP, ActorRef.noSender()); // logs Hi
        swap.tell(Swap.SWAP, ActorRef.noSender()); // logs Ho
        swap.tell(Swap.SWAP, ActorRef.noSender()); // logs Hi
        swap.tell(Swap.SWAP, ActorRef.noSender()); // logs Ho
    }
}

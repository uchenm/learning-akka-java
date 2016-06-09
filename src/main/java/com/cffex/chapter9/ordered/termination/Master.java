package com.cffex.chapter9.ordered.termination;

import akka.actor.UntypedActor;
import akka.actor.dungeon.Children;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Procedure;

/**
 * Created by Ming on 2016/6/9.
 */
public class Master extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private final Procedure<Object> waiting = new Procedure<Object>() {
        public void apply(Object msg) {
            if (msg instanceof Children) {
                log.info("Work is complete.  Result {}.");
            }
        }
    };

    public void onReceive(Object msg){

    }
}

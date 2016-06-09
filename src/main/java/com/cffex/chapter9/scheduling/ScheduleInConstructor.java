package com.cffex.chapter9.scheduling;

import akka.actor.Cancellable;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import scala.concurrent.duration.Duration;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ming on 2016/6/9.
 */
public class ScheduleInConstructor extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private final Cancellable tick = getContext().system().scheduler().schedule(
            Duration.create(500, TimeUnit.MILLISECONDS),
            Duration.create(1, TimeUnit.SECONDS),
            getSelf(), "tick", getContext().dispatcher(), null);

    @Override
    public void postStop() {
        tick.cancel();
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message.equals("tick")) {
            // do something useful here
            log.info("Cool! I got tick message at {}",new Date().toString());
        }
        else {
            unhandled(message);
        }
    }
}
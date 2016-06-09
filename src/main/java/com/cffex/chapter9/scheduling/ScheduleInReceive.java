package com.cffex.chapter9.scheduling;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import scala.concurrent.duration.Duration;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ming on 2016/6/9.
 */
public class ScheduleInReceive extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    @Override
    public void preStart() {
        getContext().system().scheduler().scheduleOnce(
                Duration.create(500, TimeUnit.MILLISECONDS),
                getSelf(), "tick", getContext().dispatcher(), null);
    }

    // override postRestart so we don't call preStart and schedule a new message
    @Override
    public void postRestart(Throwable reason) {
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message.equals("tick")) {
            // send another periodic tick after the specified delay
            getContext().system().scheduler().scheduleOnce(
                    Duration.create(1, TimeUnit.SECONDS),
                    getSelf(), "tick", getContext().dispatcher(), null);
            // do something useful here
            log.info("Cool! I got tick message at {}",new Date().toString());
        }
        else {
            unhandled(message);
        }
    }
}

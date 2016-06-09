package com.cffex.chapter9.throttling;

import akka.actor.AbstractFSM;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Option;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ming on 2016/6/9.
 */
public class TimerBasedThrottler extends AbstractFSM<TimerBasedThrottler.State, TimerBasedThrottler.Data> {
    LoggingAdapter log = Logging.getLogger(context().system(), this);
    /**
     * =======================================================#
     * case class messages
     * =======================================================#
     */
    public static class Rate {
        public final int numberOfCalls;
        public final FiniteDuration duration;

        public Rate(int numberOfCalls, FiniteDuration duration) {
            this.numberOfCalls = numberOfCalls;
            this.duration = duration;
        }

        public Long durationInMillis() {
            return duration.toMillis();
        }
    }

    public static class SetTarget {
        public final Option<ActorRef> target;

        public SetTarget(Option<ActorRef> target) {
            this.target = target;
        }

        public SetTarget(ActorRef target) {
            this(Option.some(target));
        }
    }

    public static class SetRate {
        public final Rate rate;

        public SetRate(Rate rate) {
            this.rate = rate;
        }
    }

    public static class RateInt {
        public final int numberOfCalls;

        public RateInt(int numberOfCalls) {
            this.numberOfCalls = numberOfCalls;
        }

        public Rate msgsPer(int duration, TimeUnit timeUnit) {
            return new Rate(numberOfCalls, Duration.create(duration, timeUnit));
        }

        public Rate msgsPer(FiniteDuration duration) {
            return new Rate(numberOfCalls, duration);
        }

        public Rate msgsPerSecond() {
            return new Rate(numberOfCalls, Duration.create(1, TimeUnit.SECONDS));
        }

        public Rate msgsPerMinute() {
            return new Rate(numberOfCalls, Duration.create(1, TimeUnit.MINUTES));
        }

        public Rate msgsPerHour() {
            return new Rate(numberOfCalls, Duration.create(1, TimeUnit.HOURS));
        }
    }

    public static class Tick {
    }

    public static enum State {
        Idle, Active
    }

    public static class Message {
        public final ActorRef sender;
        public final Object message;

        public Message(Object message, ActorRef sender) {
            this.message = message;
            this.sender = sender;
        }
    }

    public static class Data {
        public final Option<ActorRef> target;
        public final int callsLeftInThisPeriod;
        public final Queue<Message> queue;

        public Data(Option<ActorRef> target,
                    int callsLeftInThisPeriod,
                    Queue<Message> queue) {
            this.target = target;
            this.callsLeftInThisPeriod = callsLeftInThisPeriod;
            this.queue = queue;
        }
    }

    /**
     * =======================================================#
     * Finite state machine
     * =======================================================#
     */

    private Rate rate;

    public TimerBasedThrottler(Rate rate) {
        this.rate = rate;
    }

    {
        startWith(State.Idle, new Data(Option.none(), 0, new ConcurrentLinkedQueue<>()));

        when(State.Idle,
                matchEvent(SetRate.class, Data.class, (e, d) -> {
                    log.debug("Idle:== receive SetRate event:{},with data:{}",e,d);

                    // Set the rate
                    this.rate = ((SetRate) e).rate;
                    Data data = (Data) d;

                    return stay().using(new Data(data.target, rate.numberOfCalls, data.queue));
                }).event(SetTarget.class, TimerBasedThrottler.Data.class, (e, d) -> {
                    // Set the target
                    log.debug("Idle:==receive SetTarget event:{},with data:{}",e,d);
                    Data data = new Data(e.target, d.callsLeftInThisPeriod, d.queue);
                    if (!d.queue.isEmpty()) {
                        return goTo(State.Active).using(deliverMessages(data));
                    }
                    return stay().using(data);
                }).event(Object.class, Data.class, (e, d) -> {
                    // Queuing
                    log.debug("Idle:==receive event:{},with data:{}",e,d);
                    Message msg = new Message(e, this.context().sender());
                    d.queue.add(msg);
                    if (d.target.isEmpty()) {
                        return stay().using(new Data(d.target, d.callsLeftInThisPeriod, d.queue));
                    } else {
                        return goTo(State.Active).using(deliverMessages(new Data(d.target, d.callsLeftInThisPeriod, d.queue)));
                    }
                })
        );

        when(State.Active,
                // Set the rate
                matchEvent(SetRate.class, Data.class, (e, d) -> {
                    log.debug("Active:==receive SetRate event:{},with data:{}",e,d);
                    Rate rate = ((SetRate) e).rate;
                    stopTimer();
                    startTimer(rate);
                    Data old = ((Data) d);
                    Data data = new Data(old.target, rate.numberOfCalls, old.queue);
                    return stay().using(data);
                }).event(SetTarget.class, Data.class, (e, d) -> {
                    log.debug("Active:==receive SetTarget event:{},with data:{}",e,d);
                    Data data = new Data(e.target, d.callsLeftInThisPeriod, d.queue);
                    return stay().using(data);
                }).event(Tick.class, Data.class, (e, d) -> {
                    log.debug("Active:==receive Tick event:{},with data:{}",e,d);
                    Data data = new Data(d.target, rate.numberOfCalls, d.queue);
//                    return goTo(State.Idle).using(d);
                    return stay().using(deliverMessages(data));
                }).event(Object.class, Data.class, (e, d) -> {
                    log.debug("Active:==receive Object event:{},with data:{}",e,d);
                    Message msg = new Message(e, this.context().sender());
                    d.queue.add(msg);

                    Data data = new Data(d.target, d.callsLeftInThisPeriod, d.queue);
                    log.debug("{},{},{}",d.target, d.callsLeftInThisPeriod,d.queue.size());
                    return stay().using(deliverMessages(data));
                })

        );

        onTransition(
                matchState(State.Idle, State.Active, () -> {
                    // reuse this matcher
                    startTimer(rate);
                }).state(State.Active, State.Idle, () -> {
                    stopTimer();
                })
        );

        initialize();
    }

    private void startTimer(Rate rate) {
        log.debug("timer started");
        log.debug("rate duration:{},rate.numberOfCalls:{}",rate.duration,rate.numberOfCalls);
        setTimer("morePermits", new Tick(), rate.duration, true);
    }

    private void stopTimer() {
        log.debug("timer stopped");
        cancelTimer("morePermits");
    }

    private Data deliverMessages(Data data) {
        Queue queue = data.queue;
        int nrOfMsgToSend = Math.min(queue.size(), data.callsLeftInThisPeriod);

//        queue.take(nrOfMsgToSend).foreach(x ⇒ data.target.get.tell(x.message, x.sender));
        for (int i = 0; i < nrOfMsgToSend; i++) {
            TimerBasedThrottler.Message message = (TimerBasedThrottler.Message) queue.poll();
            data.target.get().tell(message.message, message.sender);
        }

        return new Data(data.target, data.callsLeftInThisPeriod - nrOfMsgToSend, data.queue);
//        data.copy(queue = queue.drop(nrOfMsgToSend), callsLeftInThisPeriod = data.callsLeftInThisPeriod - nrOfMsgToSend)
    }

}

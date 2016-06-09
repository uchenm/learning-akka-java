package com.cffex.chapter5.sharding;

import akka.actor.Props;
import akka.cluster.sharding.ShardRegion;
import akka.japi.Function;
import akka.japi.Procedure;
import akka.persistence.UntypedPersistentActor;
import scala.concurrent.duration.Duration;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ming on 2016/6/4.
 */
public class Counter extends UntypedPersistentActor {
    /**
     * =========================================#
     * object classes
     * =========================================#
     */
    public static enum CounterOp {
        Increment, Decrement, Get, Stop
    }

    public static class CounterChanged implements Serializable{
        final public int delta;

        public CounterChanged(int delta) {
            this.delta = delta;
        }
    }

    public static class CounterMessage implements Serializable {
        final public Long id;
        final public CounterOp op;
        public CounterMessage(Long id,CounterOp op) {
            this.id=id;
            this.op=op;
        }
    }

    /**
     * =======================================#
     * instance variables
     * =======================================#
     */
    public static Props props = Props.create(Counter.class);
    public static String shardName="Counter";
    public static int numberOfShards=12;

    public static ShardRegion.MessageExtractor messageExtractor = new ShardRegion.MessageExtractor() {

        public String entityId(Object message) {
            if (message instanceof Counter.CounterMessage)
                return ((CounterMessage) message).id.toString();
            else
                return null;
        }

        public Object entityMessage(Object message) {
            if (message instanceof Counter.CounterMessage)
                return ((CounterMessage) message).op;
            else
                return message;
        }

        public String shardId(Object message) {
            int numberOfShards = 100;
            if (message instanceof Counter.CounterMessage) {
                return String.valueOf(((CounterMessage) message).id % numberOfShards);
            } else {
                return null;
            }
        }

    };

    int count = 0;

    /**
     * =======================================#
     * instance methods
     * =======================================#
     */
    public void preStart() throws Exception {
        super.preStart();
        context().setReceiveTimeout(Duration.create(120, TimeUnit.SECONDS));
    }

    public String persistenceId() {
        return getSelf().path().parent().name() + "-" + getSelf().path().name();
    }

    public void onReceiveRecover(Object msg) {
        if (msg instanceof CounterChanged)
            updateState((CounterChanged) msg);
        else
            unhandled(msg);
    }

    void updateState(CounterChanged event) {
        count += event.delta;
    }

    public void onReceiveCommand(Object msg) {
        if (msg == CounterOp.Get) {
            getContext().system().log().info("Counter with path: ${} recevied Get Command", self());
            getContext().system().log().info("Count = ${count}", count);
            getSender().tell(count, getSelf());
        } else if (msg == CounterOp.Increment) {
            getContext().system().log().info("Counter with path: ${} recevied Increment Command", self());
            persist(new CounterChanged(+1), new Procedure<CounterChanged>() {
                public void apply(CounterChanged evt) {
                    updateState(evt);
                }
            });
        } else if (msg == CounterOp.Decrement) {
            getContext().system().log().info("Counter with path: ${} recevied Decrement Command", self());
            persist(new CounterChanged(-1), new Procedure<CounterChanged>() {
                public void apply(CounterChanged evt) {
                    updateState(evt);
                }
            });
        } else if (msg == CounterOp.Stop) {
            getContext().stop(self());
        }

//        else if (msg.equals(ReceiveTimeout.getInstance()))
//            getContext().parent().tell(
//                    new ShardRegion.Passivate(PoisonPill.getInstance()), getSelf());
        else
            unhandled(msg);
    }

}

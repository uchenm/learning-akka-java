package com.cffex.chapter5.sharding;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.cluster.sharding.ClusterSharding;
import scala.concurrent.duration.Duration;

import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ming on 2016/6/4.
 */
public class Frontend extends UntypedActor {

    /**
     * ==============================================#
     * companion message object class
     * ==============================================#
     */
    public static enum Op {
        Inc, Dec, Get
    }

    public static class Tick implements Serializable{
        final public Op op;

        public Tick(Op op) {
            this.op = op;
        }
    }

    public static Props props = Props.create(Frontend.class);

    /**
     * ==============================================#
     * instance variables
     * ==============================================#
     */

    ActorRef counterRegion = ClusterSharding.get(getContext().system()).shardRegion(Counter.shardName);

    public void preStart() throws Exception {
        super.preStart();

        // Every 3 second will send increment operation
        getContext().system().scheduler().schedule(Duration.create(3, TimeUnit.SECONDS), Duration.create(3, TimeUnit.SECONDS), new Runnable() {
            public void run() {
                self().tell(new Tick(Op.Inc), ActorRef.noSender());
            }
        }, getContext().system().dispatcher());

        // Every 12 second will send Decrement operation
        getContext().system().scheduler().schedule(Duration.create(6, TimeUnit.SECONDS), Duration.create(6, TimeUnit.SECONDS), new Runnable() {
            public void run() {
                self().tell(new Tick(Op.Dec), ActorRef.noSender());
            }
        }, getContext().system().dispatcher());

        // Every 30 second will send get operation
        getContext().system().scheduler().schedule(Duration.create(10, TimeUnit.SECONDS), Duration.create(10, TimeUnit.SECONDS), new Runnable() {
            public void run() {
                self().tell(new Tick(Op.Get), ActorRef.noSender());
            }
        }, getContext().system().dispatcher());

    }

    public void onReceive(Object msg) {
        if (msg instanceof Tick) {
            if (((Tick) msg).op == Op.Inc) {
                getContext().system().log().info("Frontend: Send Increment message to shard region.");
                counterRegion .tell(new Counter.CounterMessage(getId(), Counter.CounterOp.Increment),self());
            } else if (((Tick) msg).op == Op.Dec) {
                getContext().system().log().info("Frontend: Send Decrement message to shard region.");
                counterRegion .tell(new Counter.CounterMessage(getId(), Counter.CounterOp.Decrement),self());
            } else if (((Tick) msg).op == Op.Get) {
                getContext().system().log().info("Frontend: Send Get message to shard region.");
                counterRegion .tell(new Counter.CounterMessage(getId(), Counter.CounterOp.Get),self());
            }
        }
    }

    public Long getId(){
       return new Random(4L).nextLong();
    }
}

package com.cffex.chapter9.ordered.termination;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Procedure;
import akka.pattern.GracefulStopSupport;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ming on 2016/6/9.
 */
public class Terminator extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    //case classes messages
    public static class GetChildren{
        public final ActorRef forActor;
        public GetChildren( ActorRef forActor){
            this.forActor=forActor;
        }
    }
    public static class Children{
        public final Iterable<ActorRef> kids;
        public Children(Iterable<ActorRef> kids){
            this.kids=kids;
        }
    }

    public class AllDead{}

    /**
     * ==================================================
     */

    private Props childProps;
    private int numOfChildren;
    public Terminator(Props childProps, int numOfChildren){
        this.childProps=childProps;
        this.numOfChildren=numOfChildren;
    }

    public void preStart(){
        for (int i = 0; i < numOfChildren; i++) {
            getContext().actorOf(childProps,"worker"+i);
        }
    }

    private final Procedure<Object> waiting = new Procedure<Object>() {
        public void apply(Object msg) {
            if (msg instanceof GetChildren) {
                ActorRef forActor=((GetChildren) msg).forActor;
                getContext().watch(forActor);
                forActor.tell(getContext().getChildren(),self());
                log.info("Work is complete.  Result {}.");
                getContext().become(childrenGiven);
            }
        }
    };
    private final Procedure<Object> childrenGiven = new Procedure<Object>() {
        public void apply(Object msg) {
            if (msg instanceof GetChildren) {
                if(getSender()==((GetChildren) msg).forActor){
                    ((GetChildren) msg).forActor.tell(new Children(getContext().getChildren()),self());
                }
            }else if(msg instanceof Terminated){

            }
        }
    };

//    private Future killKids(List<ActorRef> kids){
//        if (kids.isEmpty()) new AllDead();
//        else{
//            .gracefulStop(kids, Duration.create(5, TimeUnit.SECONDS));
//        }
//    }

    public void onReceive(Object msg){

    }
}

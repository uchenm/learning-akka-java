package com.cffex.chapter9.balancing.workload;

import akka.actor.ActorPath;
import akka.actor.ActorRef;
import akka.japi.function.Procedure;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Future;

import java.util.concurrent.TimeUnit;

/**
 * Created by Ming on 2016/6/8.
 */
public class TestWorker extends Worker {
    public TestWorker(ActorPath masterLocation){
        super(masterLocation);
    }

    public void doWork(ActorRef workSender, Object work){
        Future future=Patterns.ask(workSender,work, new Timeout(1, TimeUnit.SECONDS));
//        future.onComplete(new Procedure<Object>() {
//            public void apply(Object msg){
//                //return new Worker.WorkComplete("done");
//            }
//        },getContext().dispatcher());
//        Patterns.pipe(future,getContext().dispatcher()).to(self());
        
//        future.onSuccess(new Procedure<Object>() {
//            public void apply(Object o) {
//                self().tell(new Worker.WorkComplete("done"),self());
//            }
//        },getContext().dispatcher());

        self().tell(new Worker.WorkComplete("done"),self());

//        Future {
//            workSender ! msg
//            WorkComplete("done")
//        } pipeTo self
    }
}

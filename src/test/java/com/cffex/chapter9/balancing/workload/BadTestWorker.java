package com.cffex.chapter9.balancing.workload;

import akka.actor.ActorPath;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;

/**
 * Created by Ming on 2016/6/9.
 */
public class BadTestWorker extends Worker {
    public BadTestWorker(ActorPath masterLocation){
        super(masterLocation);
    }

    public void doWork(ActorRef workSender, Object work){
        getContext().stop(self());
    }
}

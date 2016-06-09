package com.cffex.chapter9.balancing.workload;

import akka.actor.ActorRef;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Option;
import scala.Tuple2;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by Ming on 2016/6/8.
 */
public class Master extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    // Holds known workers and what they may be working on
    Map<ActorRef, Option<Tuple2<ActorRef, Object>>> workers = new HashMap<ActorRef, Option<Tuple2<ActorRef, Object>>>();
    // Holds the incoming list of work to be done as well
    // as the memory of who asked for it
    Queue<Tuple2<ActorRef, Object>> workQ = new LinkedBlockingDeque<Tuple2<ActorRef, Object>>();


    // Notifies workers that there's work available, provided they're
    // not already working on something
    private void notifyWorkers() {
        if (!workQ.isEmpty()) {
            workers.forEach((k, v) -> {
                if (v.isEmpty()) {
                    k.tell(new MasterWorkerProtocol.WorkIsReady(), self());
                }
            });
        }
    }

    public void onReceive(Object msg) {
        // Worker is alive. Add him to the list, watch him for
        // death, and let him know if there's work to be done
        if (msg instanceof MasterWorkerProtocol.WorkerCreated) {
            ActorRef worker = ((MasterWorkerProtocol.WorkerCreated) msg).worker;
            log.info("Worker created: {}", worker);
            getContext().watch(((MasterWorkerProtocol.WorkerCreated) msg).worker);

//            Tuple2<ActorRef, Object> tuple = new Tuple2<ActorRef, Object>(worker, null);
            workers.put(worker, Option.none());
            notifyWorkers();
        }

        // A worker wants more work.  If we know about him, he's not
        // currently doing anything, and we've got something to do,
        // give it to him.
        else if(msg instanceof MasterWorkerProtocol.WorkerRequestsWork){
            ActorRef worker=((MasterWorkerProtocol.WorkerRequestsWork) msg).worker;
            log.info("Worker requests work: {}", worker);
            if(workers.containsKey(worker)){
                if(workQ.isEmpty())worker.tell(new MasterWorkerProtocol.NoWorkToBeDone(),self());
                else if(workers.get(worker).equals(Option.none())){
                    Tuple2<ActorRef,Object> tuple2= workQ.poll();

                    workers.put(worker,Option.some(tuple2));
                    // Use the special form of 'tell' that lets us supply  the sender()
                    worker.tell(new MasterWorkerProtocol.WorkToBeDone(tuple2._2()),tuple2._1());
                }
            }
        }

        // Worker has completed its work and we can clear it out
        else if (msg instanceof MasterWorkerProtocol.WorkIsDone) {
            ActorRef worker = ((MasterWorkerProtocol.WorkIsDone) msg).worker;
            if (!workers.containsKey(worker)) {
                log.error("Blurgh! {} said it's done work but we didn't know about him", worker);
            } else {
                workers.put(worker, Option.none());
            }
        }

        // A worker died.  If he was doing anything then we need
        // to give it to someone else so we just add it back to the
        // master and let things progress as usual
        else if(msg instanceof Terminated){
            ActorRef worker=((Terminated) msg).getActor();
            if(workers.containsKey(worker)&& !workers.get(worker).equals(Option.none())){
                log.error("Blurgh! {} died while processing {}", worker, workers.get(worker));
                Tuple2<ActorRef,Object> tuple2=workers.get(worker).get();
                // Send the work that it was doing back to ourselves for processing
                self().tell(tuple2._2(),tuple2._1());
            }
            workers.remove(worker);
            getContext().unwatch(worker);
        }

        // Anything other than our own protocol is "work to be done"
        else if(msg instanceof Object){
            log.info("Queueing {}", msg);
            workQ.add(new Tuple2<ActorRef, Object>(sender(),msg));
            notifyWorkers();
        }
    }
}

package com.cffex.chapter9.balancing.workload;

import akka.actor.ActorPath;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Procedure;

/**
 * Created by Ming on 2016/6/8.
 */
public abstract class Worker extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    // We need to know where the master is
    private ActorSelection master;

    /**
     * This is how our derivations will interact with us.  It
     * allows dervations to complete work asynchronously
     */
    public class WorkComplete {
        public final Object result;

        public WorkComplete(Object result) {
            this.result = result;
        }
    }

    public Worker(ActorPath masterLocation) {
        master = getContext().actorSelection(masterLocation);
    }

    public void preStart() {
        log.debug("Worker preStart()");
        master.tell(new MasterWorkerProtocol.WorkerCreated(self()), self());
    }

    /**
     * This is the state we're in when we're working on something.
     * In this state we can deal with messages in a much more
     * reasonable manner
     *
     * @param msg
     */
    private final Procedure<Object> working = new Procedure<Object>() {
        public void apply(Object msg) {
            if (msg instanceof WorkComplete) {
                log.info("Work is complete.  Result {}.", ((WorkComplete) msg).result);
                master.tell(new MasterWorkerProtocol.WorkIsDone(self()), self());
                master.tell(new MasterWorkerProtocol.WorkerRequestsWork(self()), self());
                // We're idle now
                getContext().become(idle);
            } else if (msg instanceof MasterWorkerProtocol.WorkToBeDone) {
                log.info("Yikes. Master told me to do work, while I'm working.");
            } else {
                unhandled(msg);
            }
        }
    };

    /**
     * In this state we have no work to do.  There really are only
     * two messages that make sense while we're in this state, and
     * we deal with them specially here
     */
    private final Procedure<Object> idle = new Procedure<Object>() {
        public void apply(Object msg) {
            // Master says there's work to be done, let's ask for it
            if (msg instanceof MasterWorkerProtocol.WorkIsReady) {
                log.info("Requesting work");
                master.tell(new MasterWorkerProtocol.WorkerRequestsWork(self()), self());
            }
            //Send the work off to the implementation
            else if (msg instanceof MasterWorkerProtocol.WorkToBeDone) {
                log.info("Got work {}", ((MasterWorkerProtocol.WorkToBeDone) msg).work);
                doWork(sender(), ((MasterWorkerProtocol.WorkToBeDone) msg).work);
                getContext().become(working);
            }
            // We asked for it, but either someone else got it first, or
            // there's literally no work to be done
            else if (msg instanceof MasterWorkerProtocol.NoWorkToBeDone) {
                log.info("The master doesn't have any works");
            }

        }
    };

    public void onReceive(Object message) throws Exception {
        unhandled(message);
    }

    //initial state of the actor
    {
        getContext().become(idle);
    }

    // Required to be implemented
    public abstract void doWork(ActorRef workSender, Object work);
}

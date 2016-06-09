package com.cffex.chapter9.balancing.workload;

import akka.actor.ActorRef;

/**
 * Created by Ming on 2016/6/8.
 */
public class MasterWorkerProtocol {
    // Messages from Workers
    public static class WorkerCreated {
        public ActorRef worker;

        public WorkerCreated(ActorRef worker) {
            this.worker = worker;
        }
    }

    public static class WorkerRequestsWork {
        public ActorRef worker;

        public WorkerRequestsWork(ActorRef worker) {
            this.worker = worker;
        }
    }

    public static class WorkIsDone {
        public ActorRef worker;

        public WorkIsDone(ActorRef worker) {
            this.worker = worker;
        }
    }

    // Messages to Workers
    public static class WorkToBeDone {
        public Object work;

        public WorkToBeDone(Object work) {
            this.work = work;
        }
    }

    public static class WorkIsReady {
    }

    public static class NoWorkToBeDone {
    }
}

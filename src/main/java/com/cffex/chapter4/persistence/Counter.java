package com.cffex.chapter4.persistence;

import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Procedure;
import akka.persistence.RecoveryCompleted;
import akka.persistence.SaveSnapshotFailure;
import akka.persistence.SaveSnapshotSuccess;
import akka.persistence.SnapshotOffer;
import akka.persistence.UntypedPersistentActor;

import java.io.Serializable;

/**
 * Created by Ming on 2016/6/5.
 */
public class Counter extends UntypedPersistentActor {

    /**
     * ===============================================#
     * static object message classes
     * ===============================================#
     */
    static abstract class Operation implements Serializable {
        final public int count;

        public Operation(int count) {
            this.count = count;
        }
    }

    static public class Increment extends Operation {
        public Increment(int count) {
            super(count);
        }
    }

    static public class Decrement extends Operation {
        public Decrement(int count) {
            super(count);
        }
    }

    static public class Cmd implements Serializable {
        final public Operation op;

        public Cmd(Operation op) {
            this.op = op;
        }
    }

    static public class Evt implements Serializable {
        final public Operation op;

        public Evt(Operation op) {
            this.op = op;
        }
    }

    static public class States implements Serializable{
        final public int count;

        public States(int count) {
            this.count = count;
        }
    }

    /**
     * ===============================================#
     * actor instance variables
     * ===============================================#
     */

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private Counter.States state = new Counter.States(0);

    private void takeSnapshot() {
        if (state.count % 5 == 0) {
            saveSnapshot(state);
        }
    }

    private void updateState(Evt evt) {

        if (evt.op instanceof Increment) {
            log.info("Counter receive Increment {} on recovering mood", evt.op);
            state = new States(state.count + evt.op.count);
            takeSnapshot();
        } else if (evt.op instanceof Decrement) {
            log.info("Counter receive Decrement {} on recovering mood", evt.op);
            state = new Counter.States(state.count - evt.op.count);
            takeSnapshot();
        }
    }

    @Override
    public void onReceiveRecover(Object msg) throws Exception {
        if (msg instanceof Evt) {
            log.info("Counter receive {} on recovering mood", ((Evt) msg).op);
            updateState((Evt) msg);
        } else if (msg instanceof SnapshotOffer) {
            log.info("Counter receive snapshot with data:" + ((SnapshotOffer) msg).snapshot() + "  on recovering mood");
            state = (Counter.States) ((SnapshotOffer) msg).snapshot();
        } else if (msg instanceof RecoveryCompleted) {
            log.info("Recovery Complete and Now I'll swtich to receiving mode :)");
        }
    }

    @Override
    public void onReceiveCommand(Object msg) throws Exception {
        if (msg instanceof Cmd) {
            Operation op = ((Cmd) msg).op;
            log.info("Counter receive {}", op);
            persist(new Evt(op), new Procedure<Evt>() {
                public void apply(Evt event) throws Exception {
                    updateState(event);
                }
            });
        } else if (msg instanceof SaveSnapshotSuccess) {
            log.info("save snapshot succeed.");
        } else if (msg instanceof SaveSnapshotFailure) {
            log.info("save snapshot failed and failure is {}", ((SaveSnapshotFailure) msg).metadata());
        } else if ("print".equals(msg)) {
            log.info("the current state of counter is {}", state.count);
        }
    }

    @Override
    public String persistenceId() {
        return "counter-example2";
    }
}

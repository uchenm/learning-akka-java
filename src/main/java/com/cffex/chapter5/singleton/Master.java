package com.cffex.chapter5.singleton;

import akka.actor.ActorRef;
import akka.japi.Procedure;
import akka.persistence.SnapshotOffer;
import akka.persistence.UntypedPersistentActor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Ming on 2016/6/4.
 */
public class Master extends UntypedPersistentActor {
    /**
     * ==============================================#
     * companion message object class
     * ==============================================#
     */

    public static interface Command extends Serializable {
    }

    public static class RegisterWorker implements Command {
        final public ActorRef worker;

        public RegisterWorker(ActorRef worker) {
            this.worker = worker;
        }
    }

    public static class RequestWork implements Command {
        public ActorRef requester;

        public RequestWork(ActorRef requester) {
            this.requester = requester;
        }
    }

    public static class Work implements Command {
        public ActorRef requester;
        public String op;

        public Work(ActorRef requester, String op) {
            this.op = op;
            this.requester = requester;
        }
    }

    public static interface Event extends Serializable {
    }

    public static class AddWorker implements Event {
        final public ActorRef worker;

        public AddWorker(ActorRef worker) {
            this.worker = worker;
        }
    }

    public static class AddWork implements Event {
        final public Work work;

        public AddWork(Work work) {
            this.work = work;
        }
    }

    public static class UpdateWorks implements Event {
        final public List<Work> works;

        public UpdateWorks(List<Work> works) {
            this.works = works;
        }
    }

    public static class State implements Serializable {
        final public Set<ActorRef> workers;
        final public List<Work> works;

        public State(Set<ActorRef> workers, List<Work> works) {
            this.workers = workers;
            this.works = works;
        }
    }

    public static class NoWork implements Serializable {
    }
    /**
     * ==============================================#
     * instance variables
     * ==============================================#
     */
    private Set<ActorRef> workers = new HashSet<>();
    private List<Work> works = new ArrayList<>();

    private void updateState(Event evt) {
        getContext().system().log().info("master updateState message {}",evt);
        if (evt instanceof AddWork) {
            works.add(((AddWork) evt).work);
        } else if (evt instanceof AddWorker) {
            workers.add(((AddWorker) evt).worker);
        } else if (evt instanceof UpdateWorks) {
            works = ((UpdateWorks) evt).works;
        }
    }

    public void onReceiveRecover(Object msg) throws Exception {
        getContext().system().log().info("master onReceiveRecover message {}",msg);
        if (msg instanceof Event) {
            updateState((Event) msg);
        } else if (msg instanceof SnapshotOffer) {
            State state = (State) ((SnapshotOffer) msg).snapshot();
            workers = state.workers;
            works = state.works;
        }
    }

    public void onReceiveCommand(Object msg) throws Exception {
        getContext().system().log().info("master onReceiveCommand message {}",msg);
        if (msg instanceof RegisterWorker) {
            ActorRef worker = ((RegisterWorker) msg).worker;
            persist(new AddWorker(worker), new Procedure<AddWorker>() {
                public void apply(AddWorker event) throws Exception {
                    updateState(event);
                }
            });
        } else if (msg instanceof RequestWork) {
            if (works.isEmpty()) {
                sender().tell(new NoWork(), self());
            } else if (workers.contains(((RequestWork) msg).requester) && !works.isEmpty()) {
                sender().tell(works.get(0), self());
                List<Work> remainingWork = works.stream().skip(1).collect(Collectors.toList());
                persist(new UpdateWorks(remainingWork), new Procedure<UpdateWorks>() {
                    public void apply(UpdateWorks event) throws Exception {
                        updateState(event);
                    }
                });
            }
        } else if (msg instanceof Work) {
            persist(new AddWork((Work) msg), new Procedure<AddWork>() {
                public void apply(AddWork event) throws Exception {
                    updateState(event);
                }
            });
        }
    }

    public String persistenceId() {
        return self().path().parent().name() + "-" + self().path().name();
    }
}

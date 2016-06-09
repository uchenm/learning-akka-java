package com.cffex.chapter2.talktoactor;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.Serializable;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

/**
 * Created by Ming on 2016/6/5.
 */
public class Recorder extends UntypedActor {
    /**
     * ===========================================#
     * message case class
     * ===========================================#
     */
    //RecorderMsg
    public static interface RecorderMsg extends Serializable {
    }

    public static class NewUser implements RecorderMsg {
        public NewUser(User user) {
            this.user = user;
        }

        final public User user;
    }

    /**
     * ===========================================#
     * message case class
     * ===========================================#
     */

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private ActorRef checker;
    private ActorRef storage;

    public Recorder(ActorRef checker, ActorRef storage) {
        this.checker = checker;
        this.storage = storage;
    }

    public void onReceive(Object message) throws Exception {
        if (message instanceof NewUser) {
            User user = ((Recorder.NewUser) message).user;
            Timeout timeout = new Timeout(Duration.create(5, "seconds"));
            Future<Object> future = Patterns.ask(checker, new Checker.CheckUser(user), timeout);
            Checker.CheckerMsg result = (Checker.CheckerMsg) Await.result(future, timeout.duration());
            if (result instanceof Checker.WhiteUser) {
                storage.tell(new Storage.AddUser(((Checker.WhiteUser) result).user), self());
            } else if (result instanceof Checker.BlackUser) {
                log.info("Recorder:{} in in black user.",((Checker.BlackUser) result).user);
            }
        }
        //pipe(, context().dispatcher()).to(getSender());
    }
}

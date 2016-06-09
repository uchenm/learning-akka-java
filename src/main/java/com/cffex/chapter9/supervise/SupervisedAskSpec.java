package com.cffex.chapter9.supervise;

import akka.actor.ActorRef;
import akka.actor.ActorRefFactory;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;

/**
 * Created by Ming on 2016/6/9.
 */
public class SupervisedAskSpec {

    public static Object execute(Class<? extends UntypedActor> someActor,
                          Object message, Timeout timeout, ActorRefFactory actorSystem)
            throws Exception {
        // example usage
        try {
            ActorRef supervisorCreator = SupervisedAsk
                    .createSupervisorCreator(actorSystem);
            Future<Object> finished = SupervisedAsk.askOf(supervisorCreator,
                    Props.create(someActor), message, timeout);
            return Await.result(finished, timeout.duration());
        } catch (Exception e) {
            // exception propagated by supervision
            throw e;
        }
    }
}

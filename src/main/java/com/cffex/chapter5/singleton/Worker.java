package com.cffex.chapter5.singleton;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.cluster.singleton.ClusterSingletonProxy;
import akka.cluster.singleton.ClusterSingletonProxySettings;
import scala.Option;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

/**
 * Created by Ming on 2016/6/4.
 */
public class Worker extends UntypedActor {

    public static Props props = Props.create(Worker.class);

    private ActorRef masterProxy = getContext().actorOf(
            ClusterSingletonProxy.props("/user/master",
                    ClusterSingletonProxySettings.apply(getContext().system()).withRole(Option.empty())
            ), "masterProxy");


    public void preStart() throws Exception {
        super.preStart();
        getContext().system().scheduler().schedule(Duration.create(0, TimeUnit.SECONDS), Duration.create(30, TimeUnit.SECONDS), new Runnable() {
            public void run() {
                masterProxy.tell(new Master.RegisterWorker(self()), self());
            }
        }, getContext().system().dispatcher());
        getContext().system().scheduler().schedule(Duration.create(3, TimeUnit.SECONDS), Duration.create(3, TimeUnit.SECONDS), new Runnable() {
            public void run() {
                masterProxy.tell(new Master.RequestWork(self()), self());
            }
        }, getContext().system().dispatcher());
    }

    public void onReceive(Object message) throws Exception {
        getContext().system().log().info("worker onReceive message {}",message);
        if (message instanceof Master.Work) {
            getContext().system().log().info("Worker: I received work with op: {} and I will reply to {}.",
                    ((Master.Work) message).op ,  ((Master.Work) message).requester);
        }else if(message instanceof Master.NoWork){
            getContext().system().log().info("there is no work to do");
        }
    }
}

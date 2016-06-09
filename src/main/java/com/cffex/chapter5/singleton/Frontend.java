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
public class Frontend extends UntypedActor {
    /**
     * ==============================================#
     * companion message object class
     * ==============================================#
     */
    public static class Tick {
    }

    public static Props props = Props.create(Frontend.class);


    /**
     * ==============================================#
     * instance variables
     * ==============================================#
     */
    private ActorRef masterProxy = getContext().actorOf(
            ClusterSingletonProxy.props("/user/master",
                    ClusterSingletonProxySettings.apply(getContext().system()).withRole(Option.empty())
            ), "masterProxy");

    public void preStart() throws Exception {
        super.preStart();
        System.out.println("==========postStart================");
        getContext().system().scheduler().schedule(Duration.create(0, TimeUnit.SECONDS), Duration.create(3, TimeUnit.SECONDS), new Runnable() {
            public void run() {
                self().tell(new Tick(), self());
            }
        }, getContext().system().dispatcher());
    }

    public void onReceive(Object message) throws Exception {
        if (message instanceof Tick) {
            masterProxy.tell(new Master.Work(self(), "add"), self());
        }
    }


}

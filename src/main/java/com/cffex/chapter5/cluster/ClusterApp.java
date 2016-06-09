package com.cffex.chapter5.cluster;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Address;
import akka.actor.Props;
import akka.actor.RootActorPath;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import com.cffex.chapter5.commons.Add;
import com.cffex.chapter5.commons.BackendRegistration;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Ming on 2016/6/1.
 */
public class ClusterApp {
    public static void main(String[] args) throws Exception {
        //initiate frontend node
        ActorRef frontend=Frontend.initiate();

        //initiate three nodes from backend
        Backend.initiate(2552);

        Backend.initiate(2560);

        Backend.initiate(2561);

        Thread.sleep(10000);

        frontend.tell(new Add(2, 4),ActorRef.noSender());
        frontend.tell(new Add(2, 4),ActorRef.noSender());
        frontend.tell(new Add(2, 4),ActorRef.noSender());
        frontend.tell(new Add(2, 4),ActorRef.noSender());

    }
}


class Backend extends UntypedActor {

    private Cluster cluster = Cluster.get(getContext().system());

    public void preStart() {
        cluster.subscribe(self(), ClusterEvent.MemberUp.class);
    }

    public void postStop() {
        cluster.unsubscribe(self());
    }

    public void onReceive(Object message) throws Exception {
        if (message instanceof Add) {
            System.out.println("I'm a backend with path: " + self() + " and I received add operation.");
        } else if (message instanceof ClusterEvent.MemberUp) {
            if (((ClusterEvent.MemberUp) message).member().hasRole("frontend")) {
                Address address = ((ClusterEvent.MemberUp) message).member().address();
                ActorSelection frontActorSelection = getContext().actorSelection(RootActorPath.apply(address, "/") + "/user/frontend");
                //register backend itself in the frontend.
                frontActorSelection.tell(new BackendRegistration(), self());
            }
        }
    }

    public static ActorRef initiate(int port) {
        Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port="+port).
                withFallback(ConfigFactory.load().getConfig("Backend"));
        ActorSystem system = ActorSystem.apply("ClusterSystem", config);
        return system.actorOf(Props.create(Backend.class), "backend");
    }
}

class Frontend extends UntypedActor {
    List<ActorRef> backends = new ArrayList<ActorRef>();

    public void onReceive(Object message) throws Exception {
        if (message instanceof Add) {
            if (backends.isEmpty()) {
                System.out.println("Service unavailable, cluster doesn't have backend node.");
            } else {
                System.out.println("Frontend: I'll forward add operation to backend node to handle it.");
                ActorRef randomBackend = backends.get(new Random().nextInt(backends.size()));
                randomBackend.forward(message, getContext());
            }
        } else if (message instanceof BackendRegistration) {
            if (!backends.contains(sender())) {
                backends.add(sender());
                getContext().watch(sender());
            }
        }else if(message instanceof Terminated){
            ActorRef targetActor=((Terminated) message).getActor();
            backends.remove(targetActor);
        }
    }
    public static ActorRef initiate() {
        Config config = ConfigFactory.load().getConfig("Frontend").
                withFallback(ConfigFactory.load().getConfig("Backend"));
        ActorSystem system = ActorSystem.apply("ClusterSystem", config);
        return system.actorOf(Props.create(Frontend.class), "frontend");
    }
}
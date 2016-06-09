package com.cffex.chapter5.remoting;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Created by Ming on 2016/5/31.
 */
public class MemberServiceLookup {
    public static void main(String[] args)throws Exception{
        Config config = ConfigFactory.load().getConfig("MemberServiceLookup");
        ActorSystem system = ActorSystem.apply("MemberServiceLookup", config);
        ActorSelection worker = system.actorSelection("akka.tcp://MembersService@127.0.0.1:2552/user/remote-worker");
        System.out.println("Worker actor path is ${worker.path}"+worker.path());
        worker.tell(new Worker.Work("Hi Remote Actor"),ActorRef.noSender());
        system.terminate();
    }
}

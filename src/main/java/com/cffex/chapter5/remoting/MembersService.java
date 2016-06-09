package com.cffex.chapter5.remoting;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Created by Ming on 2016/5/31.
 */
public class MembersService {
    public static void main(String[] args) throws Exception{
        Config config = ConfigFactory.load().getConfig("MembersService");
        ActorSystem system = ActorSystem.apply("MembersService", config);
        ActorRef worker = system.actorOf(Props.create(Worker.class), "remote-worker");
        System.out.println("Worker actor path is ${worker.path}"+worker.path());
        system.terminate();
    }
}

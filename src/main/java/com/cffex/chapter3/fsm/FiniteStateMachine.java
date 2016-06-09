package com.cffex.chapter3.fsm;

import akka.actor.AbstractFSMWithStash;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

/**
 * Created by Ming on 2016/5/31.
 */
public class FiniteStateMachine {
    public static void main(String[] args) throws Exception{
        ActorSystem system = ActorSystem.apply("Hotswap-FSM");

        ActorRef userStorage = system.actorOf(Props.create(UserStorageFSM.class), "userStorage-fsm");

        userStorage.tell(new UserStorageFSM.Connect(),ActorRef.noSender());

        userStorage.tell( new UserStorageFSM.Operation(UserStorageFSM.DBOperation.Create, new UserStorageFSM.User("Admin", "admin@packt.com")),ActorRef.noSender());

        userStorage.tell(new UserStorageFSM.Disconnect(),ActorRef.noSender());

        Thread.sleep(1000);

        system.terminate();
    }
}
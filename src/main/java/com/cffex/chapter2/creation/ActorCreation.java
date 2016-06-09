package com.cffex.chapter2.creation;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;

/**
 * Created by Ming on 2016/5/29.
 */
public class ActorCreation {

    public static void main(String[] args) throws Exception{
        //create akka 'hello-akka' actor system
        ActorSystem system = ActorSystem.apply("creation");
        //create the MusicPlayer actor
        ActorRef player = system.actorOf(MusicPlayer.props, "musicPlayer");
        //send message to the player to start music
        player.tell(MusicPlayer.PlayMsg.StartMusic, ActorRef.noSender());

        Thread.sleep(1000);
        //terminate the actor system
        system.terminate();
    }
}







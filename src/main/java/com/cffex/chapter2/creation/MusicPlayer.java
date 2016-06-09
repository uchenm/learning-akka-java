package com.cffex.chapter2.creation;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * Created by Ming on 2016/6/5.
 */
public class MusicPlayer extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public static enum PlayMsg{
        StopMusic,StartMusic
    }

    public static Props props = Props.create(MusicPlayer.class);

    public void onReceive(Object message) {
        if (message == PlayMsg.StopMusic) {
            log.debug("I don't want to stop music...");
        }
        else if (message == PlayMsg.StartMusic) {
            log.debug("I am going to start the music...");
            ActorRef controller = context().actorOf(MusicController.props, "musicController");
            controller.tell(MusicController.ControllerMsg.Play, self());
        }else{
            // the default message handler
            log.debug("Unknown messages");
        }

    }
}

package com.cffex.chapter2.creation;

import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * Created by Ming on 2016/6/5.
 */
public class MusicController extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public static enum ControllerMsg{
        Play,Stop
    }
    public static Props props = Props.create(MusicController.class);

    public void onReceive(Object message) {
        if (message == ControllerMsg.Play) {
            log.debug("Music started...");
        }
        if (message == ControllerMsg.Stop) {
            log.debug("Music stopped...");
        }
    }
}

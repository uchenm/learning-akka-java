package com.cffex.chapter9.shutdown;

import akka.actor.UntypedActor;

/**
 * Created by Ming on 2016/6/9.
 */
public abstract class ReaperWatched extends UntypedActor {
    public void preStart() {
        System.out.println("IN PRESTART");
        getContext().actorSelection("/user/" + Reaper.name).tell(new Reaper.WatchMe(self()), self());
    }
}

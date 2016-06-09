package com.cffex.chapter9.shutdown;

import akka.actor.ActorRef;
import akka.actor.Terminated;
import akka.actor.UntypedActor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ming on 2016/6/9.
 */
public class Reaper extends UntypedActor {

    /**
     * case class messages
     */

    public static String name = "reaper";
    public static class WatchMe{
        public final ActorRef ref;
        public WatchMe( ActorRef ref){
            this.ref=ref;
        }
    }


    private List<ActorRef> watched=new ArrayList<ActorRef>();

    public void allSoulsReaped() {
        System.out.println("SYSTEM SHUTDOWN START");
        getContext().system().shutdown();
        System.out.println("SYSTEM SHUTDOWN END");
    }

    public void onReceive(Object msg){
        if(msg instanceof WatchMe){
            getContext().watch(((WatchMe) msg).ref);
            watched.add(((WatchMe) msg).ref);
        }else if(msg instanceof Terminated){
            watched.remove(((Terminated) msg).getActor());
            if (watched.isEmpty()) allSoulsReaped();
        }
    }
}

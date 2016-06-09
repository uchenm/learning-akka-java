package com.cffex.chapter5.remoting;

import akka.actor.UntypedActor;

/**
 * Created by Ming on 2016/5/31.
 */
public class Worker extends UntypedActor {
    public static class Work {
        final public String message;
        public Work(String message) {
            this.message = message;
        }
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof Work){
            System.out.println("I received Work Message and My ActorRef:"+self());
        }
    }
}



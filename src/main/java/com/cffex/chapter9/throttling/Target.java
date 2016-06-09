package com.cffex.chapter9.throttling;

import akka.actor.UntypedActor;

import java.util.Date;

/**
 * Created by Ming on 2016/6/9.
 */

public class Target extends UntypedActor{
    public void onReceive(Object msg) throws Exception{
        System.out.println(new Date().toString()+"I received a message: "+msg);
    }
}

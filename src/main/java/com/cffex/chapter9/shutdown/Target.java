package com.cffex.chapter9.shutdown;

import java.util.Date;

/**
 * Created by Ming on 2016/6/9.
 */

public class Target extends ReaperWatched{
    public void onReceive(Object msg) throws Exception{
        System.out.println(new Date().toString()+"I received a message: "+msg);
    }
}

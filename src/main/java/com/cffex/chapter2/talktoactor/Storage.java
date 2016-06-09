package com.cffex.chapter2.talktoactor;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ming on 2016/6/5.
 */
public class Storage extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    //StorageMsg
    public static interface StorageMsg extends Serializable{}
    public static class AddUser implements StorageMsg{
        public AddUser(User user){
            this.user=user;
        }
        final public  User user;
    }

    List<User> users=new ArrayList<User>();
    public void onReceive(Object message) {
        if (message instanceof AddUser) {
            User user=((AddUser)message).user;
            users.add(user);
            log.info("user {} added...",user);
        }
    }
}

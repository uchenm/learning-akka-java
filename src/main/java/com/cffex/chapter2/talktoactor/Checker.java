package com.cffex.chapter2.talktoactor;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Ming on 2016/6/5.
 */
public class Checker extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    //CheakerMsg
    public static interface CheckerMsg extends Serializable{}
    public static class CheckUser implements CheckerMsg{
        public CheckUser(User user){
            this.user=user;
        }
        final public  User user;
    }
    public static class BlackUser implements CheckerMsg{
        public BlackUser(User user){
            this.user=user;
        }
        final public  User user;
    }
    public static class WhiteUser implements CheckerMsg{
        public WhiteUser(User user){
            this.user=user;
        }
        final public  User user;
    }

    User[] users = { new User("Adam","adam@gmail.com") };
    List<User> blackList= Arrays.asList(users);
    public void onReceive(Object message) {
        if (message instanceof CheckUser) {
            User user=((CheckUser)message).user;
            if(blackList.contains(user)){
                log.info("the blacklist contains the user {}",user);
                sender().tell(new BlackUser(user),self());
            }else{
                log.info("the blacklist does not contain the user {}",user);
                sender().tell(new WhiteUser(user),self());
            }
        }
    }
}

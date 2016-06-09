package com.cffex.chapter3.hotswap.behavior;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Option;
import akka.japi.Procedure;

/**
 * Created by Ming on 2016/5/30.
 */
public class BecomeHotswap {

    public static void main(String[] args) throws Exception{
        ActorSystem system = ActorSystem.create("Hotswap-Become");

        ActorRef userStorage = system.actorOf(Props.create(UserStorage.class), "userStorage");
        Option<UserStorage.User> user=Option.some(new UserStorage.User("Admin", "admin@packt.com"));
        userStorage.tell(new UserStorage.Operation(UserStorage.DBOperation.Create, user),ActorRef.noSender());
        userStorage.tell(new UserStorage.Connect(),ActorRef.noSender());
        userStorage.tell(new UserStorage.Disconnect(),ActorRef.noSender());
        Thread.sleep(100);

        system.terminate();
    }
}



class UserStorage extends UntypedActorWithStash{
    /**
     * ==========================================#
     * message case classes
     * ==========================================#
     */
    public static class User{
        public final String username;
        public final String email;
        public User( String username,  String email){
            this.username=username;
            this.email=email;
        }
    }

    public static enum DBOperation{
        Create,Update,Read,Delete
    }

    public static class Connect{}
    public static class Disconnect{}
    public static class Operation{
        public final DBOperation dBOperation;
        public final Option<User> user;
        public Operation( DBOperation dBOperation,  Option<User> user){
            this.dBOperation=dBOperation;
            this.user=user;
        }
    }

    /**
     * ===============================================#
     * @param msg
     * ===============================================#
     */

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    Procedure<Object> connected = new Procedure<Object>() {
        @Override
        public void apply(Object message) {
            if(message instanceof Disconnect){
                log.debug("User Storage Disconnect from DB");
                getContext().unbecome();
            }else if(message instanceof Operation){
                Operation op=(Operation)message;
                log.debug("User Storage receive {} to do in user:{}",op.dBOperation,op.user);
            }
        }
    };

    Procedure<Object> disconnected = new Procedure<Object>() {
        @Override
        public void apply(Object message) {
            if (message instanceof Connect) {
                log.debug("User Storage connected to DB");
                unstashAll();
                getContext().become(connected);
            } else{
                stash();
            }
        }
    };

    {
        getContext().become(disconnected);
    }

    public void onReceive(Object msg){
        log.debug("unhandled is received!");
        unhandled(msg);
    }
}
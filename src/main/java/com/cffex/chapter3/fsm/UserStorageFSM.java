package com.cffex.chapter3.fsm;

import akka.actor.AbstractFSMWithStash;

/**
 * Created by Ming on 2016/6/6.
 */
public class UserStorageFSM extends AbstractFSMWithStash<UserStorageFSM.State, UserStorageFSM.Data> {
    /**
     * ==========================================#
     * message case class
     * ==========================================#
     */
    // FSM State
    public static enum State {
        Connected, Disconnected
    }

    //FSM Data
    public static enum Data {
        EmptyData
    }

    public static enum DBOperation {
        Create, Update, Read, Delete
    }

    public static class Connect {
    }

    public static class Disconnect {
    }

    public static class Operation {
        public final DBOperation op;
        public final User user;

        public Operation(DBOperation op, User user) {
            this.op = op;
            this.user = user;
        }
    }

    public static class User {
        public final String username;
        public final String email;

        public User(String username, String email) {
            this.username = username;
            this.email = email;
        }
    }
    /**
     * ==========================================#
     * FSM
     * ==========================================#
     */
    {
        // 1. define start with
        startWith(UserStorageFSM.State.Disconnected, UserStorageFSM.Data.EmptyData);
        // 2. define states
        when(UserStorageFSM.State.Disconnected,
                matchEvent(UserStorageFSM.Connect.class, (x, data) -> {
                    log().info("UserStorage Connected to DB");
                    unstashAll();
                    return goTo(UserStorageFSM.State.Connected).using(UserStorageFSM.Data.EmptyData);
                }).anyEvent((event, data) -> {
                    stash();
                    log().warning("Received unknown event: " + event);
                    return stay().using(UserStorageFSM.Data.EmptyData);
                })
        );
        when(UserStorageFSM.State.Connected,
                matchEvent(UserStorageFSM.Disconnect.class, (x, data) -> {
                    log().info("UserStorage disconnected from DB");
                    unstashAll();
                    return goTo(UserStorageFSM.State.Disconnected).using(UserStorageFSM.Data.EmptyData);
                }).event(UserStorageFSM.Operation.class, (x, data) -> {
                    log().info("UserStorage receive{} operation to do in user:" ,x.op,x.user);
                    stash();
                    return stay().using(UserStorageFSM.Data.EmptyData);
                })
        );
        // 3. initialize
        initialize();
    }

}

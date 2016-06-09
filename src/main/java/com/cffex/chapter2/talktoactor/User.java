package com.cffex.chapter2.talktoactor;

import java.io.Serializable;

/**
 * Created by Ming on 2016/6/5.
 */
public class User implements Serializable{
    public User(String username,String email){
        this.username=username;
        this.email=email;
    }
    final public String username;
    final public  String email;
}

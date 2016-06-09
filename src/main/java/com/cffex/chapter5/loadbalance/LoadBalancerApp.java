package com.cffex.chapter5.loadbalance;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.routing.FromConfig;
import com.cffex.chapter5.commons.Add;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.duration.Duration;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ming on 2016/6/4.
 */
public class LoadBalancerApp {
    public static void main(String[] args) throws Exception {
        //initiate three nodes from backend
        Backend.initiate(2551);

        Backend.initiate(2552);

        Backend.initiate(2561);

        //initiate frontend node
        Frontend.initiate();
    }
}




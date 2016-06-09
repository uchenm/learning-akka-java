//package com.cffex.chapter9.throttling;
//
//import akka.actor.ActorRef;
//import akka.japi.Option;
//import scala.concurrent.duration.Duration;
//import scala.concurrent.duration.FiniteDuration;
//
//import java.util.concurrent.TimeUnit;
//
///**
// * Created by Ming on 2016/6/9.
// */
//public class Throttler {
//    public static class Rate{
//        public final int numberOfCalls;
//        public final FiniteDuration duration;
//        public Rate(int numberOfCalls,  FiniteDuration duration){
//            this.numberOfCalls=numberOfCalls;
//            this.duration=duration;
//        }
//        public Long durationInMillis(){
//            return duration.toMillis();
//        }
//    }
//
//    public static class SetTarget{
//        public final Option<ActorRef> target;
//        public SetTarget(Option<ActorRef> target){
//            this.target=target;
//        }
//        public SetTarget(ActorRef target){
//            this(Option.some(target));
//        }
//    }
//    public static class SetRate{
//        public final Rate rate;
//        public SetRate(Rate rate){
//            this.rate=rate;
//        }
//    }
//    public static class RateInt{
//        public final int numberOfCalls;
//        public RateInt(int numberOfCalls){
//            this.numberOfCalls=numberOfCalls;
//        }
//
//        public Rate msgsPer( int duration,  TimeUnit timeUnit){
//            return new Rate(numberOfCalls, Duration.create(duration, timeUnit));
//        }
//        public Rate msgsPer( FiniteDuration duration){
//            return new Rate(numberOfCalls, duration);
//        }
//        public Rate msgsPerSecond(){
//            return new Rate(numberOfCalls, Duration.create(1, TimeUnit.SECONDS));
//        }
//        public Rate msgsPerMinute(){
//            return new Rate(numberOfCalls, Duration.create(1, TimeUnit.MINUTES));
//        }
//        public Rate msgsPerHour(){
//            return new Rate(numberOfCalls, Duration.create(1, TimeUnit.HOURS));
//        }
//    }
//}

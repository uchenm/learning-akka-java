//package com.cffex.chapter7.quickstart;
//
//import akka.NotUsed;
//import akka.actor.ActorSystem;
//import akka.stream.ActorMaterializer;
//import akka.stream.Materializer;
//import akka.stream.javadsl.Flow;
//import akka.stream.javadsl.Sink;
//import akka.stream.javadsl.Source;
//
///**
// * Created by Ming on 2016/6/6.
// */
//public class StreamDemo {
//    public static void main(String[] args) throws Exception {
//        final ActorSystem system = ActorSystem.create("StreamDemo");
//        final Materializer materializer = ActorMaterializer.create(system);
//
//        final Source<Integer, NotUsed> source = Source.range(1, 100);
//
//        final Flow<Integer,Integer,Materializer> flow= Flow.of(Integer.class).map(i->i*2 ).toMat(System.out.println);
//
//        final Sink output = Sink.foreach<Int>(System.out.println);
//    }
//}

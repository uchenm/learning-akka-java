package com.cffex.chapter7.tweets;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.ClosedShape;
import akka.stream.FlowShape;
import akka.stream.Materializer;
import akka.stream.SinkShape;
import akka.stream.UniformFanOutShape;
import akka.stream.javadsl.Broadcast;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.GraphDSL;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

import java.util.ArrayList;

/**
 * Created by Ming on 2016/6/6.
 */
public class ReactiveTweetsApp {
    public static void main(String[] args) throws Exception {
        final ActorSystem system = ActorSystem.create("reactive-tweets");
        final Materializer mat = ActorMaterializer.create(system);
        Source<Tweets.Tweet, NotUsed> tweets = null;
        final Source<Tweets.Author, NotUsed> authors = tweets
                .filter(t -> t.hashtags().contains("AKKA"))
                .map(t -> t.author);
        authors.runWith(Sink.foreach(a -> System.out.println(a)), mat);
        //authors.runForeach(a -> System.out.println(a), mat);
        final Source<Tweets.Hashtag, NotUsed> hashtags =
                tweets.mapConcat(t -> new ArrayList<Tweets.Hashtag>(t.hashtags()));

//        Sink<Tweets.Author, NotUsed> writeAuthors;
//        Sink<Tweets.Hashtag, NotUsed> writeHashtags;
//        RunnableGraph.fromGraph(GraphDSL.create(b -> {
//            final UniformFanOutShape<Tweets.Tweet, Tweets.Tweet> bcast = b.add(Broadcast.create(2));
//            final FlowShape<Tweets.Tweet, Tweets.Author> toAuthor =
//                    b.add(Flow.of(Tweets.Tweet.class).map(t -> t.author));
//            final FlowShape<Tweets.Tweet, Tweets.Hashtag> toTags =
//                    b.add(Flow.of(Tweets.Tweet.class).mapConcat(t -> new ArrayList<Tweets.Hashtag>(t.hashtags())));
//            final SinkShape<Tweets.Author> authors = b.add(writeAuthors);
//            final SinkShape<Tweets.Hashtag> hashtags = b.add(writeHashtags);
//
//            b.from(b.add(tweets)).viaFanOut(bcast).via(toAuthor).to(authors);
//            b.from(bcast).via(toTags).to(hashtags);
//            return ClosedShape.getInstance();
//        })).run(mat);
    }
}

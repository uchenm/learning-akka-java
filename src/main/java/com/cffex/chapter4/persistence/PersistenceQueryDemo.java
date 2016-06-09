package com.cffex.chapter4.persistence;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.persistence.query.EventEnvelope;
import akka.persistence.query.PersistenceQuery;
import akka.persistence.query.journal.leveldb.javadsl.LeveldbReadJournal;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Source;

/**
 * Created by Ming on 2016/5/31.
 */
public class PersistenceQueryDemo {
    public static void main(String[] args) throws Exception{
        ActorSystem system = ActorSystem.create("persistent-query");

        ActorMaterializer mat = ActorMaterializer.create(system);

        LeveldbReadJournal queries = PersistenceQuery.get(system).getReadJournalFor(LeveldbReadJournal.class,
                LeveldbReadJournal.Identifier());

        Source<EventEnvelope, NotUsed> events =
                queries.eventsByPersistenceId("account", 0, Long.MAX_VALUE);

        events.runForeach(evt -> System.out.println("Event $evt"),mat);

        Thread.sleep(1000);

        system.terminate();
    }
}

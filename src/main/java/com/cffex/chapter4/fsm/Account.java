package com.cffex.chapter4.fsm;

import akka.persistence.UntypedPersistentActor;
import akka.persistence.fsm.PersistentFSM;

import java.io.Serializable;

/**
 * Created by Ming on 2016/6/5.
 */

public class Account extends UntypedPersistentActor {


    /**
     * ===============================================#
     * static object message classes
     * ===============================================#
     */
    public static abstract class State implements PersistentFSM.FSMState {
    }

    public static class Empty extends State {
        public String identifier() {
            return "Empty";
        }
    }

    public static class Active extends State {
        public String identifier() {
            return "Active";
        }
    }

    public static abstract class Data {
        public Float amount;
    }

    public static class ZeroBalance extends Data {
        public final Float amount = 0.0f;
    }

    public static class Balance extends Data {
        public final Float amount;

        public Balance(Float amount) {
            this.amount = amount;
        }
    }

    public static enum TransactionType {
        CR, DR
    }

    public static class Operation {
        public final Float amount;
        public final TransactionType type;

        public Operation(Float amount, TransactionType type) {
            this.amount = amount;
            this.type = type;
        }
    }

    public static interface DomainEvent extends Serializable{}
    public static class AcceptedTransaction implements DomainEvent{
        public final TransactionType type;
        public final Float amount;

        public AcceptedTransaction(Float amount,TransactionType type){
            this.amount=amount;
            this.type   =type;
        }
    }
    public static class RejectedTransaction implements DomainEvent{
        public final TransactionType type;
        public final Float amount;
        public final String reason;

        public RejectedTransaction(Float amount,TransactionType type,String reason){
            this.amount=amount;
            this.type   =type;
            this.reason=reason;
        }
    }

    /**
     * ===============================================#
     * instance variable and methods
     * ===============================================#
     */

    @Override
    public void onReceiveRecover(Object msg) throws Exception {

    }

    @Override
    public void onReceiveCommand(Object msg) throws Exception {

    }

    @Override
    public String persistenceId() {
        return "account";
    }

}

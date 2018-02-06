package org.vision.github.springboot.mqserver.thread.threadfactory;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author ganminghui
 * @date 2018/1/2
 */
public class MqServerThreadFactory implements ThreadFactory {
    private final AtomicLong threadCounter;
    private final ThreadFactory wrappedFactory;
    private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
    private final String namingPattern;
    private final Integer priority;
    private final Boolean daemonFlag;

    private MqServerThreadFactory(MqServerThreadFactory.Bulider bulider){
        this.threadCounter = new AtomicLong();
        this.wrappedFactory = Objects.isNull(bulider.wrappedFactory)? Executors.defaultThreadFactory() : bulider.wrappedFactory;
        this.uncaughtExceptionHandler = bulider.uncaughtExceptionHandler;
        this.namingPattern = bulider.namingPattern;
        this.priority = bulider.proirity;
        this.daemonFlag = bulider.daemonFlag;
    }

    public static class Bulider{
        private ThreadFactory wrappedFactory;
        private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
        private String namingPattern;
        private Integer proirity;
        private Boolean daemonFlag;

        public Bulider(){}

        public MqServerThreadFactory.Bulider wrappedFactory(ThreadFactory factory){
            Objects.requireNonNull(factory,"Wrapped ThreadFactory must not be null!");
            this.wrappedFactory = factory;
            return this;
        }

        public MqServerThreadFactory.Bulider namingPattern(String pattern){
            Objects.requireNonNull(pattern,"Naming pattern must not be null!");
            this.namingPattern = pattern;
            return this;
        }

        public MqServerThreadFactory.Bulider priority(int proirity){
            this.proirity = proirity;
            return this;
        }

        public MqServerThreadFactory.Bulider daemonFlag(boolean daemon){
            this.daemonFlag = daemon;
            return this;
        }

        public MqServerThreadFactory.Bulider uncaughtExceptionHandler(Thread.UncaughtExceptionHandler caught){
            Objects.requireNonNull(caught,"Uncaught exception handler must not be null!");
            this.uncaughtExceptionHandler = caught;
            return this;
        }

        public void reset(){
            this.wrappedFactory = null;
            this.uncaughtExceptionHandler = null;
            this.namingPattern = null;
            this.daemonFlag = null;
            this.proirity = null;
        }

        public MqServerThreadFactory bulid(){
            MqServerThreadFactory factory = new MqServerThreadFactory(this);
            this.reset();
            return  factory;
        }
    }

    @Override public Thread newThread(Runnable r) {
        Thread thread = this.wrappedFactory.newThread(r);
        initializeThread(thread);
        return thread;
    }

    private void initializeThread(Thread thread){
        if(!Objects.isNull(this.namingPattern)){
            thread.setName(String.format(this.namingPattern,this.threadCounter.getAndIncrement()));
        }

        if(!Objects.isNull(this.uncaughtExceptionHandler)){
            thread.setUncaughtExceptionHandler(this.uncaughtExceptionHandler);
        }

        if(!Objects.isNull(this.priority)){
            thread.setPriority(this.priority);
        }

        if(!Objects.isNull(this.daemonFlag)){
            thread.setDaemon(this.daemonFlag);
        }
    }
}

package com.airbus.archivemanager.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.jhipster.config.JHipsterDefaults.Async;

public final class ExecutorServiceTransfer {

    private static final Logger log = LoggerFactory.getLogger(ExecutorServiceTransfer.class);

    private static final ExecutorServiceTransfer instance = new ExecutorServiceTransfer();

    private ExecutorService executor;

    private ExecutorServiceTransfer() {
        System.out.println("=======================Create a pool of thread");
        // TODO uncomment to use CachedThreadPool instead of FixedThreadPool
        // executor = Executors.newCachedThreadPool(new ThreadFactory() {
        // @Override
        // public Thread newThread(Runnable r) {
        // Thread t = Executors.defaultThreadFactory().newThread(r);
        // return t;
        // }
        // });

        final List<Thread> threads = new ArrayList<Thread>();
        executor = Executors.newFixedThreadPool(Async.maxPoolSize, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setDaemon(true);
                synchronized (threads) {
                    threads.add(t);
                }
                return t;
            }
        });

    }

    /**
     * 
     * @return instance
     */
    public static ExecutorServiceTransfer getInstance() {
        return instance;
    }

    /**
     * 
     * @return the ExecutorService
     */
    public ExecutorService getExecutor() {
        return executor;
    }
}
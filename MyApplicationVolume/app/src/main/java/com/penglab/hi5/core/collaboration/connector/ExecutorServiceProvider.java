package com.penglab.hi5.core.collaboration.connector;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorServiceProvider {
    private static ExecutorService executorService;

    private ExecutorServiceProvider () {}

    public static synchronized ExecutorService getExecutorService() {
        if(executorService == null || executorService.isShutdown()) {
            executorService = Executors.newFixedThreadPool(4);

        }
        return executorService;
    }

    public static synchronized void shutdownService() {
        if(executorService !=null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }


}

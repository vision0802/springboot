package org.vision.github.springboot.mqserver.util;

import org.vision.github.springboot.mqserver.thread.runnable.QueueHandler;
import org.vision.github.springboot.mqserver.thread.threadpool.MqServerThreadPool;

import java.util.concurrent.ExecutorService;

/**
 * @author ganminghui
 * @date 2018/1/3
 */
public class HttpThreadManagerUtil {
    private static final int CORE_THREAD_SIZE = 20;
    private static final int MAX_THREAD_SIZE = 20;
    private static final int MAX_TASK = 1000;
    private static final String POOL_NAME = "queue_handler-%d";
    private static ExecutorService pool = MqServerThreadPool.createThreadPoolExecutor(CORE_THREAD_SIZE, MAX_THREAD_SIZE, MAX_TASK, POOL_NAME);
    private static boolean isStart = false;

    public static void startThread(){
        if(!isStart){
            for (int i = 0; i < MAX_THREAD_SIZE; ++i) {
                pool.execute(new QueueHandler());
            }
            isStart = true;
        }
    }
}
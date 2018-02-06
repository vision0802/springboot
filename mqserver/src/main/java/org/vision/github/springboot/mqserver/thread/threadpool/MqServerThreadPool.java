package org.vision.github.springboot.mqserver.thread.threadpool;

import org.vision.github.springboot.mqserver.thread.threadfactory.MqServerThreadFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author ganminghui
 * @date 2018/1/2
 */
public class MqServerThreadPool {
    private static Integer defaultCoreThreadSize = 2;
    private static Integer defaultMaxThreadSize = 10;
    private static Integer defaultKeepAliveTime = 60;
    private static Integer defaultMaxTask = 500;

    private MqServerThreadPool(){}

    public static ThreadPoolExecutor createThreadPoolExecutoe(String pattern){
        return createThreadPoolExecutor(defaultCoreThreadSize,defaultMaxThreadSize,defaultMaxTask,pattern);
    }

    public static ThreadPoolExecutor createThreadPoolExecutor(int coreThreadSize, int maxThreadSize, int maxTask, String pattern){
        return createThreadPoolExecutor(coreThreadSize,maxThreadSize,defaultKeepAliveTime,TimeUnit.SECONDS, new LinkedBlockingDeque<>(maxTask),pattern);
    }

    private static ThreadPoolExecutor createThreadPoolExecutor(int coreThreadSize, int maxThreadSize, long keepAliveTime, TimeUnit timeUtil, BlockingQueue<Runnable> queue, String pattern){
        return new ThreadPoolExecutor(coreThreadSize,maxThreadSize,keepAliveTime,timeUtil,queue,new MqServerThreadFactory.Bulider().namingPattern(pattern).bulid());
    }
}
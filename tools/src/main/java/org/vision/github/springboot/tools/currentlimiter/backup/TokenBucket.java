package org.vision.github.springboot.tools.currentlimiter.backup;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.vision.github.springboot.tools.time.TimeTool;

import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 基于令牌桶限流方案
 * 表示单位时间产生限定的令牌
 * @author ganminghui
 * @date 2018/1/7
 */
@Deprecated public class TokenBucket {
    private static final ScheduledThreadPoolExecutor POOL = new ScheduledThreadPoolExecutor(1,new BasicThreadFactory.Builder().namingPattern("scheduled-%d").build());

    private static final Integer COUNT_THRESHOLD = 1000;

    private static final Integer FREQUENCY = 5;

    private static final ArrayBlockingQueue<Long> BUCKET_QUEUE = new ArrayBlockingQueue(COUNT_THRESHOLD);

    static {
        POOL.scheduleAtFixedRate(new TokenTask(),-1,FREQUENCY,TimeUnit.SECONDS);
    }

    static class TokenTask implements Runnable{
        @Override public void run() {
            BUCKET_QUEUE.clear();
            while (BUCKET_QUEUE.size()<COUNT_THRESHOLD){
                BUCKET_QUEUE.offer(TimeTool.getCurrentMillis());
            }
        }
    }

    public static boolean getTokenBucket(){
        boolean flag = !Objects.isNull(BUCKET_QUEUE.poll());
        if(flag){
            System.out.println(String.format("令牌桶中还剩余令牌数为:%d",BUCKET_QUEUE.size()));
        }
        return flag;
    }
}
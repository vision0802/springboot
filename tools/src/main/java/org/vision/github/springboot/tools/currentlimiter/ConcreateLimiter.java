package org.vision.github.springboot.tools.currentlimiter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.vision.github.springboot.tools.common.DateTool;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author ganminghui
 * @date 2018/1/8
 */
public class ConcreateLimiter {
    /**
     * 计数器限流器
     * a. 对后一个元素清零,如果是零,也要清零,更新清零时间.
     * b. 检查当前计数器不为0时清零时间是不是上一秒,不是则当前清零
     * c. 判断计数器是否达到阀值,并打印前3s的请求量
     */
    @Slf4j protected static class CounterLimiter implements ICurrLimiter{

        public CounterLimiter(){ this(DEFAULT_THRESHOLD); }

        public CounterLimiter(int threshold){
            COUNTER_ARR = new AtomicReference[]{ new AtomicReference(new Counter(threshold)),new AtomicReference(new Counter(threshold)),new AtomicReference(new Counter(threshold)), new AtomicReference(new Counter(threshold)),new AtomicReference(new Counter(threshold))};
        }

        /** 计数阀值 */
        private static final int DEFAULT_THRESHOLD = 40000;

        @Getter private static class Counter{
            private long clearSecond = 0L;
            private long number = 0L;
            private int threshold;

            public Counter(int threshold){ this.threshold = threshold; }

            public void clearSecond(long clearSecond,int number){
                this.clearSecond = clearSecond;
                this.number = number;
            }

            public void increment(){ number ++ ; }

            private int getIndexClearSecond(){ return (int)clearSecond % 5; }

            public boolean validate(int beforeIndex){
                /** 当前计数器大于0且清零时间不为上一秒时,当前计数器清零 */
                if(number > 0 && clearSecond > 0 && getIndexClearSecond() != beforeIndex){
                    clearSecond(DateTool.getCurrentSenconds(),0);
                }
                if(number<threshold){
                    increment();
                    return true;
                }
                return false;
            }
        }

        /** 5个原始变量,用来计数每5秒的请求数量 */
        private final AtomicReference<Counter>[] COUNTER_ARR ;

        /** 获取前一秒对应的计数器索引 */
        private int getBeforeIndex(int currIndex){ return currIndex==0? COUNTER_ARR.length-1 : currIndex-1; }

        /** 获取后一秒对应的计数器索引 */
        private int getAfterIndex(int currIndex){ return currIndex==COUNTER_ARR.length-1? 0 : currIndex+1; }

        /** 打印当前以及前3s的访问量 */
        private void printCount(int currIndex){
            List<Integer> rtn = new ArrayList<>();
            int i=0;
            int tmpIndex = currIndex;
            while (i < COUNTER_ARR.length-1){
                rtn.add(tmpIndex<0?COUNTER_ARR.length + tmpIndex : tmpIndex);
                tmpIndex--;
                i++;
            }
            System.out.println(String.format("当前索引:%d,当前以及前3s访问量为:%s",currIndex,rtn.stream().map(index->String.valueOf(COUNTER_ARR[index].get().getNumber())).collect(Collectors.joining(","))));
            log.info("当前索引:{},当前以及前3s访问量为:{}", currIndex,rtn.stream().map(index->String.valueOf(COUNTER_ARR[index].get().getNumber())).collect(Collectors.joining(",")));
        }

        @Override public boolean currentLimited() {
            long currSeconds = DateTool.getCurrentSenconds();
            int currIndex = (int)currSeconds % COUNTER_ARR.length;

            COUNTER_ARR[getAfterIndex(currIndex)].get().clearSecond(currSeconds,0);

            boolean flag = COUNTER_ARR[currIndex].get().validate(getBeforeIndex(currIndex));

            if(flag){ printCount(currIndex); }

            return  flag;
        }
    }

    /**
     * 基于令牌桶限流方案
     * 表示单位时间产生限定的令牌
     */
    @Slf4j protected static class TokenBucketLimiter implements ICurrLimiter{

        private static final ScheduledThreadPoolExecutor POOL = new ScheduledThreadPoolExecutor(1,new BasicThreadFactory.Builder().namingPattern("scheduled-%d").build());

        private static final Integer DEFAULT_THRESHOLD = 1000;

        private static final Integer FREQUENCY = 5;

        private final ArrayBlockingQueue<Long> bucketQueue ;

        public TokenBucketLimiter(){ this(DEFAULT_THRESHOLD); }

        public TokenBucketLimiter(int threshold){
            bucketQueue = new ArrayBlockingQueue(threshold);
            POOL.scheduleAtFixedRate(new TokenTask(bucketQueue,threshold),-1,FREQUENCY, TimeUnit.SECONDS);
        }

        private static class TokenTask implements Runnable{
            private ArrayBlockingQueue<Long> bucketQueue;
            private int threshold;
            public TokenTask(ArrayBlockingQueue<Long> bucketQueue,int threshold){
                this.bucketQueue = bucketQueue;
                this.threshold = threshold;
            }

            @Override public void run() {
                bucketQueue.clear();
                while (bucketQueue.size()<threshold){
                    bucketQueue.offer(DateTool.getCurrentMillis());
                }
            }
        }

        @Override public boolean currentLimited() {
            boolean flag = !Objects.isNull(bucketQueue.poll());
            if(flag){
                log.info("令牌桶中还剩余令牌数为:{}(个)",bucketQueue.size());
                System.out.println(String.format("令牌桶中还剩余令牌数为:%d",bucketQueue.size()));
            }
            return flag;
        }
    }

    /**
     * * 计数器限流器-1
     * a. 对后一个元素清零,如果是零,也要清零,更新清零时间.
     * b. 检查当前计数器不为0时清零时间是不是上一秒,不是则当前清零
     * c. 判断计数器是否达到阀值,并打印前3s的请求量
     */
    @Slf4j protected static class CounterLimiterV1 implements ICurrLimiter{
        /** 计数阀值 */
        private static final Integer COUNT_THRESHOLD = 50;

        private static volatile long clearMillis = DateTool.getCurrentSenconds();

        /** 5个原始变量,用来计数每5秒的请求数量 */
        private static final AtomicLong[] COUNT_ARR = new AtomicLong[]{ new AtomicLong(0L),new AtomicLong(0L), new AtomicLong(0L),new AtomicLong(0L),new AtomicLong(0L)};

        private static void clearSecondCount(AtomicLong secondCount){
            secondCount.set(0);
            clearMillis = DateTool.getCurrentSenconds();
        }

        /** 打印当前以及前3s的访问量 */
        private static void printCount(int currIndex){
            List<Integer> rtn = new ArrayList<>();
            int i=0;
            int tmpIndex = currIndex;
            while (i < COUNT_ARR.length-1){
                rtn.add(tmpIndex<0?COUNT_ARR.length + tmpIndex : tmpIndex);
                tmpIndex--;
                i++;
            }
            System.out.println(String.format("当前索引:%d,当前以及前3s访问量为:%s",currIndex,rtn.stream().map(index->String.valueOf(COUNT_ARR[index])).collect(Collectors.joining(","))));
        }

        /** 获取当前秒对应的计数器索引 */
        private static int getIndex(){
            return (int) DateTool.getCurrentSenconds()%COUNT_ARR.length;
        }

        /** 获取前一秒对应的计数器 */
        private static int getBeforeIndex(int currIndex){
            return currIndex==0? COUNT_ARR.length-1 : currIndex-1;
        }

        /** 获取后一秒对应的计数器 */
        private static int getAfterIndex(int currIndex){
            return currIndex==COUNT_ARR.length-1? 0 : currIndex+1;
        }

        /** 判断计数器是否达到阀值 */
        private static boolean isLimited(AtomicLong currSecondCount){
            if(currSecondCount.get()<COUNT_THRESHOLD){
                currSecondCount.incrementAndGet();
                return true;
            }else {
                return false;
            }
        }

        @Override public boolean currentLimited() {
            int currentIndex = getIndex();

            /** 清空后一秒的计数器 */
            clearSecondCount(COUNT_ARR[getAfterIndex(currentIndex)]);

            return isLimited(COUNT_ARR[currentIndex]);
        }
    }

    @AllArgsConstructor @Getter enum LimiterType {
        counterLimiter("counterLimiter",1),
        tokenBucketLimiter("tokenBucketLimiter",2);
        private String name;
        private Integer value;
    }

}
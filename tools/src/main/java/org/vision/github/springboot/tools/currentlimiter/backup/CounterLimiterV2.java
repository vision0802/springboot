package org.vision.github.springboot.tools.currentlimiter.backup;

import lombok.Getter;
import org.vision.github.springboot.tools.common.DateTool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 计数器限流器
 * a. 对后一个元素清零,如果是零,也要清零,更新清零时间.
 * b. 检查当前计数器不为0时清零时间是不是上一秒,不是则当前清零
 * c. 判断计数器是否达到阀值,并打印前3s的请求量
 * @author ganminghui
 * @date 2018/1/7
 */
@Deprecated public class CounterLimiterV2 {
    /** 计数阀值 */
    private static final Integer COUNT_THRESHOLD = 40000;

    @Getter private static class Counter{
        private long clearSecond = 0L;
        private long number = 0L;

        public void clearSecond(long clearSecond,int number){
            this.clearSecond = clearSecond;
            this.number = number;
        }

        public void increment(){ number ++ ; }

        private int getIndexClearSecond(){
            return (int)clearSecond % COUNTER_ARR.length;
        }

        public boolean validate(int beforeIndex){
            /** 当前计数器大于0且清零时间不为上一秒时,当前计数器清零 */
            if(number > 0 && clearSecond > 0 && getIndexClearSecond() != beforeIndex){
                clearSecond(DateTool.getCurrentSenconds(),0);
            }
            if(number<COUNT_THRESHOLD){
                increment();
                return true;
            }
            return false;
        }
    }

    /** 5个原始变量,用来计数每5秒的请求数量 */
    private static final AtomicReference<Counter>[] COUNTER_ARR = new AtomicReference[]{ new AtomicReference(new Counter()),new AtomicReference(new Counter()),new AtomicReference(new Counter()), new AtomicReference(new Counter()),new AtomicReference(new Counter())};

    /** 获取前一秒对应的计数器索引 */
    private static int getBeforeIndex(int currIndex){
        return currIndex==0? COUNTER_ARR.length-1 : currIndex-1;
    }

    /** 获取后一秒对应的计数器索引 */
    private static int getAfterIndex(int currIndex){
        return currIndex==COUNTER_ARR.length-1? 0 : currIndex+1;
    }

    /** 打印当前以及前3s的访问量 */
    private static void printNumber(int currIndex){
        List<Integer> rtn = new ArrayList<>();
        int i=0;
        int tmpIndex = currIndex;
        while (i < COUNTER_ARR.length-1){
            rtn.add(tmpIndex<0?COUNTER_ARR.length + tmpIndex : tmpIndex);
            tmpIndex--;
            i++;
        }
        System.out.println(String.format("当前索引:%d,当前以及前3s访问量为:%s",currIndex,rtn.stream().map(index->String.valueOf(COUNTER_ARR[index].get().getNumber())).collect(Collectors.joining(","))));
    }

    public static boolean countLimited(){
        long currSeconds = DateTool.getCurrentSenconds();
        int currIndex = (int)currSeconds % COUNTER_ARR.length;

        COUNTER_ARR[getAfterIndex(currIndex)].get().clearSecond(currSeconds,0);

        boolean flag = COUNTER_ARR[currIndex].get().validate(getBeforeIndex(currIndex));

        if(flag){ printNumber(currIndex); }

        return  flag;
    }
}
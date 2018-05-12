package org.vision.github.springboot.tools.currentlimiter.backup;

import org.vision.github.springboot.tools.common.DateTool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 计数器限流器
 * a. 对后一个元素清零,如果是零,也要清零,更新清零时间.
 * b. 检查当前计数器不为0时清零时间是不是上一秒,不是则当前清零
 * c. 判断计数器是否达到阀值,并打印前3s的请求量
 * @author ganminghui
 * @date 2018/1/7
 */
@Deprecated public class CounterLimiter {
    /** 计数阀值 */
    private static final Integer COUNT_THRESHOLD = 50;

    private static volatile long clearMillis = DateTool.getCurrentSeconds();

    /** 5个原始变量,用来计数每5秒的请求数量 */
    private static final AtomicLong[] COUNT_ARR = new AtomicLong[]{ new AtomicLong(0L),new AtomicLong(0L), new AtomicLong(0L),new AtomicLong(0L),new AtomicLong(0L)};

    private static void clearSecondCount(AtomicLong secondCount){
        secondCount.set(0);
        clearMillis = DateTool.getCurrentSeconds();
    }

    public static boolean countLimit(){
        int currentIndex = getIndex();

        /** 清空后一秒的计数器 */
        clearSecondCount(COUNT_ARR[getAfterIndex(currentIndex)]);

        /** 检查当前计数器不为0时清零时间是否是上一秒 */
        //checkCurrSecondCount(COUNT_ARR[currentIndex],currentIndex);

        boolean rtn = isLimited(COUNT_ARR[currentIndex]);

        if(rtn){ printCount(currentIndex); }
        return rtn;
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
        return (int) DateTool.getCurrentSeconds()%COUNT_ARR.length;
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
}
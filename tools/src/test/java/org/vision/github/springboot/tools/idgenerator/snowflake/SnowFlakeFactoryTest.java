package org.vision.github.springboot.tools.idgenerator.snowflake;

import org.junit.Test;

import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * @author ganminghui
 * @date 2018/1/5
 */
public class SnowFlakeFactoryTest {

    public void productId(int datacenterId,int workerId,int n){
        SnowFlakeFactory flakeFactory1 = new SnowFlakeFactory(workerId,datacenterId);
        SnowFlakeFactory flakeFactory2 = new SnowFlakeFactory(workerId+1,datacenterId);

        Set<Long> setOne = new HashSet<>();
        Set<Long> setTwo = new HashSet<>();

        long start = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            setOne.add(flakeFactory1.nextId());
        }
        long end1 = System.currentTimeMillis() - start;
        System.out.println(String.format("第一批ID预计生成%d个,实际生成%d个,共耗时%d毫秒.",n,setOne.size(),end1));

        for (int i = 0; i < n; i++) {
            setTwo.add(flakeFactory2.nextId());
        }
        long end2 = System.currentTimeMillis() -start;
        System.out.println(String.format("第二批ID预计生成%d个,实际生成%d个,共耗时%d毫秒.",n,setTwo.size(),end2));

        System.out.println(String.format("合计一共生成了%d个ID.",setOne.size() + setTwo.size()));
    }

    public void productIdByMoreThread(int datacenterId,int workId,int n) throws InterruptedException {
        Set<Long> setAll = new HashSet<>();
        CountDownLatch countDownLatch = new CountDownLatch(10);

        long start = System.currentTimeMillis();
        int threadNo = datacenterId;

        List<SnowFlakeFactory> flakeFactorys = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            flakeFactorys.add(new SnowFlakeFactory(workId,threadNo++));
        }

        flakeFactorys.forEach(factory-> new Thread(()->{
            Set<Long> setId = new HashSet<>();
            for (int i = 0; i < n; i++) {
                setId.add(factory.nextId());
            }
            synchronized (setAll){
                setAll.addAll(setId);
                System.out.println(String.format("预计产生了%d个id,实际产生了%d个id,并成功加入到setAll中.",n,setId.size()));
            }
            countDownLatch.countDown();
        }).start());

        countDownLatch.await();

        long end = System.currentTimeMillis() - start;
        System.out.println(String.format("共耗时:%d毫秒,预期产生%d个id,实际产生%d个id.",end,n*10,setAll.size()));
    }

    public void productIdInOneSecond(){
        SnowFlakeFactory flakeFactory = new SnowFlakeFactory(1,2);
        long start = System.currentTimeMillis();
        int count = 0;
        for (int i = 0; System.currentTimeMillis() - start < 1000; i++,count=i) {
            flakeFactory.nextId();
        }
        long end = System.currentTimeMillis() - start;
        System.out.println(String.format("%d毫秒内一共产生了%d个ID.",end,count));
    }

    /** 测试一秒可以生成多少个ID */
    @Test public void testProductIdInOneSecond(){
        productIdInOneSecond();
    }

    /** 测试10个线程10w个ID实际产出率 */
    @Test public void testProductIdByMoreThread() throws InterruptedException {
        productIdByMoreThread(1,2,10000);
    }

    /** 测试2次产生1w个ID对比 */
    @Test public void testProductId(){
        productId(1,2,10000);
    }

    /** 生成一个ID */
    @Test public void testGengerateOneId(){
        System.out.println(new SnowFlakeFactory(1,2).nextId());
    }

    /** 生成ID与反解ID */
    @Test public void generateAndConverId(){
        SnowFlakeFactory flakeFactory = new SnowFlakeFactory(2,5);
        long id = flakeFactory.nextId();
        System.out.println("id: "+id);
        Map<String,Long> rtnMap = SnowFlakeFactory.converId(id);
        rtnMap.forEach((key,value)-> System.out.println(key+": " +value));
    }
}
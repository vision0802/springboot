package org.vision.github.springboot.tools.currentlimiter;

import org.junit.Test;
import org.vision.github.springboot.tools.common.DateTool;

import java.util.concurrent.TimeUnit;

/**
 * @author ganminghui
 * @date 2018/1/8
 */
public class CurrLimiterServiceTest {

    @Test public void testGetCurrLimiter() throws InterruptedException {
        ICurrLimiter currLimiter = new CurrLimiterService(2).getCurrLimiter();
        long currSecond = DateTool.getCurrentSeconds();

        Thread t1 = new Thread(()->{
            while (DateTool.getCurrentSeconds() < currSecond + 8){
                currLimiter.currentLimited();
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread t2 = new Thread(()->{
            while (DateTool.getCurrentSeconds() < currSecond + 6){
                currLimiter.currentLimited();
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();
        /*while (DateTool.getCurrentSenconds() < currSecond + 6) {
            currLimiter.currentLimited();
        }*/
    }
}
package org.vision.github.springboot.tools.currentlimiter;

import org.junit.Test;
import org.vision.github.springboot.tools.time.TimeTool;

/**
 * @author ganminghui
 * @date 2018/1/7
 */
public class CounterLimiterTest {

    @Test public void testCountLimit() throws Exception {
        long currSecond = TimeTool.getCurrentSenconds();
        while (TimeTool.getCurrentSenconds() < currSecond + 6) {
            CounterLimiter.countLimit();
        }
    }
}
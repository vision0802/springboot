package org.vision.github.springboot.tools.currentlimiter;

import org.junit.Test;
import org.vision.github.springboot.tools.currentlimiter.backup.CounterLimiter;
import org.vision.github.springboot.tools.common.DateTool;

/**
 * @author ganminghui
 * @date 2018/1/7
 */
public class CounterLimiterTest {

    @Test public void testCountLimit() throws Exception {
        long currSecond = DateTool.getCurrentSeconds();
        while (DateTool.getCurrentSeconds() < currSecond + 6) {
            CounterLimiter.countLimit();
        }
    }
}
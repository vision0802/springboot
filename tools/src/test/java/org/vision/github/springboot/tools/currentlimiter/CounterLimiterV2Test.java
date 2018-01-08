package org.vision.github.springboot.tools.currentlimiter;

import org.junit.Test;
import org.vision.github.springboot.tools.currentlimiter.backup.CounterLimiterV2;
import org.vision.github.springboot.tools.time.TimeTool;

/**
 * @author ganminghui
 * @date 2018/1/7
 */
public class CounterLimiterV2Test {

    @Test public void testCountLimited() throws Exception {
        long currSecond = TimeTool.getCurrentSenconds();
        while (TimeTool.getCurrentSenconds() < currSecond + 6) {
            CounterLimiterV2.countLimited();
        }
    }
}
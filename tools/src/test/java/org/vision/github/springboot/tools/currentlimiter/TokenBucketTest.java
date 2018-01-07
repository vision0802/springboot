package org.vision.github.springboot.tools.currentlimiter;

import org.junit.Test;
import org.vision.github.springboot.tools.time.TimeTool;

import static org.junit.Assert.*;

/**
 * @author ganminghui
 * @date 2018/1/7
 */
public class TokenBucketTest {
    @Test
    public void testGetTokenBucket() throws Exception {
        long currSecond = TimeTool.getCurrentSenconds();
        while (TimeTool.getCurrentSenconds() < currSecond + 30) {
            TokenBucket.getTokenBucket();
        }
    }
}
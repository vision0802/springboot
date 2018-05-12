package org.vision.github.springboot.tools.currentlimiter;

import org.junit.Test;
import org.vision.github.springboot.tools.currentlimiter.backup.TokenBucket;
import org.vision.github.springboot.tools.common.DateTool;

/**
 * @author ganminghui
 * @date 2018/1/7
 */
public class TokenBucketTest {
    @Test
    public void testGetTokenBucket() throws Exception {
        long currSecond = DateTool.getCurrentSeconds();
        while (DateTool.getCurrentSeconds() < currSecond + 6) {
            TokenBucket.getTokenBucket();
        }
    }
}
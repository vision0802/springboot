package org.vision.github.springboot.mqserver.config;

import lombok.NoArgsConstructor;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ganminghui
 * @date 2017/12/20
 */
@NoArgsConstructor public class HttpRequestCount {
    private static AtomicInteger count = new AtomicInteger(0);
    private static final Integer MAX_REQUEST = 5000;

    public static boolean isOverMaxRequest(){ return count.get() >= MAX_REQUEST; }

    public static int increment(){ return count.incrementAndGet(); }

    public static int decrement(){ return count.decrementAndGet(); }

    public static int get(){ return count.get(); }

    public static void reset(){ count.set(0); }
}
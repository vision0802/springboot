package org.vision.github.springboot.tools.time;

import java.time.ZoneId;

/**
 * @author ganminghui
 * @date 2018/1/7
 */
public class TimeTool {
    private static final ZoneId ZONEID = ZoneId.of(ZoneId.SHORT_IDS.get("CTT"));

    private static final String FORMAT_MILLI = "yyyy-MM-dd HH:mm:ss.SSS";

    /** 获取当前时间毫秒数 */
    public static long getCurrentMillis(){
        return System.currentTimeMillis();
    }

    /** 获取当前时间秒数 */
    public static long getCurrentSenconds(){
        return getCurrentMillis()/1000;
    }
}
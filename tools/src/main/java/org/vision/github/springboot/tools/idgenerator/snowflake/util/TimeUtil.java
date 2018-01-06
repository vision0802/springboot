package org.vision.github.springboot.tools.idgenerator.snowflake.util;

import org.vision.github.springboot.tools.idgenerator.snowflake.bean.IDStructure;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @author ganminghui
 * @date 2018/1/6
 */
public class TimeUtil {
    private static final ZoneId ZONEID = ZoneId.of(ZoneId.SHORT_IDS.get("CTT"));

    private static final String FORMAT_MILLI = "yyyy-MM-dd HH:mm:ss.SSS";

    public static long timeGenerate(){ return System.currentTimeMillis(); }

    /** 如果参数小于当前时间,返回当前时间
     *  如果参数不小于当前时间,返回比参数值最接近的时间
     * */
    public static long tillNextMills(long lastTimestamp){
        long timestamp = timeGenerate();
        while (lastTimestamp > timestamp){
            timestamp = timeGenerate();
        }
        return timestamp;
    }

    public static void timestampAndSequenceGenerate(long sequence, long lastTimestamp, IDStructure idStructure){
        long timestamp = timeGenerate();
        if(timestamp < lastTimestamp){
            throw new RuntimeException(String.format("Clock moved backwards. Refusing to generate id for %d milliseconds.",lastTimestamp - timestamp));
        }
        /** 同一毫秒sequence递增直到MAX_SEQUENCEID,重新生成新的毫秒 */
        if(lastTimestamp == timestamp){
            sequence = (sequence+1) & idStructure.getMaxSequence() ;
            if(sequence == 0L){
                timestamp = tillNextMills(timestamp);
            }
        }else {
            sequence = 0L;
        }
        idStructure.setSequence(sequence);
        idStructure.setTimestamp(timestamp);
    }

    public static void validateTimestamp(long lastTimestamp, long timestamp) {
        if (timestamp < lastTimestamp) {
            throw new IllegalStateException(String.format( "Clock moved backwards.  Refusing to generate id for %d second/milisecond.",  lastTimestamp - timestamp));
        }
    }

    public static LocalDateTime parseEpochMilli(long milli){
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(milli),ZONEID);
    }

    public static String formatLocalDateTime(LocalDateTime localDateTime){
      return localDateTime.format(DateTimeFormatter.ofPattern(FORMAT_MILLI));
    }

    public static String formatMillis(long millis){
        return formatLocalDateTime(parseEpochMilli(millis));
    }
}
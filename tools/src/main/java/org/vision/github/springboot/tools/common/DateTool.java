package org.vision.github.springboot.tools.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

/**
 * @author ganminghui
 * @date 2018/1/7
 */
public class DateTool {
    private static final String ZONE_CHINA = "Asia/Shanghai";
    private static final String PARRTEN_MILLI = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final String PARRTEN_SECOND = "yyyy-MM-dd HH:mm:ss";

    /** 获取当前时间毫秒数 */
    public static long getCurrentMillis(){ return System.currentTimeMillis(); }

    /** 获取当前时间秒数 */
    public static long getCurrentSeconds(){ return getCurrentMillis()/1000; }

    /** 指定时间格式获取当前时间字符串 */
    public static String getTimeNow(String pattern){
        pattern = Optional.ofNullable(pattern).orElseGet(()->PARRTEN_MILLI);
        return LocalDateTime.now(ZoneId.of(ZONE_CHINA)).format(DateTimeFormatter.ofPattern(pattern));
    }

    /** 指定时间格式获取指定时间字符串 */
    public static String getTime(LocalDateTime localDateTime,String pattern){
        return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /** 获取当前小时数 */
    public static int getCurrentHour(){
        return LocalDateTime.now(ZoneId.of(ZONE_CHINA)).getHour();
    }

    /** 获取指定时间距离当前时间的小时数 */
    public static long getUntilHour(LocalDateTime futureTime){
        return LocalDateTime.now(ZoneId.of(ZONE_CHINA)).until(futureTime, ChronoUnit.HOURS);
    }

    /** 判断当前时间是否在指定时间区别范围之内 */
    public static boolean isOuter(LocalDateTime startTime,LocalDateTime endTime){
        return LocalDateTime.now(ZoneId.of(ZONE_CHINA)).isBefore(startTime) || LocalDateTime.now(ZoneId.of(ZONE_CHINA)).isAfter(endTime);
    }

    /** 指定字符串解析为时间对象 */
    public static LocalDateTime parseToDateTime(String timeStr){
        return parseToDateTime(timeStr,PARRTEN_MILLI);
    }

    /** 指定字符串解析为指定格式的时间对象 */
    public static LocalDateTime parseToDateTime(String timeStr,String pattern){
        return LocalDateTime.parse(timeStr,DateTimeFormatter.ofPattern(pattern));
    }

    /** localdateTime转化为date */
    public static Date localDateTimeToDate(LocalDateTime time){
        return Date.from(time.atZone(ZoneId.of(ZONE_CHINA)).toInstant());
    }

    /** date转化为localdateTime */
    public static LocalDateTime dateToLocalDateTime(Date date){
        return LocalDateTime.ofInstant(date.toInstant(),ZoneId.of(ZONE_CHINA));
    }

    /** date 转化为localdate */
    public static LocalDate dateToLocalDate(Date date){
        return date.toInstant().atZone(ZoneId.of(ZONE_CHINA)).toLocalDate();
    }

    /** 指定日期上追加指定分钟 */
    public static Date addMinutes(Date date,int minutes){
        return localDateTimeToDate(dateToLocalDateTime(date).plusMinutes(minutes));
    }

    /** 获取指定日期这一天的开始时间 */
    public static LocalDateTime getStartOfDay(Date date){
        date = Optional.ofNullable(date).orElse(new Date());
        return dateToLocalDate(date).atStartOfDay(ZoneId.of(ZONE_CHINA)).toLocalDateTime();
    }

    /** 获取指定日期这一天的结束时间 */
    public static LocalDateTime getEndOfDay(Date date){
        return getStartOfDay(date).plusDays(1).minusSeconds(1);
    }
}
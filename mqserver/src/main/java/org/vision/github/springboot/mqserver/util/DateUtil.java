package org.vision.github.springboot.mqserver.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author ganminghui
 * @date 2017/12/29
 */
public class DateUtil {
    private static final ZoneId ZONEID = ZoneId.of(ZoneId.SHORT_IDS.get("CTT"));

    public static LocalDateTime getTimeNow(){
        return LocalDateTime.now(ZONEID);
    }

    public static LocalDateTime addMinutes(int minutes){
        return getTimeNow().plusMinutes(minutes);
    }

    public static Date localDateTimeToDate(LocalDateTime time){
        return Date.from(time.atZone(ZONEID).toInstant());
    }

    public static LocalDateTime dateToLocalDateTime(Date date){
        return LocalDateTime.ofInstant(date.toInstant(),ZONEID);
    }
    public static Date addMinutes(Date date,int minutes){
        return localDateTimeToDate(dateToLocalDateTime(date).plusMinutes(minutes));
    }
}
package org.vision.github.springboot.mqserver.util;

import java.util.Date;

import static org.vision.github.springboot.mqserver.constant.Globals.IMessageDeliverStatus.STATUS_ERROR;
import static org.vision.github.springboot.mqserver.constant.Globals.IMessageDeliverStatus.STATUS_WAIT_RETRY;

/**
 * @author ganminghui
 * @date 2018/1/2
 */
public class MqServerUtil {

    public static Date getRetryTimes(Date date,int hasTryTimes){
        int delayMin = 5 + (int)Math.pow(3, hasTryTimes);
        return DateUtil.addMinutes(date,delayMin);
    }

    public static int getMessageDeliverStatus(int retryTimes,int hasTryTimes){
        return hasTryTimes >= retryTimes ? STATUS_ERROR : STATUS_WAIT_RETRY ;
    }
}
package org.vision.github.springboot.mqserver.util;

import org.vision.github.springboot.mqserver.constant.Globals;
import org.vision.github.springboot.mqserver.dto.HttpRequestData;
import org.vision.github.springboot.mqserver.pojo.MessageConsumer;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author ganminghui
 * @date 2018/1/3
 */
public class QueueUtil {
    private static BlockingQueue<HttpRequestData> queue = new ArrayBlockingQueue<>(5000);

    public static boolean offer(HttpRequestData requestData) throws InterruptedException {
        return queue.offer(requestData, 2L, TimeUnit.SECONDS);
    }

    public static void addForWait(HttpRequestData requestData) throws InterruptedException {
        queue.put(requestData);
    }

    public static HttpRequestData get() throws InterruptedException {
        return queue.take();
    }

    public static HttpRequestData createHttpRequestData(MessageConsumer consumer,String msgId,String text){
        return new HttpRequestData(
                consumer.getConsumerUrl(),consumer.getConsumerName(),consumer.getUrlParams(),consumer.getRequestMethod(),
                consumer.getRequestEncoding(),consumer.getCallType(),msgId,text,
                consumer.getRetryTimes(),0, Globals.IMessageDeliverStatus.STATUS_CREATE,
                consumer.getConsumerUrl(),consumer.getVersion()
        );
    }
}
package org.vision.github.springboot.mqclient.context;

import lombok.extern.slf4j.Slf4j;
import org.vision.github.springboot.mqclient.domain.MqMessage;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;

/**
 * 队列上下文对象
 * @author ganminghui
 * @date 2017/12/20
 * <p> lombok 中的@SLF4J需要和slf-api结合使用
 */
@Slf4j
public class QueueContext {
    private volatile boolean destroy = false;
    private volatile boolean terminated = false;
    private ExecutorService executorService = null;
    private Integer queueMaxCount;
    private static final Integer threadNum = 3;
    private ArrayBlockingQueue<MqMessage> cacheQueue = null;

    public QueueContext(){}

}
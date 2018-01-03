package org.vision.github.springboot.mqserver.util;

import org.vision.github.springboot.mqserver.thread.runnable.MessageListenerHandler;
import org.vision.github.springboot.mqserver.thread.threadpool.MqServerThreadPool;

import javax.jms.Connection;
import java.util.concurrent.ExecutorService;

/**
 * @author ganminghui
 * @date 2018/1/3
 */
public class MessageHelperUtil {
    private static int coreThreadSize = 1;
    private static int maxThreadSize = 1;
    private static int maxTask = 60;
    private static String poolName = "message-listener";
    private static ExecutorService pools = MqServerThreadPool.createThreadPoolExecutor(1, 1, 60, "message-listener-%d");

    public static void createQueueAndListener(Connection connection, String destination) {
        pools.execute(new MessageListenerHandler(connection, destination));
    }
}

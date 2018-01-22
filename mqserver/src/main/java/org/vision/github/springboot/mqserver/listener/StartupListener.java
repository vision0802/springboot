package org.vision.github.springboot.mqserver.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.vision.github.springboot.mqserver.pojo.Server;
import org.vision.github.springboot.mqserver.service.MessageConsumerService;
import org.vision.github.springboot.mqserver.service.ServerService;
import org.vision.github.springboot.mqserver.util.AppUtil;
import org.vision.github.springboot.mqserver.util.HttpThreadManagerUtil;
import org.vision.github.springboot.mqserver.util.MessageHelperUtil;
import org.vision.github.springboot.mqserver.util.SpringUtil;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import java.util.Date;
import java.util.List;

/**
 * @author ganminghui
 * @date 2017/12/21
 */
@Component @Slf4j
public class StartupListener implements ApplicationListener<ContextRefreshedEvent> {
    @Override public void onApplicationEvent(ContextRefreshedEvent event) {
        SpringUtil.setContext(event.getApplicationContext());

        AppUtil.init();

        SpringUtil.getBean(ServerService.class).addServer(new Server(AppUtil.getUniqueName(),new Date()));
        log.info("add server success...");

        MessageConsumerService consumerService = SpringUtil.getBean(MessageConsumerService.class);
        List<String> enableConsumerQueueName = consumerService.getEnableConsumerQueueName();
        log.info("need listener queue number:{}",enableConsumerQueueName.size());

        ConnectionFactory factory = SpringUtil.getBean(PooledConnectionFactory.class);
        try {
            for (String queueName: enableConsumerQueueName) {
                MessageHelperUtil.createQueueAndListener(factory.createConnection(),queueName);
                log.info("******* 创建队列监听,queueName:{} *******", queueName);
            }

            MessageHelperUtil.createQueueAndListener(factory.createConnection(), "ActiveMQ.DLQ");
            log.info("******* 创建死信队列监听成功 *******");
        } catch (JMSException e) {
            log.error("无法创建connection连接", e);
            throw new RuntimeException("startListener error,无法创建connection连接");
        }

        HttpThreadManagerUtil.startThread();
        log.info("******* httpThreads启动成功 *******");
    }
}
package org.vision.github.springboot.mqserver.thread.runnable;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jms.JmsProperties;
import org.vision.github.springboot.mqserver.listener.RequestMessageListener;

import javax.jms.Connection;
import javax.jms.MessageConsumer;
import javax.jms.Session;

/**
 * @author ganminghui
 * @date 2018/1/2
 */
@Slf4j @AllArgsConstructor
public class MessageListenerHandler implements Runnable {
    private Connection connection;
    private String destination ;

    @Override public void run() {
        /** 创建queue,创建consumer,添加messageListener */
        try {
            connection.setExceptionListener((ex)-> log.error("JMS listener exception message!!!",ex));

            connection.start();
            Session session = connection.createSession(false, JmsProperties.AcknowledgeMode.CLIENT.getMode());
            MessageConsumer consumer = session.createConsumer(session.createQueue(this.destination));
            consumer.setMessageListener(new RequestMessageListener());
        }catch (Exception e){
            log.error("messageHandler error!",e);
        }
    }
}
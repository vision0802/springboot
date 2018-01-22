package org.vision.github.springboot.mqserver.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.command.ActiveMQMessage;
import org.springframework.dao.DuplicateKeyException;
import org.vision.github.springboot.mqserver.pojo.MessageConsumer;
import org.vision.github.springboot.mqserver.service.MessageConsumerService;
import org.vision.github.springboot.mqserver.service.MessageService;
import org.vision.github.springboot.mqserver.util.QueueUtil;
import org.vision.github.springboot.mqserver.util.SpringUtil;

import javax.jms.*;
import java.util.Date;
import java.util.List;

/**
 * @author ganminghui
 * @date 2018/1/2
 */
@Slf4j public class RequestMessageListener implements MessageListener {
    private MessageConsumerService consumerService;
    private MessageService messageService;

    private static final String QUEUE = "ActiveMQ.DLQ";
    public RequestMessageListener(){
        consumerService = SpringUtil.getBean(MessageConsumerService.class);
        messageService = SpringUtil.getBean(MessageService.class);
    }

    @Override public void onMessage(Message message) {
        try {
            TextMessage textMessage = (TextMessage)message;
            String msgId = textMessage.getJMSMessageID();
            String text = textMessage.getText();
            String queueName = getRealQueueName(message);
            List<MessageConsumer> messageConsumers = consumerService.getByQueueName(queueName);

            org.vision.github.springboot.mqserver.pojo.Message msg = new org.vision.github.springboot.mqserver.pojo.Message(queueName,msgId,text,new Date());
            try{
                if(messageConsumers.isEmpty()){
                    messageService.addMessage(msg);
                }else {
                    messageService.addMessage(msg,messageConsumers);

                    for (MessageConsumer consumer: messageConsumers) {
                        QueueUtil.offer(QueueUtil.createHttpRequestData(consumer,msgId,text));
                    }
                }
            }catch (DuplicateKeyException e){
                String errorMsg = e.getMessage();
                if(errorMsg.contains("MySQLIntegrityConstraintViolationException")){
                    log.warn("addMqMessage error by DuplicateKeyException,so,message acknowledge will to do!");
                }else {
                    log.error("addMqMessage error,exception is DuplicateKeyException!",e);
                }
            }
            message.acknowledge();
        }catch (Exception e){
            log.error("onMessage error!",e);
            throw new RuntimeException("onMessage error!",e);
        }
    }

    private String getRealQueueName(Message message) throws JMSException {
        ActiveMQMessage activeMQMessage = (ActiveMQMessage)message;
        Queue queue = (Queue) activeMQMessage.getJMSDestination();
        if (QUEUE.equals(queue.getQueueName())) {
            queue = (Queue) activeMQMessage.getOriginalDestination();
            log.error("receive ActiveMQ.DLQ queue message,source queue name:{}",queue.getQueueName());
        }
        return queue.getQueueName();
    }
}
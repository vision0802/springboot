package org.vision.github.springboot.mqserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vision.github.springboot.mqserver.dao.MessageDao;
import org.vision.github.springboot.mqserver.dao.MessageDeliverDao;
import org.vision.github.springboot.mqserver.pojo.Message;
import org.vision.github.springboot.mqserver.pojo.MessageConsumer;
import org.vision.github.springboot.mqserver.pojo.MessageDeliver;
import org.vision.github.springboot.mqserver.util.MqServerUtil;

import java.util.Date;
import java.util.List;
import java.util.function.Function;

/**
 * @author ganminghui
 * @date 2018/1/2
 */
@Service public class MessageService {
    @Autowired private MessageDao messageDao;
    @Autowired private MessageDeliverDao deliverDao;

    @Transactional(rollbackFor = Exception.class)
    public void addMessage(Message message, List<MessageConsumer> consumers){
        /** 新增message记录 */
        messageDao.addMqMessage(message);

        /** 新增messageDeliver */
        Date nextExecuteTime = MqServerUtil.getRetryTimes(new Date(),0);
        consumers.stream().map(consumer->new MessageDeliver(consumer.getConsumerName(),message.getMessageSourceId(),nextExecuteTime,consumer.getRetryTimes(),consumer.getStatus(),consumer.getVersion()))
                 .forEach(messageDeliver->deliverDao.addMessageDeliver(messageDeliver));
    }

    public Message getByMessageSourceId(String messageSourceId){
        return messageDao.getByMessageSourceId(messageSourceId);
    }

    public int addMessage(Message message){
        return messageDao.addMqMessage(message);
    }
}
package org.vision.github.springboot.mqserver.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vision.github.springboot.mqserver.constant.Globals.*;
import org.vision.github.springboot.mqserver.mapper.MessageConsumerMapper;
import org.vision.github.springboot.mqserver.mapper.MessageListenerMapper;
import org.vision.github.springboot.mqserver.mapper.ServerMapper;
import org.vision.github.springboot.mqserver.pojo.MessageConsumer;
import org.vision.github.springboot.mqserver.pojo.MessageListener;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @author ganminghui
 * @date 2017/12/21
 */
@Service @Slf4j
public class MessageConsumerService {
    @Autowired private MessageConsumerMapper consumerMapper;

    @Autowired private ServerMapper serverMapper;

    @Autowired private MessageListenerMapper listenerMapper;

    public List<MessageConsumer> getByQueueName(String queueName) {
        return this.consumerMapper.getByQueueNameAndStatus(queueName, IMessageConsumerStatus.STATUS_ACTIVE);
    }

    public List<String> getEnableConsumerQueueName() {
        return this.consumerMapper.getDistinctQueueNamesByStatus(IMessageConsumerStatus.STATUS_ACTIVE);
    }

    public MessageConsumer getByConsumerName(String consumerName) {
        return this.consumerMapper.getByConsumerName(consumerName);
    }

    @Transactional( rollbackFor = Exception.class)
    public void updateConsumerForHandleError(String consumerName) {
        this.consumerMapper.updateHandleByConsumerName(consumerName, IMessageConsumerStatus.HANDLE_ERROR);
    }

    public List<MessageConsumer> getHandleConsumerChange() {
        return this.consumerMapper.getByIsHandle(IMessageConsumerStatus.HANDLE_NOT);
    }

    @Transactional( rollbackFor = Exception.class)
    public void processConsumer(MessageConsumer mqConsumer) throws Exception {
        int count = this.consumerMapper.selectForLockByConsumerName(mqConsumer.getConsumerName(), IMessageConsumerStatus.HANDLE_NOT);
        if (count > 0) {
            if (IMessageConsumerStatus.STATUS_WAIT_ADD.equals(mqConsumer.getStatus())) {
                this.addConsumerRecord(mqConsumer);
            } else if (IMessageConsumerStatus.STATUS_WAIT_UPDATE.equals(mqConsumer.getStatus())) {
                this.updateConsumer(mqConsumer);
            } else {
                if (!IMessageConsumerStatus.STATUS_WAIT_DELETE.equals(mqConsumer.getStatus())) {
                    log.error("status  not support,consumerName :{}", mqConsumer.getConsumerName());
                    throw new Exception("status  not support");
                }

                this.deleteConsumer(mqConsumer);
            }

        }
    }

    private void addConsumerRecord(MessageConsumer mqConsumer) {
        int existNum = this.consumerMapper.countByQueueNameAndStatusAndIsHandle(mqConsumer.getQueueName(), IMessageConsumerStatus.STATUS_ACTIVE, IMessageConsumerStatus.HANDLE_SUCCESS);
        if (existNum > 0) {
            this.consumerMapper.updateStatusAndHandleByConsumerName(mqConsumer.getConsumerName(), IMessageConsumerStatus.STATUS_ACTIVE, IMessageConsumerStatus.HANDLE_SUCCESS);
            log.info("queue listener has exist");
        } else {
            List<String> servers = this.serverMapper.getAllServerName();
            Date now = new Date();
            Iterator var5 = servers.iterator();

            while(var5.hasNext()) {
                String serverName = (String)var5.next();
                this.listenerMapper.addMqListener(new MessageListener(mqConsumer.getQueueName(), serverName, now, IMessageListenerStatus.STATUS_CREATE));
            }

            this.consumerMapper.updateStatusAndHandleByConsumerName(mqConsumer.getConsumerName(), IMessageConsumerStatus.STATUS_ACTIVE, IMessageConsumerStatus.HANDLE_SUCCESS);
        }
    }

    private void updateConsumer(MessageConsumer mqConsumer) throws Exception {
    }

    private void deleteConsumer(MessageConsumer mqConsumer) throws Exception {
        this.consumerMapper.updateStatusAndHandleByConsumerName(mqConsumer.getConsumerName(), IMessageConsumerStatus.STATUS_DELETED, IMessageConsumerStatus.HANDLE_SUCCESS);
    }
}
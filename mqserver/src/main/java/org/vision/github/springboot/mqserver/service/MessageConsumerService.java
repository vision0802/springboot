package org.vision.github.springboot.mqserver.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vision.github.springboot.mqserver.constant.Globals.IMessageConsumerStatus;
import org.vision.github.springboot.mqserver.constant.Globals.IMessageListenerStatus;
import org.vision.github.springboot.mqserver.dao.MessageConsumerDao;
import org.vision.github.springboot.mqserver.dao.MessageListenerDao;
import org.vision.github.springboot.mqserver.dao.ServerDao;
import org.vision.github.springboot.mqserver.pojo.MessageConsumer;
import org.vision.github.springboot.mqserver.pojo.MessageListener;

import java.util.Date;
import java.util.List;

/**
 * @author ganminghui
 * @date 2017/12/21
 */
@Service @Slf4j public class MessageConsumerService {
    @Autowired private MessageConsumerDao consumerDao;

    @Autowired private ServerDao serverDao;

    @Autowired private MessageListenerDao listenerDao;

    public List<MessageConsumer> getByQueueName(String queueName) {
        return this.consumerDao.getByQueueNameAndStatus(queueName, IMessageConsumerStatus.STATUS_ACTIVE);
    }

    public List<String> getEnableConsumerQueueName() {
        return this.consumerDao.getDistinctQueueNamesByStatus(IMessageConsumerStatus.STATUS_ACTIVE);
    }

    public MessageConsumer getByConsumerName(String consumerName) {
        return this.consumerDao.getByConsumerName(consumerName);
    }

    @Transactional( rollbackFor = Exception.class)
    public void updateConsumerForHandleError(String consumerName) {
        this.consumerDao.updateHandleByConsumerName(consumerName, IMessageConsumerStatus.HANDLE_ERROR);
    }

    public List<MessageConsumer> getHandleConsumerChange() {
        return this.consumerDao.getByIsHandle(IMessageConsumerStatus.HANDLE_NOT);
    }

    @Transactional( rollbackFor = Exception.class)
    public void processConsumer(MessageConsumer mqConsumer) throws Exception {
        /** 查询指定consumername并且处理标志是not状态的总条数 */
        int count = this.consumerDao.selectForLockByConsumerName(mqConsumer.getConsumerName(), IMessageConsumerStatus.HANDLE_NOT);
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
        int existNum = this.consumerDao.countByQueueNameAndStatusAndIsHandle(mqConsumer.getQueueName(), IMessageConsumerStatus.STATUS_ACTIVE, IMessageConsumerStatus.HANDLE_SUCCESS);
        if (existNum > 0) {
            this.consumerDao.updateStatusAndHandleByConsumerName(mqConsumer.getConsumerName(), IMessageConsumerStatus.STATUS_ACTIVE, IMessageConsumerStatus.HANDLE_SUCCESS);
            log.info("queue listener has exist");
        } else {
            List<String> servers = this.serverDao.getAllServerName();

            Date now = new Date();
            servers.stream()
                   .map(name->new MessageListener(mqConsumer.getQueueName(), name, now, IMessageListenerStatus.STATUS_CREATE))
                   .forEach(listener -> listenerDao.addMqListener(listener));

            this.consumerDao.updateStatusAndHandleByConsumerName(mqConsumer.getConsumerName(), IMessageConsumerStatus.STATUS_ACTIVE, IMessageConsumerStatus.HANDLE_SUCCESS);
        }
    }

    private void updateConsumer(MessageConsumer mqConsumer) throws Exception {}

    private void deleteConsumer(MessageConsumer mqConsumer) throws Exception {
        this.consumerDao.updateStatusAndHandleByConsumerName(mqConsumer.getConsumerName(), IMessageConsumerStatus.STATUS_DELETED, IMessageConsumerStatus.HANDLE_SUCCESS);
    }
}
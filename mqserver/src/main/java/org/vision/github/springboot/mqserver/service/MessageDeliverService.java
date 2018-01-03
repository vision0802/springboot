package org.vision.github.springboot.mqserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vision.github.springboot.mqserver.dao.MessageDeliverDao;
import org.vision.github.springboot.mqserver.dto.MessageDeliverDto;
import org.vision.github.springboot.mqserver.pojo.MessageDeliver;

import java.util.Date;
import java.util.List;

import static org.vision.github.springboot.mqserver.constant.Globals.IMessageDeliverStatus.STATUS_CREATE;
import static org.vision.github.springboot.mqserver.constant.Globals.IMessageDeliverStatus.STATUS_WAIT_RETRY;

/**
 * @author ganminghui
 * @date 2017/12/21
 */
@Service public class MessageDeliverService {
    @Autowired private MessageDeliverDao deliverDao;

    @Transactional(rollbackFor = Exception.class)
    public int updateStatus(MessageDeliverDto dto){
        return deliverDao.updateStatus(dto);
    }

    @Transactional( rollbackFor = Exception.class)
    public int updateStatusAndMark(MessageDeliverDto dto) {
        return deliverDao.updateStatusAndMark(dto);
    }

    public List<MessageDeliver> getNeedExecuteTask() {
        List<MessageDeliver> waitRetryTask = this.deliverDao.getByStatusAndLimit(STATUS_WAIT_RETRY, 1000);
        List<MessageDeliver> notExecuteTask = this.deliverDao.getByStatusAndLimit(STATUS_CREATE, 1000);
        waitRetryTask.addAll(notExecuteTask);
        return waitRetryTask;
    }

    @Transactional( rollbackFor = Exception.class )
    public int updateVersionAndNextExecuteTime(Date nextExecuteTime, String mqMessageSourceId, String consumerName, Integer version) {
        return this.deliverDao.updateVersionAndNextExecuteTime(nextExecuteTime, mqMessageSourceId, consumerName, version);
    }
}
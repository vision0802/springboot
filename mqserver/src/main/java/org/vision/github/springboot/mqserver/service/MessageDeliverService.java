package org.vision.github.springboot.mqserver.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vision.github.springboot.mqserver.constant.Globals;
import org.vision.github.springboot.mqserver.dto.MessageDeliverDto;
import org.vision.github.springboot.mqserver.mapper.MessageDeliverMapper;
import org.vision.github.springboot.mqserver.pojo.MessageDeliver;

import java.util.Date;
import java.util.List;

/**
 * @author ganminghui
 * @date 2017/12/21
 */
@Service @Slf4j
public class MessageDeliverService {
    @Autowired private MessageDeliverMapper mapper;

    @Transactional(rollbackFor = Exception.class)
    public int updateStatus(MessageDeliverDto dto){
        return mapper.updateStatus(dto);
    }

    @Transactional( rollbackFor = Exception.class)
    public int updateStatusAndMark(MessageDeliverDto dto) {
        return mapper.updateStatusAndMark(dto);
    }

    public List<MessageDeliver> getNeedExecuteTask() {
        List<MessageDeliver> waitRetryTask = this.mapper.getByStatusAndLimit(Globals.IMessageDeliverStatus.STATUS_WAIT_RETRY, Integer.valueOf(1000));
        List<MessageDeliver> notExecuteTask = this.mapper.getByStatusAndLimit(Globals.IMessageDeliverStatus.STATUS_CREATE, Integer.valueOf(1000));
        waitRetryTask.addAll(notExecuteTask);
        return waitRetryTask;
    }

    @Transactional( rollbackFor = Exception.class )
    public int updateVersionAndNextExecuteTime(Date nextExecuteTime, String mqMessageSourceId, String consumerName, Integer version) {
        return this.mapper.updateVersionAndNextExecuteTime(nextExecuteTime, mqMessageSourceId, consumerName, version);
    }
}
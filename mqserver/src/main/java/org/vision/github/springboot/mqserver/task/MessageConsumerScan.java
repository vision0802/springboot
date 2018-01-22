package org.vision.github.springboot.mqserver.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.vision.github.springboot.mqserver.pojo.MessageConsumer;
import org.vision.github.springboot.mqserver.service.MessageConsumerService;
import org.vision.github.springboot.mqserver.util.AppUtil;

import java.util.List;

/**
 * @author ganminghui
 * @date 2018/1/3
 */
@Component @Slf4j
public class MessageConsumerScan {
    @Autowired private MessageConsumerService consumerService;

    @Scheduled(fixedRate = 60000L, initialDelay = 60000L)
    public void execute(){
        List<MessageConsumer> change = consumerService.getHandleConsumerChange();
        change.forEach(consumer -> AppUtil.retryThreeTimes(consumer, (t)->{
            try {
                consumerService.processConsumer(t);
                return true;
            } catch (Exception e) {
                log.error("processConsumer error ,consumerName:{}", consumer.getConsumerName(), e);
                return false;
            }
        },(u)->{
            log.error("processConsumer error,consumer isHanlde already to error status,consumerName:{}", u.getConsumerName());
            consumerService.updateConsumerForHandleError(u.getConsumerName());
        }));
    }
}
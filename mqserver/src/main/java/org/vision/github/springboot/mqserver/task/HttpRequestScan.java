package org.vision.github.springboot.mqserver.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.vision.github.springboot.mqserver.pojo.Message;
import org.vision.github.springboot.mqserver.pojo.MessageConsumer;
import org.vision.github.springboot.mqserver.pojo.MessageDeliver;
import org.vision.github.springboot.mqserver.service.MessageConsumerService;
import org.vision.github.springboot.mqserver.service.MessageDeliverService;
import org.vision.github.springboot.mqserver.service.MessageService;
import org.vision.github.springboot.mqserver.util.MqServerUtil;
import org.vision.github.springboot.mqserver.util.QueueUtil;

import java.util.Date;
import java.util.List;

/**
 * @author ganminghui
 * @date 2018/1/3
 */
@Component @Slf4j
public class HttpRequestScan {
    @Autowired private MessageDeliverService deliverService;
    @Autowired private MessageConsumerService consumerService;
    @Autowired private MessageService messageService;

    @Scheduled(fixedRate = 1000L,initialDelay = 6000L)
    public void execute(){
        List<MessageDeliver> needExecuteTask = deliverService.getNeedExecuteTask();
        needExecuteTask.forEach(this::handleMessageDeliver);
    }

    private void handleMessageDeliver(MessageDeliver messageDeliver){
        int rtn = deliverService.updateVersionAndNextExecuteTime(MqServerUtil.getRetryTimes(new Date(), messageDeliver.getHasTryTimes()), messageDeliver.getMqMessageSourceId(), messageDeliver.getConsumerName(), messageDeliver.getVersion());

        if(rtn > 0){
            try{
                Message message = messageService.getByMessageSourceId(messageDeliver.getMqMessageSourceId());
                MessageConsumer consumer = consumerService.getByConsumerName(messageDeliver.getConsumerName());
                QueueUtil.addForWait(QueueUtil.createHttpRequestData(consumer,message.getMessageSourceId(),message.getMqMessage()));
            }catch (Exception e){
                log.error("HttpRequestScan runing on error: mqMessageSourceId:{}, consumerName:{}", messageDeliver.getMqMessageSourceId(), messageDeliver.getConsumerName(), e);
            }
        }

    }
}
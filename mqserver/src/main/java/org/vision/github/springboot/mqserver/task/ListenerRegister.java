package org.vision.github.springboot.mqserver.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.vision.github.springboot.mqserver.pojo.MessageListener;
import org.vision.github.springboot.mqserver.service.MessageListenerService;
import org.vision.github.springboot.mqserver.util.AppUtil;

import java.util.List;

/**
 * @author ganminghui
 * @date 2018/1/3
 */
@Component @Slf4j
public class ListenerRegister {
    @Autowired private MessageListenerService listenerService ;

    @Scheduled(fixedRate = 6000L,initialDelay = 6000L)
    public void execute(){
        List<MessageListener> listeners = listenerService.getNeedHandleByServerName(AppUtil.getUniqueName());

        listeners.forEach(listener -> AppUtil.retryThreeTimes(listener,(t)->{
            try {
                listenerService.addListener(t);
                return true;
            } catch (Exception e) {
                log.error("add queue listener error ,mq_listener id:{}", listener.getId(), e);
                return false;
            }
        },(u)->{
            log.error("add queue lisntener error,consumer status update to error status,mq_listener id:{}", u.getId());
            listenerService.updateListenerToErrorById(u.getId());
        }));
    }
}
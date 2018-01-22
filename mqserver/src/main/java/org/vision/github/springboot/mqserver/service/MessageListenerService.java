package org.vision.github.springboot.mqserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vision.github.springboot.mqserver.constant.Globals;
import org.vision.github.springboot.mqserver.dao.MessageListenerDao;
import org.vision.github.springboot.mqserver.pojo.MessageListener;
import org.vision.github.springboot.mqserver.thread.runnable.MessageListenerHandler;
import org.vision.github.springboot.mqserver.thread.threadpool.MqServerThreadPool;
import org.vision.github.springboot.mqserver.util.MessageHelperUtil;

import javax.jms.JMSException;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * @author ganminghui
 * @date 2018/1/3
 */
@Service public class MessageListenerService {
    @Autowired private MessageListenerDao listenerDao;
    @Autowired private JmsTemplate jmsTemplate;


    public List<MessageListener> getNeedHandleByServerName(String serverName){
        return listenerDao.getByStatusAndServerName(Globals.IMessageListenerStatus.STATUS_CREATE,serverName);
    }

    @Transactional(rollbackFor = Exception.class)
    public void addListener(MessageListener listener) throws Exception {
        createMessageListener(listener.getQueueName());
        listenerDao.updateStatusById(listener.getId(),Globals.IMessageListenerStatus.STATUS_SUCCESS);
    }

    private void createMessageListener(String queueName) throws JMSException {
        MessageHelperUtil.createQueueAndListener(jmsTemplate.getConnectionFactory().createConnection(),queueName);
    }

    public int updateListenerToErrorById(Integer id){
        return listenerDao.updateStatusById(id,Globals.IMessageListenerStatus.STATUS_ERROR);
    }
}
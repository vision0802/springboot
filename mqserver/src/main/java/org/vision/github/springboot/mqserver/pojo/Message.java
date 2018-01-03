package org.vision.github.springboot.mqserver.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * @author ganminghui
 * @date 2017/12/21
 */
@Data public class Message {
    private Integer id;
    private String queueName;
    private String messageSourceId;
    private String mqMessage;
    private Date consumerTime;

    public Message(String queueName,String messageSourceId,String mqMessage,Date consumerTime){
        setQueueName(queueName); setMessageSourceId(messageSourceId);
        setMqMessage(mqMessage); setConsumerTime(consumerTime);
    }
}
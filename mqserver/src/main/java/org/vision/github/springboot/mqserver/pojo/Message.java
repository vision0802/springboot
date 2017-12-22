package org.vision.github.springboot.mqserver.pojo;

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
}
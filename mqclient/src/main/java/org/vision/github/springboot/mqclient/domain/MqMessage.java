package org.vision.github.springboot.mqclient.domain;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * MQ消息的载体
 * @author ganminghui
 * @date 2017/12/20
 */
@Data public class MqMessage {
    interface IStatus{
        Integer MESSAGE_STATUS_WAIT = 1;
        Integer MESSAGE_STATUS_SENDING = 2;
        Integer MESSAGE_STATUS_SUCCESS = 3;
    }

    private Integer id;
    private Integer messageStatus;
    private String message;
    private String queueName;
    private Integer version;
    private LocalDateTime retryTime;
    private Integer retryTimes;
    private String failReason;
    private String tableName;
    private Long sendTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
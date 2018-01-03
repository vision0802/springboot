package org.vision.github.springboot.mqserver.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author ganminghui
 * @date 2017/12/21
 */
@Data @NoArgsConstructor public class MessageDeliver {
    private Integer id;
    private String mqMessageSourceId;
    private String consumerName;
    private Date createTime;
    private Date updateTime;
    private Date nextExecuteTime;
    private Integer retryTimes;
    private Integer hasTryTimes;
    private String mark;
    private Integer status;
    private Integer version;

    public MessageDeliver(String consumerName, String mqMessageSourceId, Date nextExecuteTime, Integer retryTimes, Integer status, Integer version) {
        this.consumerName = consumerName;
        this.mqMessageSourceId = mqMessageSourceId;
        this.nextExecuteTime = nextExecuteTime;
        this.retryTimes = retryTimes;
        this.hasTryTimes = 0;
        this.status = status;
        this.version = version;
    }
}
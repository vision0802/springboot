package org.vision.github.springboot.mqserver.pojo;

import lombok.Data;

import java.util.Date;

/**
 * @author ganminghui
 * @date 2017/12/21
 */
@Data public class MessageDeliver {
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
}
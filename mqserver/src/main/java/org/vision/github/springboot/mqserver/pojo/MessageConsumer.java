package org.vision.github.springboot.mqserver.pojo;

import lombok.Data;

import java.util.Date;

/**
 * @author ganminghui
 * @date 2017/12/21
 */
@Data public class MessageConsumer {
    private Integer id;
    private String consumerName;
    private String consumerDesc;
    private String queueName;
    private String consumerUrl;
    private String urlParams;
    private String requestEncoding;
    private String requestMethod;
    private String callType;
    private Integer retryTimes;
    private Date createTime;
    private Date updateTime;
    private Integer version;
    private Integer status;
    private Integer isHandle;
}
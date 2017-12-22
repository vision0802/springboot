package org.vision.github.springboot.mqserver.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author ganminghui
 * @date 2017/12/21
 */
@AllArgsConstructor @NoArgsConstructor
@Data public class MessageListener {
    private Integer id;
    private String queueName;
    private String serverName;
    private Date createTime;
    private Date updateTime;
    private Integer status;

    public MessageListener(String queueName, String serverName, Date createTime, Integer status){
        setQueueName(queueName); setServerName(serverName);
        setCreateTime(createTime); setStatus(status);
    }
}
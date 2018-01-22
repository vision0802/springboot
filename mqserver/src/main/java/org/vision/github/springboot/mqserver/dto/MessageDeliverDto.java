package org.vision.github.springboot.mqserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ganminghui
 * @date 2017/12/20
 */
@AllArgsConstructor @NoArgsConstructor
@Data public class MessageDeliverDto {
    private String consumerName;
    private String mqMessageSourceId;
    private Integer status;
    private Integer sourceStatus;
    private String mark;

    public MessageDeliverDto(String consumerName,String mqMessageSourceId,Integer status,Integer sourceStatus){
        setConsumerName(consumerName); setMqMessageSourceId(mqMessageSourceId);
        setStatus(status); setSourceStatus(sourceStatus);
    }
}
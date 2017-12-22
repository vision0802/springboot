package org.vision.github.springboot.mqserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ganminghui
 * @date 2017/12/20
 */
@NoArgsConstructor @AllArgsConstructor
@Data public class HttpRequestData {
    private String url;
    private String consumerName;
    private String urlParams;
    private String requestMethod;
    private String requestEncoding;
    private String callType;
    private String mqMessageSourceId;
    private String mqMessage;
    private Integer retryTimes;
    private Integer hasTryTimes;
    private Integer messsageDeliverStatus;
    private String rawUrl;
    private Integer version;
}
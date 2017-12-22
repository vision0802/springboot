package org.vision.github.springboot.mqclient.context;

import lombok.Data;
import javax.sql.DataSource;

/**
 * 服务端的上下文对象
 * @author user
 * @date 2017/12/20
 */
@Data public class ServerContext {
    private String tableName;
    private DataSource dataSource;
    private String brolerUrl;
    private QueueContext queueContext;
    private String queueName;
    private Integer queueMaxCount;
}
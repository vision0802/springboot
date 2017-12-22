package org.vision.github.springboot.mqserver.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author ganminghui
 * @date 2017/12/21
 */
@NoArgsConstructor
@Data public class Server {
    private Integer id;
    private String serverName;
    private Date createTime;

    public Server(String serverName,Date createTime){
        setServerName(serverName); setCreateTime(createTime);
    }
}
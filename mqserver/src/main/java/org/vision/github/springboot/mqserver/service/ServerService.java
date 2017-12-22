package org.vision.github.springboot.mqserver.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vision.github.springboot.mqserver.mapper.ServerMapper;
import org.vision.github.springboot.mqserver.pojo.Server;

/**
 * @author ganminghui
 * @date 2017/12/21
 */
@Service @Slf4j
public class ServerService {
    @Autowired private ServerMapper serverMapper ;

    @Transactional(rollbackFor = Exception.class)
    public void addServer(Server server){
        serverMapper.addServer(server);
    }
}
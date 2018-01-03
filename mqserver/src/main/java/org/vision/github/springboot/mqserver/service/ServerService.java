package org.vision.github.springboot.mqserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vision.github.springboot.mqserver.dao.ServerDao;
import org.vision.github.springboot.mqserver.pojo.Server;

import java.util.List;

/**
 * @author ganminghui
 * @date 2017/12/21
 */
@Service public class ServerService {
    @Autowired private ServerDao serverDao;

    @Transactional(rollbackFor = Exception.class)
    public void addServer(Server server){
        int number = serverDao.countByServerName(server.getServerName());
        if(number <= 0){
            serverDao.addServer(server);
        }
    }

    public List<String> getAllServerName(){
        return serverDao.getAllServerName();
    }
}
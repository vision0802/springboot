package org.vision.github.springboot.mqserver.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.vision.github.springboot.mqserver.pojo.Server;
import org.vision.github.springboot.mqserver.service.ServerService;
import org.vision.github.springboot.mqserver.util.AppUtil;
import org.vision.github.springboot.mqserver.util.SpringUtil;

import java.net.UnknownHostException;
import java.util.Date;

/**
 * @author ganminghui
 * @date 2017/12/21
 */
@Component @Slf4j
public class StartupListener implements ApplicationListener<ContextRefreshedEvent> {
    @Override public void onApplicationEvent(ContextRefreshedEvent event) {
        SpringUtil.setContext(event.getApplicationContext());

        AppUtil.init();

        SpringUtil.getBean(ServerService.class).addServer(new Server(AppUtil.getUniqueName(),new Date()));
        log.info("add server success...");

    }
}
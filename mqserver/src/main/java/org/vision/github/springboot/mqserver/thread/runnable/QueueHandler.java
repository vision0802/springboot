package org.vision.github.springboot.mqserver.thread.runnable;

import com.dianping.cat.message.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.http.nio.reactor.IOReactorException;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;
import org.vision.github.springboot.mqserver.config.HttpRequestCount;
import org.vision.github.springboot.mqserver.constant.Globals;
import org.vision.github.springboot.mqserver.dto.HttpRequestData;
import org.vision.github.springboot.mqserver.dto.MessageDeliverDto;
import org.vision.github.springboot.mqserver.exception.ConsumerException;
import org.vision.github.springboot.mqserver.http.asynccallback.HttpAsyncCallback;
import org.vision.github.springboot.mqserver.service.MessageDeliverService;
import org.vision.github.springboot.mqserver.util.*;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author ganminghui
 * @date 2018/1/3
 */
@Slf4j public class QueueHandler implements Runnable {
    private MessageDeliverService deliverService;
    private DiscoveryClient discoveryClient;

    private static final String CALL_TYPE_HTTP =  "HTTP";
    private static final String CALL_TYPE_EUREKA = "EUREKA";

    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";

    public QueueHandler(){
        deliverService = SpringUtil.getBean(MessageDeliverService.class);
        discoveryClient = SpringUtil.getBean(DiscoveryClient.class);
    }

    @Override public void run() {
        while (true){
            Transaction transaction = CatUtil.createTransaction("handleRequest", "maserverHandleRequest");

            try {
                HttpRequestData requestData = QueueUtil.get();
                handleRequestData(requestData);

                if(HttpRequestCount.isOverMaxRequest()){
                    TimeUnit.SECONDS.sleep(2);
                }
            } catch (InterruptedException e) {
                log.error("queue get exception!",e);
            } finally {
                CatUtil.commitCatTransaction(transaction);
            }
        }
    }

    private void handleRequestData(HttpRequestData requestData){
        try {
            call(requestData);
        } catch (Exception e) {
            MessageDeliverDto deliverDto = new MessageDeliverDto( requestData.getConsumerName(),requestData.getMqMessageSourceId(), MqServerUtil.getMessageDeliverStatus(requestData.getRetryTimes(), requestData.getHasTryTimes()), requestData.getMesssageDeliverStatus());
            deliverDto.setMark(String.format("message:%s,exception:%s",e.getLocalizedMessage(),e.getClass().getName()));
            deliverService.updateStatusAndMark(deliverDto);
            log.error("request httpasync error,consumerName:{}!",requestData.getConsumerName(),e);
        }
    }

    private void call(HttpRequestData requestData) throws Exception {
        String callType = requestData.getCallType().toUpperCase();
        if(CALL_TYPE_HTTP.equals(callType)){
            callByHttp(requestData);
        }else if (CALL_TYPE_EUREKA.equals(callType)){
            callByEureka(requestData);
        }else {
            throw new ConsumerException("consumer callType is not support!");
        }
    }

    private void callByEureka(HttpRequestData requestData) throws ConsumerException, UnsupportedEncodingException, IOReactorException {
        String host = AppUtil.getEurekaServiceName(requestData.getUrl());
        List<ServiceInstance> instances = discoveryClient.getInstances(host);
        if(instances.isEmpty()){
            throw new ConsumerException(String.format("no serviceInstance from eureka register center,consumerName:%s,consumerUrl:%s",requestData.getConsumerName(),requestData.getUrl()));
        }
        EurekaDiscoveryClient.EurekaServiceInstance instance = chooseServiceInstance(instances);
        String ipAndPort = instance.getInstanceInfo().getIPAddr() + ":" + instance.getInstanceInfo().getPort();
        requestData.setUrl(requestData.getUrl().replace(host,ipAndPort));
        this.callByHttp(requestData);
    }

    private EurekaDiscoveryClient.EurekaServiceInstance chooseServiceInstance(List<ServiceInstance> instances){
        int index = RandomUtils.nextInt() % instances.size();
        return (EurekaDiscoveryClient.EurekaServiceInstance)instances.get(index);
    }

    private void callByHttp(HttpRequestData requestData) throws UnsupportedEncodingException, IOReactorException{
        String method = requestData.getRequestMethod().toUpperCase();
        if(METHOD_GET.equals(method)) {
            sendGet(requestData);
        } else if(METHOD_POST.equals(method)) {
            sendPost(requestData);
        }
    }

    private void sendGet(HttpRequestData requestData) throws UnsupportedEncodingException, IOReactorException {
        HttpUtil.sendAsyncByGet( requestData.getUrl().trim(),new HashMap<String,String>(1){{put("text",requestData.getMqMessage());}}, "UTF-8",new HttpAsyncCallback(requestData));
    }

    private void sendPost(HttpRequestData requestData) throws UnsupportedEncodingException, IOReactorException {
       if(Globals.MQAPP_VERSION_1.equals(requestData.getVersion())){
            HttpUtil.sendFormByPostAsync(requestData.getUrl().trim(),new HashMap<String,String>(1){{put("text",requestData.getMqMessage());}},"UTF-8",new HttpAsyncCallback(requestData));
       }else if (Globals.MQAPP_VERSION_2.equals(requestData.getVersion())){
            HttpUtil.sendRawByPostAsync(requestData.getUrl().trim(),requestData.getMqMessage(),"UTF-8",new HttpAsyncCallback(requestData));
       }else {
           throw new RuntimeException("mqserver consumer version is not support!");
       }
    }
}
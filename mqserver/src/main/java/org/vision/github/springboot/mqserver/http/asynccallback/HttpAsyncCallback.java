package org.vision.github.springboot.mqserver.http.asynccallback;

import com.dianping.cat.message.Transaction;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.vision.github.springboot.mqserver.config.HttpRequestCount;
import org.vision.github.springboot.mqserver.constant.Globals;
import org.vision.github.springboot.mqserver.dto.HttpRequestData;
import org.vision.github.springboot.mqserver.dto.MessageDeliverDto;
import org.vision.github.springboot.mqserver.dto.ResultMsg;
import org.vision.github.springboot.mqserver.exception.ConsumerException;
import org.vision.github.springboot.mqserver.service.MessageDeliverService;
import org.vision.github.springboot.mqserver.util.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author ganminghui
 * @date 2017/12/20
 */
@Slf4j public class HttpAsyncCallback extends BaseHttpCallback {
    private HttpRequestData requestData;

    private MessageDeliverService messageDeliverService;

    public HttpAsyncCallback(HttpRequestData httpRequestData){
        this.requestData = httpRequestData;
        this.messageDeliverService = SpringUtil.getBean(MessageDeliverService.class);
    }

    @Override public void internalComplete(HttpResponse httpResponse) {
        Transaction transaction = CatUtil.createTransaction("requestCallback", "mqappRequestCallback");
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        if (statusCode == HttpStatus.SC_OK) {
            this.handleResp(httpResponse);
        } else {
            this.handleErrorResp(String.valueOf(statusCode), "http status is not 200,inner error");
        }

        CatUtil.commitCatTransaction(transaction);
    }

    private void handleResp(HttpResponse httpResponse) {
        ResultMsg resultMsg = HttpUtil.getResultMsg(httpResponse);

        IResultMsgStrategy strategy;
        if (Globals.MQAPP_VERSION_1.equals(this.requestData.getVersion())) {
            strategy = new ResultMsgStrategyV1();
        } else if(Globals.MQAPP_VERSION_2.equals(this.requestData.getVersion())) {
            strategy = new ResultMsgStrategyV2();
        }else {
            throw new RuntimeException("mqapp consumer version is not support");
        }
        ResultMsgContext context = new ResultMsgContext(resultMsg,strategy);
        context.handleResultMsg();
    }

    private void handleSuccessResp() {
        HttpRequestCount.decrement();
        this.messageDeliverService.updateStatus(new MessageDeliverDto(this.requestData.getConsumerName(), this.requestData.getMqMessageSourceId(), Globals.IMessageDeliverStatus.STATUS_SUCCESS, this.requestData.getMesssageDeliverStatus()));
    }

    private void handlRetryResp() {
        try {
            HttpRequestCount.decrement();
            this.requestData.setUrl(this.requestData.getRawUrl());
            QueueUtil.offer(this.requestData);
        } catch (InterruptedException var2) {
            log.error("retry request error,interruptedException", var2);
        }

    }

    private void handleErrorResp(String statusCode, String msg) {
        log.error("request return statusCode:{},msg:{},consumerName:{},url:{}", statusCode, msg, this.requestData.getConsumerName(), this.requestData.getUrl());
        this.internalFailed(new ConsumerException("request fail,statusCode:" + statusCode));
    }

    @Override public void internalFailed(Exception e) {
        HttpRequestCount.decrement();
        Transaction transaction = CatUtil.createTransaction("requestCallback", "mqappRequestCallback");
        if (e instanceof TimeoutException) {
            try {
                log.error("http timeout error,try again,consumerName:{},url:{},exceptionName:{}", this.requestData.getConsumerName(), this.requestData.getUrl(), e.getClass().getName());
                this.requestData.setUrl(this.requestData.getRawUrl());
                QueueUtil.offer(this.requestData);
            } catch (InterruptedException var7) {
                log.error("add request job to queue error,consumerName:{}", this.requestData.getConsumerName(), var7);
            } finally {
                CatUtil.commitCatTransaction(transaction);
            }

        } else {
            MessageDeliverDto messageDeliver = new MessageDeliverDto(this.requestData.getConsumerName(), this.requestData.getMqMessageSourceId(), MqServerUtil.getMessageDeliverStatus(this.requestData.getRetryTimes(), this.requestData.getHasTryTimes()), this.requestData.getMesssageDeliverStatus());
            messageDeliver.setMark(String.format("message:%s,exception:%s", e.getLocalizedMessage(), e.getClass().getName()));
            this.messageDeliverService.updateStatusAndMark(messageDeliver);
            log.error("httpasync async fail ,consumerName:{},url:{}", this.requestData.getConsumerName(), this.requestData.getUrl(), e);
            CatUtil.commitCatTransaction(transaction);
        }
    }

    @Override public void internalCancelled() {
        HttpRequestCount.decrement();
        Transaction transaction = CatUtil.createTransaction("requestCallback", "mqappRequestCallback");
        MessageDeliverDto messageDeliver = new MessageDeliverDto(this.requestData.getConsumerName(), this.requestData.getMqMessageSourceId(), MqServerUtil.getMessageDeliverStatus(this.requestData.getRetryTimes(), this.requestData.getHasTryTimes()), this.requestData.getMesssageDeliverStatus());
        messageDeliver.setMark("connection has been cancelled");
        this.messageDeliverService.updateStatusAndMark(messageDeliver);
        log.error("httpasync async connection cancelled,consumerName:{},url:{}", this.requestData.getConsumerName(), this.requestData.getUrl());
        CatUtil.commitCatTransaction(transaction);
    }

    static class ResultMsgContext{
        private ResultMsg resultMsg;
        private IResultMsgStrategy strategy;
        public ResultMsgContext(ResultMsg msg,IResultMsgStrategy strategy){
            this.resultMsg = msg;
            this.strategy = strategy;
        }

        public void handleResultMsg(){
            this.strategy.doHandle(this);
        }
    }

    interface IResultMsgStrategy{
        /** 策略算法默认实现
         *  @param context 上下文对象
         * */
        default void doHandle(ResultMsgContext context){  }
    }

    class ResultMsgStrategyV1 implements IResultMsgStrategy{
        @Override public void doHandle(ResultMsgContext context) {
            handleSuccessResp();
        }
    }

    class ResultMsgStrategyV2 implements IResultMsgStrategy{
        @Override public void doHandle(ResultMsgContext context) {
            if(Globals.IMessageResultStatus.RESULT_RETRY.equals(context.resultMsg.getCode())){
                handlRetryResp();
            }else if (Globals.IMessageResultStatus.RESULT_SUCCESS.equals(context.resultMsg.getCode())){
                handleSuccessResp();
            }else {
                handleErrorResp(context.resultMsg.getCode(),context.resultMsg.getMsg());
            }
        }
    }
}
package org.vision.github.springboot.mqserver.http.asynccallback;

import org.apache.http.HttpResponse;
import org.vision.github.springboot.mqserver.dto.HttpRequestData;
import org.vision.github.springboot.mqserver.service.MessageDeliverService;
import org.vision.github.springboot.mqserver.util.SpringUtil;

/**
 * @author ganminghui
 * @date 2017/12/20
 */
public class HttpAsyncCallback extends BaseHttpCallback {
    private HttpRequestData httpRequestData;

    private MessageDeliverService messageDeliverService;

    public HttpAsyncCallback(HttpRequestData httpRequestData){
        this.httpRequestData = httpRequestData;
        this.messageDeliverService = SpringUtil.getBean(MessageDeliverService.class);
    }

    @Override protected void internalComplete(HttpResponse response) {

    }

    @Override protected void internalFailed(Exception ex) {

    }

    @Override protected void internalCancelled() {

    }
}
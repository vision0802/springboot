package org.vision.github.springboot.mqserver.http.asynccallback;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.vision.github.springboot.mqserver.dto.HttpRequestData;

/**
 * @author ganminghui
 * @date 2017/12/20
 */
@Slf4j public class HttpAsyncCallback extends BaseHttpCallback {
    private HttpRequestData httpRequestData;

    @Override protected void internalComplete(HttpResponse response) {

    }

    @Override protected void internalFailed(Exception ex) {

    }

    @Override protected void internalCancelled() {

    }
}
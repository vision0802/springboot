package org.vision.github.springboot.mqserver.http.asynccallback;

import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;

/**
 * @author ganminghui
 * @date 2017/12/20
 */
public abstract class BaseHttpCallback implements FutureCallback<HttpResponse> {
    @Override public void cancelled() { this.internalCancelled(); }

    @Override public void completed(HttpResponse httpResponse) { this.internalComplete(httpResponse); }

    @Override public void failed(Exception e) { this.internalFailed(e); }

    /** 请求完成
     * @param response
     */
    protected abstract void internalComplete(HttpResponse response);

    /** 请求失败
     * @param ex
     */
    protected abstract void internalFailed(Exception ex);

    /** 取消请求 */
    protected abstract void internalCancelled();
}
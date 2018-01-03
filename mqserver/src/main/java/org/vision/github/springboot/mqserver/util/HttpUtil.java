package org.vision.github.springboot.mqserver.util;

import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.util.EntityUtils;
import org.springframework.util.CollectionUtils;
import org.vision.github.springboot.mqserver.config.HttpRequestCount;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ganminghui
 * @date 2017/12/21
 */
@Slf4j
public class HttpUtil {
    private static final int SOCKE_TIME_OUT = 20000;
    private static final int CONNECT_REQUEST_TIME_OUT = 180000;
    private static final int CONNECT_TIME_OUT = 20000;
    private static final String JSON_CONTENT_TYPE = "application/json";
    private static final String FORM_CONTENT_TYPE = "application/x-www-form-urlencoded";

    private static CloseableHttpAsyncClient httpClient = HttpClientHolder.httpClient;
    private static class HttpClientHolder{
        public static CloseableHttpAsyncClient httpClient = initClientAndConnectionManager();
    }

    private static CloseableHttpAsyncClient initClientAndConnectionManager() {
        IOReactorConfig ioReactorConfig = IOReactorConfig.custom().setIoThreadCount(Runtime.getRuntime().availableProcessors()).build();
        try {
            DefaultConnectingIOReactor ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);
            PoolingNHttpClientConnectionManager httpConnectionManager = new PoolingNHttpClientConnectionManager(ioReactor);
            httpConnectionManager.setMaxTotal(2000);
            httpConnectionManager.setDefaultMaxPerRoute(500);
            return HttpAsyncClients.custom().setConnectionManager(httpConnectionManager).setDefaultRequestConfig(getRequestConfig()).build();
        } catch (Exception e) {
            log.error("init httpAsyncClient error...",e);
            throw new RuntimeException("init httpAsyncClient error",e);
        }
    }

    private static RequestConfig getRequestConfig() {
        return RequestConfig.custom().setConnectionRequestTimeout(CONNECT_REQUEST_TIME_OUT)
                .setConnectTimeout(CONNECT_TIME_OUT).setSocketTimeout(SOCKE_TIME_OUT).build();
    }

    public static void sendAsyncByGet(String url, Map<String, String> params, String charset, FutureCallback<HttpResponse> callback) throws UnsupportedEncodingException, IOReactorException {
        httpClient.start();
        String strParams = getParamsStr(params);
        url = getEncodedUrl(url, charset, strParams);
        HttpGet httpGet = new HttpGet(url);
        log.debug("send async get request,url:{}", url);
        httpClient.execute(httpGet, callback);
        HttpRequestCount.increment();
    }

    public static void sendFormByPostAsync(String url, Map<String, String> params, String charset, FutureCallback<HttpResponse> callback) throws UnsupportedEncodingException, IOReactorException {
        charset = StringUtils.isBlank(charset) ? "UTF-8" : charset;
        httpClient.start();
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Content-Type", FORM_CONTENT_TYPE);

        List<BasicNameValuePair> valuePairs = params.entrySet().stream().map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue())).collect(Collectors.toList());
        UrlEncodedFormEntity uefEntity1 = new UrlEncodedFormEntity(valuePairs, charset);
        httpPost.setEntity(uefEntity1);

        log.debug("send async post request,url:{}", url);
        httpClient.execute(httpPost, callback);
        HttpRequestCount.increment();
    }

    public static void sendRawByPostAsync(String url, String rawParam, String charset, FutureCallback<HttpResponse> callback) throws UnsupportedEncodingException, IOReactorException {
        charset = StringUtils.isBlank(charset) ? "UTF-8" : charset;

        httpClient.start();
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Content-Type", JSON_CONTENT_TYPE);
        if(StringUtils.isNoneBlank(rawParam)) {
            StringEntity entity = new StringEntity(rawParam, charset);
            entity.setContentEncoding(charset);
            entity.setContentType(JSON_CONTENT_TYPE);
            httpPost.setEntity(entity);
        }

        log.debug("send async post request,url:{}", url);
        httpClient.execute(httpPost, callback);
        HttpRequestCount.increment();
    }

    private static String getParamsStr(Map<String, String> params) {
        if(CollectionUtils.isEmpty(params)){
            return StringUtils.EMPTY;
        }
        return Joiner.on("").withKeyValueSeparator("=").join(params);
    }

    private static String getEncodedUrl(String url, String charset, String strParams) throws UnsupportedEncodingException {
        if(url.contains("?")) {
            StringBuilder encodedParams = new StringBuilder(url.substring(url.indexOf("?") + 1));
            if(StringUtils.isNotBlank(strParams)) {
                encodedParams.append("&").append(strParams);
            }

            String encodedParams1 = URLEncoder.encode(encodedParams.toString(), charset);
            url = url.substring(0, url.indexOf("?") + 1) + encodedParams1;
        } else if(StringUtils.isNotBlank(strParams)) {
            String encodedParams2 = URLEncoder.encode(strParams, charset);
            url = url + "?" + encodedParams2;
        }

        return url;
    }

    public static String getResponseStr(HttpResponse response) throws IOException {
        return EntityUtils.toString(response.getEntity());
    }

    private void closeClient() {
        if(httpClient != null) {
            try {
                log.debug("closing client");
                httpClient.close();
            } catch (IOException var2) {
                log.error("close client error ", var2);
            }
        }
    }
}
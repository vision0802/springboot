package org.vision.github.springboot.mqserver.exception;

/**
 * @author ganminghui
 * @date 2017/12/20
 */
public class HttpResponseException extends Exception {
    public HttpResponseException(String message){ super(message); }

    public HttpResponseException(String message,Throwable cause){ super(message,cause); }

    public HttpResponseException(Throwable cause){ super(cause); }

}
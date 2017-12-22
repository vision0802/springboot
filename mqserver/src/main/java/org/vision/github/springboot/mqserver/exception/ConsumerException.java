package org.vision.github.springboot.mqserver.exception;

/**
 * @author ganminghui
 * @date 2017/12/20
 */
public class ConsumerException extends Exception {
    public ConsumerException(String message){ super(message); }

    public ConsumerException(String message,Throwable cause){ super(message,cause); }

    public ConsumerException(Throwable cause){ super(cause); }

}
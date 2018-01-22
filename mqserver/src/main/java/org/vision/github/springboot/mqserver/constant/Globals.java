package org.vision.github.springboot.mqserver.constant;

/**
 * @author ganminghui
 * @date 2017/12/20
 */
public interface Globals {
    String CHARSET_UTF8 = "UTF-8";
    String DEAD_QUEUE = "ActiveMQ.DLQ";
    int SCAN_RETRY_TIMES = 3;
    int REGISTER_LISTENER_RETRY_TIMES = 3;
    Integer MQAPP_VERSION_1 = Integer.valueOf(1);
    Integer MQAPP_VERSION_2 = Integer.valueOf(2);
    int MAX_SIZE = 1000;
    String HTTP_PARAM_NAME_TEXT = "text";
    int Listener_number = 1;
    String CAT_HANDLE_REQUEST_TYPE = "handleRequest";
    String CAT_HANDLE_REQUEST_NAME = "mqappHandleRequest";
    String CAT_REQUEST_CALLBACK_TYPE = "requestCallback";
    String CAT_REQUEST_CALLBACK_NAME = "mqappRequestCallback";

    interface  IMessageListenerStatus{
        Integer STATUS_CREATE = Integer.valueOf(0);
        Integer STATUS_SUCCESS = Integer.valueOf(1);
        Integer STATUS_ERROR = Integer.valueOf(2);  
    }
    
    interface IMessageDeliverStatus{
        Integer STATUS_CREATE = Integer.valueOf(0);
        Integer STATUS_SUCCESS = Integer.valueOf(1);
        Integer STATUS_ERROR = Integer.valueOf(2);
        Integer STATUS_WAIT_RETRY = Integer.valueOf(3);
    }

    interface IMessageConsumerStatus{
        Integer HANDLE_NOT = Integer.valueOf(0);
        Integer HANDLE_SUCCESS = Integer.valueOf(1);
        Integer HANDLE_ERROR = Integer.valueOf(2);
        Integer STATUS_WAIT_ADD = Integer.valueOf(0);
        Integer STATUS_WAIT_UPDATE = Integer.valueOf(1);
        Integer STATUS_WAIT_DELETE = Integer.valueOf(2);
        Integer STATUS_ACTIVE = Integer.valueOf(7);
        Integer STATUS_DELETED = Integer.valueOf(8);
        String CALL_TYPE_EUREKA = "EUREKA";
        String CALL_TYPE_HTTP = "HTTP";
    }

    interface IMessageResultStatus{
        String RESULT_SUCCESS = "00";
        String RESULT_FAIL = "-1";
        String RESULT_RETRY = "999";

    }
}
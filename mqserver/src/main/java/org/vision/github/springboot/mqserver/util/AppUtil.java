package org.vision.github.springboot.mqserver.util;

import org.vision.github.springboot.mqserver.config.ExtConfiguration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ganminghui
 * @date 2017/12/20
 */
public class AppUtil {
    private static String appName = SpringUtil.getBean(ExtConfiguration.class).getAppName();
    private static boolean isInit = false;
    private static final Pattern compile = Pattern.compile("(http|HTTP)://(.*?)/.*");

    private AppUtil() {}

    public static String getUniqueName() { return AppUtil.appName; }

    public static synchronized void init() {
        if(!isInit){
            InetAddress localHost;
            try {
                localHost = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                throw new RuntimeException("can't get localhost address and name...");
            }
            appName = localHost.getHostName() + localHost.getHostAddress();
            isInit = true;
        }
    }

    public static String getEurekaServiceName(String url) {
        Objects.requireNonNull(url, "url parameter must be not null...");
        Matcher matcher = compile.matcher(url);
        if (matcher.matches()) { return matcher.group(2); }
        throw new RuntimeException("can't get eureka service name,url:" + url);
    }

    private static final Integer RETRY_TIMES = 3;

    public static <T> void retryThreeTimes(T t, Predicate<T> predicate, Consumer<T> finalConsumer){
        int i =0;
        while (i < RETRY_TIMES){
            if(predicate.test(t)){ break; }
            else { ++ i; }
        }
        if(i==RETRY_TIMES){ finalConsumer.accept(t); }
    }
}
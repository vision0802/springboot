package org.vision.github.springboot.mqserver.util;

import org.springframework.context.ApplicationContext;

/**
 * @author ganminghui
 * @date 2017/12/21
 */
public class SpringUtil {
    private static ApplicationContext context = null;
    private SpringUtil(){   }

    public static void setContext(ApplicationContext context){ SpringUtil.context = context; }

    public static ApplicationContext getContext(){ return SpringUtil.context; }

    public static <T> T getBean(Class<T> clazz) { return SpringUtil.context.getBean(clazz); }
}
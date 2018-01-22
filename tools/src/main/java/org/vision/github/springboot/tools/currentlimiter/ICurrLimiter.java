package org.vision.github.springboot.tools.currentlimiter;

/**
 * @author ganminghui
 * @date 2018/1/8
 */
public interface ICurrLimiter {

    /**
     * 限流接口
     *  @return 限流结果
     */
    boolean currentLimited();
}
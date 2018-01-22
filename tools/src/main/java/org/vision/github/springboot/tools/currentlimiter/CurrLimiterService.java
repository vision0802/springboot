package org.vision.github.springboot.tools.currentlimiter;

/**
 * 提供限流器服务,可用于filter/aop/method中使用
 * @author ganminghui
 * @date 2018/1/8
 */
public class CurrLimiterService {
    private int limiterType;

    private static final int DEFAULT_LIMITER_TYPE = ConcreateLimiter.LimiterType.counterLimiter.getValue();

    public CurrLimiterService(int limiterType) { this.limiterType = limiterType; }

    public CurrLimiterService(){this(DEFAULT_LIMITER_TYPE);}

    private ICurrLimiter getCounterLimiter(){
        return new ConcreateLimiter.CounterLimiter();
    }

    private ICurrLimiter getTokenBucketLimiter(){
        return new ConcreateLimiter.TokenBucketLimiter();
    }

    public ICurrLimiter getCurrLimiter(){
        if(limiterType==ConcreateLimiter.LimiterType.counterLimiter.getValue()){ return getCounterLimiter();}
        else {return getTokenBucketLimiter(); }
    }
}
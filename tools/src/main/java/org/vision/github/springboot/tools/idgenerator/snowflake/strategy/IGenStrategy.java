package org.vision.github.springboot.tools.idgenerator.snowflake.strategy;

/**
 * @author user
 * @date 2018/1/5
 */
public interface IGenStrategy {
    /**
     * 根据上下文对象生成ID
     * @param context 上下文对象
     * @return ID
     */
    default long generateId(StrategyContext context){
        return 0L;
    }
}
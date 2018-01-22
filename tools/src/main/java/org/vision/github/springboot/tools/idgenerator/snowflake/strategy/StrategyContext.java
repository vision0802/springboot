package org.vision.github.springboot.tools.idgenerator.snowflake.strategy;

import lombok.Getter;
import org.vision.github.springboot.tools.idgenerator.snowflake.bean.IDStructure;

/**
 * @author user
 * @date 2018/1/5
 */
public class StrategyContext {
    private IGenStrategy genStrategy;
    @Getter private IDStructure idStructure;

    public StrategyContext(IDStructure idStructure,IGenStrategy genStrategy){
        this.idStructure = idStructure;
        this.genStrategy = genStrategy;
    }

    public long generateId(){
        return genStrategy.generateId(this);
    }
}
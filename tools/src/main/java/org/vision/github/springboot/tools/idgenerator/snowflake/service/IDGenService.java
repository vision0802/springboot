package org.vision.github.springboot.tools.idgenerator.snowflake.service;

import org.vision.github.springboot.tools.idgenerator.snowflake.bean.IDStructure;
import org.vision.github.springboot.tools.idgenerator.snowflake.beanfactory.IDStructureFactory;
import org.vision.github.springboot.tools.idgenerator.snowflake.strategy.ConcreateStrategy;
import org.vision.github.springboot.tools.idgenerator.snowflake.strategy.IGenStrategy;
import org.vision.github.springboot.tools.idgenerator.snowflake.strategy.StrategyContext;

import java.util.Optional;

/**
 * @author user
 * @date 2018/1/6
 */
public class IDGenService {
    private static final IGenStrategy DEFAULT_GENSTRATEGY = new ConcreateStrategy.GenStrategy4Lock();
    private IGenStrategy genStrategy;

    public IDGenService(){}

    public IDGenService(IGenStrategy genStrategy){
        this.genStrategy = genStrategy;
    }

    private IGenStrategy getGenStrategy(){
        return Optional.ofNullable(genStrategy).orElse(DEFAULT_GENSTRATEGY);
    }

    public long generateId(String name){
        IDStructure structure = IDStructureFactory.getIDStruct(name);

        StrategyContext context = new StrategyContext(structure,getGenStrategy());

        return context.generateId();
    }

    public long generateId(){ return generateId("default"); }
}
package org.vision.github.springboot.tools.idgenerator.snowflake.service;

import org.junit.Test;
import org.vision.github.springboot.tools.idgenerator.snowflake.beanfactory.IDStructureFactory;
import org.vision.github.springboot.tools.idgenerator.snowflake.strategy.ConcreateStrategy;
import org.vision.github.springboot.tools.idgenerator.snowflake.util.IDUtils;

/**
 * @author user
 * @date 2018/1/6
 */
public class IDGenServiceTest {

    @Test public void testGenerateId(){
        IDGenService genService = new IDGenService(new ConcreateStrategy.GenStrategy4Atomic());
        long id = genService.generateId();
        System.out.println(String.format("采用atomic算法生成的ID为:%d",id));

        System.out.println(String.format("反解ID为:%s",IDUtils.convertID(id)));
    }

    @Test public void testGenerateIdByName(){
        IDGenService genService = new IDGenService();

        IDStructureFactory.registerIDStruct("d2", 2, 2, 12, 5, 5, 41);
        long id = genService.generateId("d2");
        System.out.println(String.format("采用默认算法(lock)生成的ID为:%d",id));

        System.out.println(String.format("反解ID为:%s",IDUtils.convertID(id,"d2")));
    }
}
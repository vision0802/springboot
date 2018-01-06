package org.vision.github.springboot.tools.idgenerator.snowflake.beanfactory;

import org.vision.github.springboot.tools.idgenerator.snowflake.bean.IDStructure;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ganminghui
 * @date 2018/1/6
 */
public class IDStructureFactory {

    public static final IDStructure DEFAULT_ID = new IDStructure(1, 1, 12, 5, 5, 41);

    public static final Map<String,IDStructure> cacheIDStructMap = new HashMap<>();

    public static IDStructure getIDStruct(String name){
        return cacheIDStructMap.getOrDefault(name,DEFAULT_ID);
    }

    public static void registerIDStruct(String name,IDStructure idStructure){
        cacheIDStructMap.put(name,idStructure);
    }

    public static void registerIDStruct(String name,int sequenceBit, int datacenterIdBit, int wokerIdBit, int timestampBit){
        cacheIDStructMap.put(name,new IDStructure(sequenceBit,datacenterIdBit,wokerIdBit,timestampBit));
    }

    public static void registerIDStruct(String name,long wokerId,long datacenterId,int sequenceBit, int datacenterIdBit, int wokerIdBit, int timestampBit){
        cacheIDStructMap.put(name,new IDStructure(wokerId,datacenterId,sequenceBit,datacenterIdBit,wokerIdBit,timestampBit));
    }
}
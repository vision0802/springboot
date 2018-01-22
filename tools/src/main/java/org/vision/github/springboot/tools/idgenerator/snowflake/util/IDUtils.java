package org.vision.github.springboot.tools.idgenerator.snowflake.util;

import org.vision.github.springboot.tools.idgenerator.snowflake.bean.IDStructure;
import org.vision.github.springboot.tools.idgenerator.snowflake.beanfactory.IDStructureFactory;

/**
 * @author ganminghui
 * @date 2018/1/5
 */
public class IDUtils {

    public static long generateId(IDStructure idStructure){
        long id = 0L;

        id |= idStructure.getSequence();

        id |= idStructure.getWorkerId() << idStructure.getWokerIdShift();

        id |= idStructure.getDatacenterId() << idStructure.getDatacenterIdShift();

        id |= (idStructure.getTimestamp() - IDStructure.TWEPOCH) << idStructure.getTimestampShift();

        return id;
    }

    public static IDStructure convertID (long id,String name){
        IDStructure structure = IDStructureFactory.getIDStruct(name);

        structure.setSequence(id & structure.getMaxSequence());
        structure.setWorkerId(id >>> structure.getWokerIdShift() & structure.getMaxWokerId());
        structure.setDatacenterId(id>>>structure.getDatacenterIdShift() & structure.getMaxDatacenterId());
        structure.setTimestamp((id>>>structure.getTimestampShift() & structure.getMaxTimestamp())+IDStructure.TWEPOCH);

        return structure;
    }

    public static IDStructure convertID (long id){
        return convertID(id,"default");
    }
}
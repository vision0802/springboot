package org.vision.github.springboot.mqserver.util;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;

import java.util.Objects;

/**
 * @author user
 * @date 2017/12/29
 */
public class CatUtil {

    public static Transaction createTransaction(String name,String type){
        return Cat.newTransaction(type,name);
    }

    public static void commitCatTransaction(Transaction transaction){
        if(!Objects.isNull(transaction)){
            transaction.setStatus("0");
            transaction.complete();
        }
    }
}
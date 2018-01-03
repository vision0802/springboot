package org.vision.github.springboot.mqserver.dao;

import org.apache.ibatis.annotations.Mapper;
import org.vision.github.springboot.mqserver.pojo.Message;

/**
 * @author ganminghui
 * @date 2017/12/21
 */
@Mapper public interface MessageDao {

    /**
     * 新增消息
     * @param var1 待新增的消息
     * @return 返回影响行数
     * */
    int addMqMessage(Message var1);

    /**
     * 根据messagesourceid查询主键
     * @param var1 消息主键编号
     * @return 返回影响行数
     * */
    int getIdByMessageId(String var1);

    /**
     * 根据messagesourceid查询消息对象
     * @param var1 消息编号
     * @return 返回消息对象
     * */
    Message getByMessageSourceId(String var1);
}
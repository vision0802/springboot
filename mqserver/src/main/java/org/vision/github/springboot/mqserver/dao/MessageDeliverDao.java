package org.vision.github.springboot.mqserver.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.vision.github.springboot.mqserver.dto.MessageDeliverDto;
import org.vision.github.springboot.mqserver.pojo.MessageDeliver;

import java.util.Date;
import java.util.List;

/**
 * @author ganminghui
 * @date 2017/12/21
 */
@Mapper public interface MessageDeliverDao {

    /**
     * 新增messagedeliver对象
     * @param deliver 待新增对象
     * @return 返回影响行数
     * */
    int addMessageDeliver(MessageDeliver deliver);

    /**
     *  根据messagesourceid\consumername\status更新status
     *  @param dto 待更新对象
     *  @return 返回影响行数
     * */
    int updateStatus(MessageDeliverDto dto);

    /**
     *  根据messagesourceid\consumername\status更新status\mark
     *  @param dto 待更新对象
     *  @return 返回影响行数
     * */
    int updateStatusAndMark(MessageDeliverDto dto);

    /**
     * 根据状态查询消息列表
     * @param status 状态
     * @param maxSize 最大条数
     * @return 返回列表信息
     * */
    List<MessageDeliver> getByStatusAndLimit(@Param("status") Integer status,@Param("maxSize") Integer maxSize);

    /**
     * 根据messagesourceid\consumername\version更新nextExecuteTime\versiob\hasTryTimes
     * @param var1 下次执行时间
     * @param var2 消息编号
     * @param var3 消费名称
     * @param var4 版本号
     * @return 返回影响行数
     * */
    int updateVersionAndNextExecuteTime(@Param("nextExecuteTime") Date var1, @Param("mqMessageSourceId") String var2, @Param("consumerName") String var3, @Param("version") Integer var4);

}
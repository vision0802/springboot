package org.vision.github.springboot.mqserver.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.vision.github.springboot.mqserver.pojo.MessageConsumer;

import java.util.List;

/**
 * The interface Message consumer dao.
 * @author ganminghui
 * @date 2017 /12/21
 */
@Mapper public interface MessageConsumerDao {

    /**
     * 根据是否处理标志查询消息列表
     *
     * @param isHandle 是否处理标志
     * @return 消息列表 by is handle
     */
    List<MessageConsumer> getByIsHandle(@Param("isHandle") Integer isHandle);

    /**
     * 根据consumeeName获取对象信息
     *
     * @param consumerName the consumer name
     * @return the by consumer name
     */
    MessageConsumer getByConsumerName(@Param("consumerName") String consumerName);

    /** 根据状态获取去重后的队列名称列表
     * @param status 状态
     * @return 返回列表信息
     */
    List<String> getDistinctQueueNamesByStatus(Integer status);

    /** 根据consumer名称和是否处理标志查询consumer总数
     * @param var1 consumer name
     * @param var2 isHandle
     * @return 返回总数
     * */
    int selectForLockByConsumerName(@Param("consumerName") String var1, @Param("isHandle") Integer var2);

    /**
     * 根据队列名称/状态/处理标志查询总数
     * @param var1 队列名称
     * @param var2 状态
     * @param var3 是否处理标志
     * @return 返回总数
     * */
    int countByQueueNameAndStatusAndIsHandle(@Param("queueName") String var1, @Param("status") Integer var2, @Param("isHandle") Integer var3);

    /**
     *  根据consumer名称更新状态和处理标志
     *  @param var1 消费名称
     *  @param var2 状态
     *  @param var3 是否处理标志
     * */
    void updateStatusAndHandleByConsumerName(@Param("consumerName") String var1, @Param("status") Integer var2, @Param("isHandle") Integer var3);

    /**
     * 根据consumer名称更新处理标志
     * @param var1 消费者名称
     * @param var2 是否处理标志
     * */
    void updateHandleByConsumerName(@Param("consumerName") String var1, @Param("isHandle") Integer var2);

    /**
     * 根据队列名称和状态查询列表信息
     * @param var1 队列名称
     * @param var2 状态
     * @return 返回列表信息
     * */
    List<MessageConsumer> getByQueueNameAndStatus(@Param("queueName") String var1, @Param("status") Integer var2);
}
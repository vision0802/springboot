package org.vision.github.springboot.mqserver.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.vision.github.springboot.mqserver.pojo.MessageConsumer;

import java.util.List;

/**
 * @author ganminghui
 * @date 2017/12/21
 */
public interface MessageConsumerMapper {

    List<MessageConsumer> getByIsHandle(Integer isHandle);

    MessageConsumer getByConsumerName(String consumerName);

    List<String> getDistinctQueueNamesByStatus(Integer status);

    @Select({"select count(id) from tb_mq_consumer where consumer_name=#{consumerName,jdbcType=VARCHAR} and is_handle=#{isHandle,jdbcType=INTEGER} FOR UPDATE"})
    int selectForLockByConsumerName(@Param("consumerName") String var1, @Param("isHandle") Integer var2);

    @Select({"select count(id) from tb_mq_consumer where queue_name=#{queueName,jdbcType=VARCHAR} and status=#{status,jdbcType=INTEGER} and is_handle=#{isHandle,jdbcType=INTEGER}"})
    int countByQueueNameAndStatusAndIsHandle(@Param("queueName") String var1, @Param("status") Integer var2, @Param("isHandle") Integer var3);

    @Update({"update tb_mq_consumer set status=#{status,jdbcType=INTEGER},is_handle=#{isHandle,jdbcType=INTEGER} where consumer_name=#{consumerName,jdbcType=VARCHAR}"})
    void updateStatusAndHandleByConsumerName(@Param("consumerName") String var1, @Param("status") Integer var2, @Param("isHandle") Integer var3);

    @Update({"update tb_mq_consumer set is_handle=#{isHandle,jdbcType=INTEGER} where consumer_name=#{consumerName,jdbcType=VARCHAR}"})
    void updateHandleByConsumerName(@Param("consumerName") String var1, @Param("isHandle") Integer var2);

    List<MessageConsumer> getByQueueNameAndStatus(@Param("queueName") String var1, @Param("status") Integer var2);
}

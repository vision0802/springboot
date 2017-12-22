package org.vision.github.springboot.mqserver.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.vision.github.springboot.mqserver.pojo.Message;

/**
 * @author ganminghui
 * @date 2017/12/21
 */
public interface MessageMapper {

    @Insert({"insert into tb_mq_message(queue_name,message_source_id,mq_message,consumer_time) values(#{queueName,jdbcType=VARCHAR},#{messageSourceId,jdbcType=VARCHAR},#{mqMessage,jdbcType=VARCHAR},#{consumerTime,jdbcType=TIMESTAMP})"})
    int addMqMessage(Message var1);

    @Select({"select id from tb_mq_message where message_source_id=#{messageSourceId,jdbcType=VARCHAR}"})
    int getIdByMessageId(String var1);

    Message getByMessageSourceId(String var1);
}
package org.vision.github.springboot.mqserver.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.vision.github.springboot.mqserver.pojo.MessageListener;

import java.util.List;

/**
 * @author ganminghui
 * @date 2017/12/21
 */
public interface MessageListenerMapper {
    @Insert({"insert into tb_mq_listener(queue_name,server_name,create_time,status) values(#{queueName,jdbcType=VARCHAR,},#{serverName,jdbcType=VARCHAR},#{createTime,jdbcType=TIMESTAMP},#{status,jdbcType=VARCHAR})"})
    int addMqListener(MessageListener var1);

    List<MessageListener> getByStatusAndServerName(@Param("status") Integer var1, @Param("serverName") String var2);

    @Update({"update tb_mq_listener set status=#{status,jdbcType=INTEGER} where id=#{id,jdbcType=INTEGER}"})
    int updateStatusById(@Param("id") Integer var1, @Param("status") Integer var2);
}
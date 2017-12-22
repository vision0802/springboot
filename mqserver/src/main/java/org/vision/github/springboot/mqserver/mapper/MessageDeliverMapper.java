package org.vision.github.springboot.mqserver.mapper;

import org.apache.ibatis.annotations.Param;
import org.vision.github.springboot.mqserver.dto.MessageDeliverDto;
import org.vision.github.springboot.mqserver.pojo.MessageDeliver;

import java.util.Date;
import java.util.List;

/**
 * @author ganminghui
 * @date 2017/12/21
 */
public interface MessageDeliverMapper {

    int addMessageDeliver(MessageDeliver deliver);

    int updateStatus(MessageDeliverDto dto);

    int updateStatusAndMark(MessageDeliverDto dto);

    List<MessageDeliver> getByStatusAndLimit(@Param("status") Integer status,@Param("maxSize") Integer maxSize);

    int updateVersionAndNextExecuteTime(@Param("nextExecuteTime") Date var1, @Param("mqMessageSourceId") String var2, @Param("consumerName") String var3, @Param("version") Integer var4);

}
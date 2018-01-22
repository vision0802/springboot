package org.vision.github.springboot.mqserver.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.vision.github.springboot.mqserver.pojo.MessageListener;

import java.util.List;

/**
 * @author ganminghui
 * @date 2017/12/21
 */
@Mapper public interface MessageListenerDao {

    /**
     * 新增对象
     * @param var1 待新增的对象
     * @return 返回影响行数
     * */
    int addMqListener(MessageListener var1);

    /**
     * 根据servername\status查询列表信息
     * @param var1 状态
     * @param var2 服务名称
     * @return 返回列表信息
     * */
    List<MessageListener> getByStatusAndServerName(@Param("status") Integer var1, @Param("serverName") String var2);

    /**
     * 根据id更新状态
     * @param var1 编号
     * @param var2 状态
     * @return 返回影响行数
     * */
    int updateStatusById(@Param("id") Integer var1, @Param("status") Integer var2);
}
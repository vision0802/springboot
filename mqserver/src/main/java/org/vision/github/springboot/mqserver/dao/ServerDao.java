package org.vision.github.springboot.mqserver.dao;

import org.apache.ibatis.annotations.Mapper;
import org.vision.github.springboot.mqserver.pojo.Server;

import java.util.List;

/**
 * @author ganminghui
 * @date 2017/12/21
 */
@Mapper public interface ServerDao {

    /**
     * 根据服务名查询服务器总数
     * @param var1 服务名称
     * @return 返回影响行数
     * */
    int countByServerName(String var1);

    /**
     * 新增服务器
     * @param var1 待新增的服务
     * */
    void addServer(Server var1);

    /**
     * 查询服务器名称列表
     * @return 返回服务器名称列表
     * */
    List<String> getAllServerName();
}
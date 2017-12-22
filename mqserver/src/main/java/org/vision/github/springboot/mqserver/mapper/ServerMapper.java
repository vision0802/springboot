package org.vision.github.springboot.mqserver.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.vision.github.springboot.mqserver.pojo.Server;

import java.util.List;

/**
 * @author ganminghui
 * @date 2017/12/21
 */
public interface ServerMapper {
    /** 新增服务 @param server */
    @Select({"select count(0) from tb_server_list where server_name=#{serverName,jdbcType=VARCHAR}"})
    int countByServerName(String var1);

    @Insert({"insert into tb_server_list(server_name,create_time) values(#{serverName,jdbcType=VARCHAR},#{createTime,jdbcType=TIMESTAMP})"})
    void addServer(Server var1);

    @Select({"select server_name from tb_server_list"})
    List<String> getAllServerName();
}
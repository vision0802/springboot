CREATE DATABASE mqtask DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

CREATE USER 'mqtask'@'%' IDENTIFIED BY 'mqtask@liquan';

GRANT ALL PRIVILEGES ON mqtask.* TO 'mqtask'@'%';

FLUSH PRIVILEGES;

DROP TABLE IF EXISTS tb_mq_consumer;
DROP TABLE IF EXISTS tb_mq_message;
DROP TABLE IF EXISTS tb_message_deliver;
DROP TABLE IF EXISTS tb_server_list;
DROP TABLE IF EXISTS tb_mq_listener;

CREATE TABLE `tb_mq_consumer` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `consumer_name` varchar(50) NOT NULL COMMENT 'consumer名称',
  `consumer_desc` varchar(255) DEFAULT NULL COMMENT 'consumer描述',
  `queue_name` varchar(50) NOT NULL COMMENT '队列名称',
  `consumer_url` varchar(255) NOT NULL COMMENT '请求consumer的url',
  `url_params` varchar(255) DEFAULT NULL COMMENT 'url参数，json格式',
  `request_encoding` varchar(20) DEFAULT NULL COMMENT '请求编码方式',
  `request_method` varchar(10) DEFAULT NULL COMMENT '请求方式，get or post',
  `call_type` varchar(50) NOT NULL COMMENT '调用方式，eureka or http',
  `retry_times` int(11) DEFAULT '7' COMMENT '失败重试次数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `last_update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `version` int(11) DEFAULT NULL,
  `status` int(2) NOT NULL COMMENT 'consumer状态',
  `is_handle` int(2) NOT NULL COMMENT '是否处理',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_consumer_name` (`consumer_name`) USING BTREE,
  KEY `ix_status` (`status`) USING BTREE,
  KEY `ix_is_handle` (`is_handle`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

CREATE TABLE `tb_mq_message` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `queue_name` varchar(255) DEFAULT NULL COMMENT '队列名称',
  `message_source_id` varchar(255) DEFAULT NULL COMMENT '消息ID',
  `mq_message` varchar(512) DEFAULT NULL COMMENT '消息内容',
  `consumer_time` datetime DEFAULT NULL COMMENT '消费时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_message_source_id` (`message_source_id`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;


CREATE TABLE `tb_message_deliver` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `mq_message_source_id` varchar(255) DEFAULT NULL COMMENT 'tb_mq_message表的message_source_id',
  `consumer_name` varchar(50) DEFAULT NULL COMMENT 'consumer名称',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `last_update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `next_execute_time` datetime DEFAULT NULL COMMENT '下次执行时间',
  `retry_times` int(11) DEFAULT NULL COMMENT '重试次数',
  `has_try_times` int(11) DEFAULT '0' COMMENT '已经重试次数',
  `mark` text COMMENT '备注，也可能用于存放task执行错误的message',
  `status` int(2) DEFAULT NULL COMMENT '0:创建 1:成功 2:失败  3待重试',
  `version` int(11) DEFAULT NULL COMMENT '版本号，用于乐观锁',
  PRIMARY KEY (`id`),
  KEY `ix_status_nextexecutetime` (`status`,`next_execute_time`) USING BTREE,
  KEY `ix_message_source_id` (`mq_message_source_id`) USING BTREE,
  KEY `ix_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

CREATE TABLE `tb_server_list` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `server_name` varchar(100) NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_server_name` (`server_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

CREATE TABLE `tb_mq_listener` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `queue_name` varchar(50) DEFAULT NULL,
  `server_name` varchar(255) DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `status` int(2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

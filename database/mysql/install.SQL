# ************************************************************
# Sequel Pro SQL dump
# Version 4541
#
# http://www.sequelpro.com/
# https://github.com/sequelpro/sequelpro
#
# Host: 192.168.1.59 (MySQL 5.7.19)
# Database: graph_dev
# Generation Time: 2019-03-25 11:04:03 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table dc_env
# ------------------------------------------------------------

DROP TABLE IF EXISTS `dc_env`;

CREATE TABLE `dc_env` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '环境名字',
  `version_dict_id` int(11) DEFAULT NULL COMMENT '发布版本，关联sys_dict.id，字典KEY=ENV_VERSION，参考取值[FIC80，FIC70，CDH5.7.0，TDH5.2.15]',
  `security_enabled` char(1) COLLATE utf8_bin NOT NULL DEFAULT 'N' COMMENT '是否需要安全认证:Y-是;N-否',
  `user` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '认证用户，user-principal',
  `remark` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '备注',
  `enabled_flag` char(1) COLLATE utf8_bin NOT NULL DEFAULT 'Y' COMMENT '可用状态:Y-是;N-否',
  `created_by` varchar(50) COLLATE utf8_bin NOT NULL DEFAULT '',
  `created_dt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` varchar(50) COLLATE utf8_bin NOT NULL DEFAULT '',
  `updated_dt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='环境表';



# Dump of table dc_env_file
# ------------------------------------------------------------

DROP TABLE IF EXISTS `dc_env_file`;

CREATE TABLE `dc_env_file` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `env_id` int(11) DEFAULT NULL COMMENT '关联dc_env.id',
  `name` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '文件名',
  `content` blob COMMENT '文件内容',
  `enabled_flag` char(1) COLLATE utf8_bin NOT NULL DEFAULT 'Y' COMMENT '可用状态:Y-是;N-否',
  `created_by` varchar(50) COLLATE utf8_bin NOT NULL DEFAULT '',
  `created_dt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` varchar(50) COLLATE utf8_bin NOT NULL DEFAULT '',
  `updated_dt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='环境配置文件表';



# Dump of table dc_graph
# ------------------------------------------------------------

DROP TABLE IF EXISTS `dc_graph`;

CREATE TABLE `dc_graph` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `graph` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '图域名',
  `graph_name_cn` varchar(50) COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '图中文名',
  `remark` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '备注',
  `enabled_flag` char(1) COLLATE utf8_bin NOT NULL DEFAULT 'Y' COMMENT '可用状态:Y-是;N-否',
  `created_by` varchar(50) COLLATE utf8_bin NOT NULL DEFAULT '',
  `created_dt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` varchar(50) COLLATE utf8_bin NOT NULL DEFAULT '',
  `updated_dt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='图域名表';



# Dump of table dc_graph_store
# ------------------------------------------------------------

DROP TABLE IF EXISTS `dc_graph_store`;

CREATE TABLE `dc_graph_store` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `graph` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '图域名',
  `store_id` int(11) NOT NULL COMMENT 'dc_store.id',
  `store_type` varchar(30) COLLATE utf8_bin NOT NULL COMMENT '存储类型，取值[ES，GDB，HBASE]',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='图存储库关系表';



# Dump of table dc_schema
# ------------------------------------------------------------

DROP TABLE IF EXISTS `dc_schema`;

CREATE TABLE `dc_schema` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `graph` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '图域名',
  `schema` varchar(128) COLLATE utf8_bin NOT NULL COMMENT '实体/边',
  `schema_name_cn` varchar(128) COLLATE utf8_bin NOT NULL COMMENT '实体/边中文名',
  `type` varchar(30) COLLATE utf8_bin NOT NULL COMMENT '分类[VERTEX-实体,VERTEX_MAIN-主实体,EDGE-普通边,EDGE_DETAIL-明细边,EDGE_SUMMARY-汇总边,EDGE_DM-挖掘边]',
  `search_weight` tinyint(1) NOT NULL DEFAULT '-1' COMMENT '搜索权重，取值[-1~100]，其中[-1表示不存储ES]',
  `use_search` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否导入ES；1是，0否',
  `use_gdb` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否导入图数据库；1是，0否',
  `use_hbase` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否导入hbase；1是，0否',
  `directed` char(1) COLLATE utf8_bin NOT NULL DEFAULT 'Y' COMMENT '是否有方向:Y-是;N-否',
  `remark` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '备注',
  `modifiable` tinyint(1) NOT NULL DEFAULT '1' COMMENT '数据导入后SCHEMA定义不可修改；1可修改，0不可修改',
  `sequence` smallint(6) DEFAULT '1' COMMENT '序号',
  `enabled_flag` char(1) COLLATE utf8_bin NOT NULL DEFAULT 'Y' COMMENT '可用状态:Y-是;N-否',
  `created_by` varchar(50) COLLATE utf8_bin NOT NULL DEFAULT '',
  `created_dt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` varchar(50) COLLATE utf8_bin NOT NULL DEFAULT '',
  `updated_dt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='实体/边元数据表';



# Dump of table dc_schema_field
# ------------------------------------------------------------

DROP TABLE IF EXISTS `dc_schema_field`;

CREATE TABLE `dc_schema_field` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `graph` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '图域名',
  `schema` varchar(128) COLLATE utf8_bin NOT NULL COMMENT '实体/关系名',
  `field` varchar(128) COLLATE utf8_bin NOT NULL COMMENT '字段名',
  `field_name_cn` varchar(128) COLLATE utf8_bin NOT NULL COMMENT '字段中文名',
  `type` varchar(30) COLLATE utf8_bin NOT NULL COMMENT '数据类型，取值[STRING-字符串,LONG-整数,DOUBLE-浮点数,DATETIME-日期]',
  `search_weight` tinyint(1) NOT NULL DEFAULT '0' COMMENT '搜索权重，取值[0~100]',
  `is_main` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否为主字段，取值[0-否，1-是]',
  `modifiable` tinyint(1) NOT NULL DEFAULT '1' COMMENT '数据导入后SCHEMA定义不可修改；1可修改，0不可修改',
  `sequence` smallint(6) NOT NULL DEFAULT '1' COMMENT '序号',
  `enabled_flag` char(1) COLLATE utf8_bin NOT NULL DEFAULT 'Y' COMMENT '可用状态:Y-是;N-否',
  `created_by` varchar(50) COLLATE utf8_bin NOT NULL DEFAULT '',
  `created_dt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` varchar(50) COLLATE utf8_bin NOT NULL DEFAULT '',
  `updated_dt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='实体/边的字段元数据表';



# Dump of table dc_store
# ------------------------------------------------------------

DROP TABLE IF EXISTS `dc_store`;

CREATE TABLE `dc_store` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '数据源名字',
  `type` varchar(30) COLLATE utf8_bin NOT NULL COMMENT '数据源类型，取值[ES，GDB，HBASE，HDFS，HIVE]',
  `version_dict_id` int(11) DEFAULT NULL COMMENT 'sys_dict.id',
  `env_id` int(11) DEFAULT NULL COMMENT 'dc_env.id',
  `url` varchar(128) COLLATE utf8_bin NOT NULL COMMENT 'URL地址',
  `user` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '用户',
  `password` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT '密码',
  `remark` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '备注',
  `enabled_flag` char(1) COLLATE utf8_bin NOT NULL DEFAULT 'Y' COMMENT '可用状态:Y-是;N-否',
  `created_by` varchar(50) COLLATE utf8_bin NOT NULL DEFAULT '',
  `created_dt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` varchar(50) COLLATE utf8_bin NOT NULL DEFAULT '',
  `updated_dt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='数据源配置表';



# Dump of table dc_store_param
# ------------------------------------------------------------

DROP TABLE IF EXISTS `dc_store_param`;

CREATE TABLE `dc_store_param` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `store_id` int(11) NOT NULL COMMENT 'dc_store.id',
  `key` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '参数键,取值[pKey-参数Key,pNameCn-参数中文名称,pRequired-是否必填,pDefault-默认值,pHint-提示]',
  `value` varchar(256) COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '参数值',
  `enabled_flag` char(1) COLLATE utf8_bin NOT NULL DEFAULT 'Y' COMMENT '可用状态:Y-是;N-否',
  `created_by` varchar(50) COLLATE utf8_bin NOT NULL DEFAULT '',
  `created_dt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` varchar(50) COLLATE utf8_bin NOT NULL DEFAULT '',
  `updated_dt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='数据源参数配置表';



# Dump of table dc_task
# ------------------------------------------------------------

DROP TABLE IF EXISTS `dc_task`;

CREATE TABLE `dc_task` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `graph` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '图域名',
  `schema` varchar(128) COLLATE utf8_bin NOT NULL COMMENT '目标表，实体/关系名',
  `task_name` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '任务名称',
  `task_type` varchar(30) COLLATE utf8_bin NOT NULL COMMENT '任务类型[FILE-文件导入,HDFS-HDFS导入,HIVE-HIVE导入,FLUME-流式导入]',
  `task_state` tinyint(1) NOT NULL DEFAULT '1' COMMENT '任务状态[1-NORMAL-正常,2-PAUSED-暂停,3-TRIGGER-已触发,4-RUNNING-执行中]',
  `last_instance_id` int(11) NOT NULL DEFAULT '0' COMMENT '关联dc_task_instance.id，默认0-表示无示例',
  `remark` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '备注',
  `execution_type` varchar(30) COLLATE utf8_bin NOT NULL DEFAULT 'ONCE' COMMENT '执行方式[ONCE-单次执行,CRON-定时执行]',
  `cron` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT 'crontab调度表达式',
  `store_id` int(11) DEFAULT NULL COMMENT '数据源，关联dc_store.id',
  `source` varchar(3000) COLLATE utf8_bin NOT NULL COMMENT '源数据配置，[FILE|HDFS-hdfs地址,HIVE-SQL]',
  `operate_type` varchar(30) COLLATE utf8_bin NOT NULL COMMENT '操作类型[INIT-初始化,UPSERT-更新,DELETE-删除]',
  `error_mode` int(11) NOT NULL DEFAULT -1 COMMENT '错误处理方式，-1表示出错继续，大于或等于0表示错误行大于该值终止',
  `enabled_flag` char(1) COLLATE utf8_bin NOT NULL DEFAULT 'Y' COMMENT '可用状态:Y-是;N-否',
  `created_by` varchar(50) COLLATE utf8_bin NOT NULL DEFAULT '',
  `created_dt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` varchar(50) COLLATE utf8_bin NOT NULL DEFAULT '',
  `updated_dt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='任务表-数据接入';



# Dump of table dc_task_instance
# ------------------------------------------------------------

DROP TABLE IF EXISTS `dc_task_instance`;

CREATE TABLE `dc_task_instance` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `task_id` int(11) NOT NULL COMMENT '关联dc_task.id',
  `operate_dt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间，也可以用做统计时间',
  `operate_type` varchar(30) COLLATE utf8_bin NOT NULL COMMENT '操作类型[INIT-初始化,UPSERT-更新,DELETE-删除]',
  `state` tinyint(1) NOT NULL DEFAULT '1' COMMENT '任务实例运行状态[1-ready-待执行,2-running-执行中,3-finished-已完成,4-interrupted-异常中断]',
  `total_size` int(11) NOT NULL DEFAULT 0 COMMENT '总数据大小单位为Byte',
  `total_rows` int(11) NOT NULL DEFAULT 0 COMMENT '总行数',
  `error_rows` int(11) NOT NULL DEFAULT 0 COMMENT '错误行数-校验阶段',
  `hbase_affected_rows` int(11) NOT NULL DEFAULT 0 COMMENT 'hbase受影响的行数',
  `es_affected_rows` int(11) NOT NULL DEFAULT 0 COMMENT 'es受影响的行数',
  `gdb_affected_rows` int(11) NOT NULL DEFAULT 0 COMMENT 'gdb受影响的行数',
  `hbase_error_rows` int(11) NOT NULL DEFAULT 0 COMMENT 'hbase错误行数',
  `es_error_rows` int(11) NOT NULL DEFAULT 0 COMMENT 'es错误行数',
  `gdb_error_rows` int(11) NOT NULL DEFAULT 0 COMMENT 'gdb错误行数',
  `yarn_app_info` varchar(1000) COLLATE utf8_bin DEFAULT NULL COMMENT 'YARN任务监控信息，JSON格式自定义',
  `error_mode` int(11) NOT NULL DEFAULT -1 COMMENT '错误处理方式，-1表示出错继续，大于或等于0表示错误行大于该值终止',
  `enabled_flag` char(1) COLLATE utf8_bin NOT NULL DEFAULT 'Y' COMMENT '可用状态:Y-是;N-否',
  `created_by` varchar(50) COLLATE utf8_bin NOT NULL DEFAULT '',
  `created_dt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` varchar(50) COLLATE utf8_bin NOT NULL DEFAULT '',
  `updated_dt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='任务实例表-数据接入';


# Dump of table dc_task
# ------------------------------------------------------------

DROP TABLE IF EXISTS `dc_task_meta`;

CREATE TABLE `dc_task_meta` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `task_id` int(11) NOT NULL COMMENT '关联dc_task.id',
  `type` varchar(30) COLLATE utf8_bin NOT NULL COMMENT '映射类型[FIELD-按字段,SEQ-按源字段顺序]',
  `src_field` varchar(128) COLLATE utf8_bin NOT NULL COMMENT '源字段，如果是SQL也可以是序号',
  `dst_field` varchar(128) COLLATE utf8_bin NULL COMMENT '目标字段',
  `sequence` smallint(6) NOT NULL DEFAULT 1 COMMENT '序号，仅用做前台展示',
  `enabled_flag` char(1) COLLATE utf8_bin NOT NULL DEFAULT 'Y' COMMENT '可用状态:Y-是;N-否',
  `created_by` varchar(50) COLLATE utf8_bin NOT NULL DEFAULT '',
  `created_dt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` varchar(50) COLLATE utf8_bin NOT NULL DEFAULT '',
  `updated_dt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='任务元数据映射-数据接入';


# Dump of table dc_vertex_edge
# ------------------------------------------------------------

DROP TABLE IF EXISTS `dc_vertex_edge`;

CREATE TABLE `dc_vertex_edge` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `graph` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '图域名',
  `from_vertex` varchar(128) COLLATE utf8_bin NOT NULL COMMENT '起始实体schema',
  `to_vertex` varchar(128) COLLATE utf8_bin NOT NULL COMMENT '目标实体schema',
  `edge` varchar(128) COLLATE utf8_bin NOT NULL COMMENT '边/关系schema',
  `key` varchar(50) COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '由graph,from_vertex,to_vertex,edge组成的唯一MD5值',
  PRIMARY KEY (`id`),
  UNIQUE KEY `key` (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='实体与边关联表';


# Dump of table sys_config
# ------------------------------------------------------------

DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` varchar(128) NOT NULL COMMENT '分类，按子产品区分，取值[DMP-数据平台，GAP-分析平台]',
  `sub_type` varchar(128) NOT NULL COMMENT '子分类，按用途区分，如：URL',
  `key` varchar(128) NOT NULL COMMENT '键，如：gap.external.manager.url.graph-search-arango',
  `value` varchar(1000) NOT NULL COMMENT '值',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `enabled_flag` char(1) NOT NULL DEFAULT 'Y' COMMENT '可用状态:Y-是;N-否',
  `created_by` varchar(50) NOT NULL DEFAULT '',
  `created_dt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` varchar(50) NOT NULL DEFAULT '',
  `updated_dt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='系统配置';



# Dump of table sys_dict
# ------------------------------------------------------------

DROP TABLE IF EXISTS `sys_dict`;

CREATE TABLE `sys_dict` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `key` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '字典健，只能使用大写英文加下划线',
  `value` varchar(200) COLLATE utf8_bin NOT NULL COMMENT '字典值',
  `parent_id` int(11) NOT NULL COMMENT '父ID，根节点默认为0',
  `enabled_flag` char(1) COLLATE utf8_bin NOT NULL DEFAULT 'Y' COMMENT '可用状态:Y-是;N-否',
  `created_by` varchar(50) COLLATE utf8_bin NOT NULL DEFAULT '',
  `created_dt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` varchar(50) COLLATE utf8_bin NOT NULL DEFAULT '',
  `updated_dt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='数据字典';



# Dump of table sys_file
# ------------------------------------------------------------

DROP TABLE IF EXISTS `sys_file`;

CREATE TABLE `sys_file` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '文件名',
  `url` varchar(200) COLLATE utf8_bin NOT NULL COMMENT '文件存储路径',
  `store_type` varchar(30) COLLATE utf8_bin NOT NULL COMMENT '存储类型[LOCAL,HDFS]',
  `file_size` int(11) NOT NULL COMMENT '文件大小',
  `enabled_flag` char(1) COLLATE utf8_bin NOT NULL DEFAULT 'Y' COMMENT '可用状态:Y-是;N-否',
  `created_by` varchar(50) COLLATE utf8_bin NOT NULL DEFAULT '',
  `created_dt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` varchar(50) COLLATE utf8_bin NOT NULL DEFAULT '',
  `updated_dt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='系统文件（统一管理）';



# Dump of table sys_resource
# ------------------------------------------------------------

DROP TABLE IF EXISTS `sys_resource`;

CREATE TABLE `sys_resource` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '资源名称',
  `code` VARCHAR(50) DEFAULT NULL COMMENT '权限code,前端使用',
  `group` VARCHAR(30) DEFAULT NULL COMMENT '权限分组，eg:P,S',
  `type` varchar(30) COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '类型，按子产品或模块划分，取值[DMP-数据平台，GAP-分析平台，AP-应用平台]',
  `sub_type` varchar(30) COLLATE utf8_bin DEFAULT NULL COMMENT '子类型, 如:SERVICE',
  `parent_id` int(11) NOT NULL COMMENT '父ID，根节点默认为0',
  `url` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '资源访问URL地址',
  `remark` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '备注',
  `enabled_flag` char(1) COLLATE utf8_bin NOT NULL DEFAULT 'Y' COMMENT '可用状态:Y-是;N-否',
  `created_by` varchar(50) COLLATE utf8_bin NOT NULL DEFAULT '',
  `created_dt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` varchar(50) COLLATE utf8_bin NOT NULL DEFAULT '',
  `updated_dt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='资源';



# Dump of table sys_role
# ------------------------------------------------------------

DROP TABLE IF EXISTS `sys_role`;

CREATE TABLE `sys_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '角色名称',
  `code` varchar(128) DEFAULT NULL COMMENT '角色代码',
  `remark` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '备注',
  `enabled_flag` char(1) COLLATE utf8_bin NOT NULL DEFAULT 'Y' COMMENT '可用状态:Y-是;N-否',
  `created_by` varchar(50) COLLATE utf8_bin NOT NULL DEFAULT '',
  `created_dt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` varchar(50) COLLATE utf8_bin NOT NULL DEFAULT '',
  `updated_dt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='角色';



# Dump of table sys_role_resource
# ------------------------------------------------------------

DROP TABLE IF EXISTS `sys_role_resource`;

CREATE TABLE `sys_role_resource` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_id` int(11) NOT NULL COMMENT 'sys_role.id',
  `resource_id` int(11) NOT NULL COMMENT 'sys_resource.id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='角色与资源';



# Dump of table sys_user
# ------------------------------------------------------------

DROP TABLE IF EXISTS `sys_user`;

CREATE TABLE `sys_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_no` varchar(128) COLLATE utf8_bin NOT NULL COMMENT '用户名-登录',
  `password` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT '登录密码',
  `name` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '姓名',
  `phone` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '手机',
  `email` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT '邮箱',
  `user_source` varchar(30) NOT NULL COMMENT '用户来源:[SSO-单点登录,SYS_CREATION-系统创建]',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态，取值[1-正常，0-冻结]',
  `enabled_flag` char(1) COLLATE utf8_bin NOT NULL DEFAULT 'Y' COMMENT '可用状态:Y-是;N-否',
  `created_by` varchar(50) COLLATE utf8_bin NOT NULL DEFAULT '',
  `created_dt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` varchar(50) COLLATE utf8_bin NOT NULL DEFAULT '',
  `updated_dt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='用户';



# Dump of table sys_user_role
# ------------------------------------------------------------

DROP TABLE IF EXISTS `sys_user_role`;

CREATE TABLE `sys_user_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT 'sys_user.id',
  `role_id` int(11) NOT NULL COMMENT 'sys_role.id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='用户与角色';




/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

INSERT INTO `sys_role` (`id`, `name`, `remark`, `enabled_flag`, `created_by`, `created_dt`, `updated_by`, `updated_dt`)
VALUES
	(1,'超级管理员',NULL,'Y','1','2019-02-27 14:23:33','1','2019-02-27 14:26:45'),
	(2,'系统管理员',NULL,'Y','1','2019-02-26 14:49:28','1','2019-02-27 14:26:57'),
	(3,'数据管理员',NULL,'Y','1','2019-02-26 14:49:05','1','2019-02-27 14:27:10'),
	(4,'项目管理员',NULL,'Y','1','2019-04-11 17:41:45','1','2019-04-18 11:00:36'),
	(5,'普通用户',NULL,'Y','1','2019-04-18 10:59:25','1','2019-04-18 11:00:56');

INSERT INTO `sys_user` (`id`, `user_no`, `password`, `name`, `phone`, `email`, `status`, `enabled_flag`, `created_by`, `created_dt`, `updated_by`, `updated_dt`)
VALUES
  (-1,'$atlas$','SHA-1$1000$eSz6rWI5$5290f6747251346f6f09b33e5bb549b9b7c00c53','内置用户','','',0,'Y','','2019-03-04 10:14:06','','2019-03-04 10:19:35'),
	(1,'superadmin','SHA-1$1000$RBG0FMwC$282589594fb284e5eb18c863ea0f71b6c689db79','超级管理员','139xxxxxxx','abc@graph.com',0,'Y','','2019-03-04 10:14:06','','2019-03-04 10:19:35'),
	(2,'admin','SHA-1$1000$RBG0FMwC$282589594fb284e5eb18c863ea0f71b6c689db79','系统管理员','134xxxxxxxx','dddd@tiger.com',0,'Y','','2019-03-04 10:19:22','','2019-03-04 10:19:59'),
	(3,'dbadmin','SHA-1$1000$RBG0FMwC$282589594fb284e5eb18c863ea0f71b6c689db79','数据管理员','138xxxxxxxx','fff@graph.com',0,'Y','','2019-03-04 10:19:55','','2019-03-04 15:48:21');

INSERT INTO `sys_user_role` (`id`, `user_id`, `role_id`)
VALUES
  (-1,-1,1),
	(1,1,1),
	(2,2,2),
	(3,3,3),
	(4,1,2),
	(5,1,3),
	(6,2,3);

INSERT INTO `sys_dict` (`id`, `key`, `value`, `parent_id`, `enabled_flag`, `created_by`, `created_dt`, `updated_by`, `updated_dt`)
VALUES
	(1,'STORE_TYPE','ES',0,'Y','','2019-03-27 11:34:34','','2019-03-27 11:34:34'),
	(2,'VERSION','5.4.3',1,'Y','','2019-03-27 11:34:50','','2019-03-27 12:13:03'),
	(4,'STORE_TYPE','Hbase',0,'Y','','2019-03-27 11:34:34','','2019-03-29 12:14:13'),
	(6,'VERSION','1.2.0',4,'Y','','2019-03-27 11:35:02','','2019-03-27 14:53:37'),
	(7,'STORE_TYPE','HDFS',0,'Y','','2019-03-27 11:34:34','','2019-03-27 14:53:21'),
	(8,'VERSION','2.6.0',7,'Y','','2019-03-27 11:35:02','','2019-03-27 14:53:40'),
	(9,'STORE_TYPE','Hive',0,'Y','','2019-03-27 11:34:34','','2019-03-29 12:14:16'),
	(10,'VERSION','1.1.0',9,'Y','','2019-03-27 11:35:02','','2019-03-27 14:54:29'),
	(11,'STORE_TYPE','GDB',0,'Y','','2019-03-27 11:34:34','','2019-03-27 14:54:43'),
	(12,'VERSION','Atlas1.0',11,'Y','','2019-03-27 11:35:02','','2019-03-27 14:54:49'),
	(14,'ENV_VERSION','CDH5.7.0',0,'Y','','2019-03-29 11:56:04','','2019-03-29 15:45:08'),
	(15,'STORE_TYPE','Greenplum',0,'Y','','2019-03-29 11:56:04','','2019-05-06 14:11:40'),
	(16,'VERSION','GP4.3.22',15,'Y','','2019-03-29 11:56:04','','2019-05-05 11:03:10');

INSERT INTO `sys_config` (`id`, `type`, `sub_type`, `key`, `value`, `remark`, `enabled_flag`)
VALUES ('1', 'DMP', 'URL', 'graph-search-arango', 'http://192.168.1.101:10021/search/arango/api', '', 'Y');
INSERT INTO `sys_config` (`id`, `type`, `sub_type`, `key`, `value`, `remark`, `enabled_flag`)
VALUES ('2', 'DMP', 'URL', 'graph-api', 'http://192.168.1.57:10030/api', '', 'Y');
INSERT INTO `sys_config` (`id`, `type`, `sub_type`, `key`, `value`, `remark`, `enabled_flag`)
VALUES ('3', 'DMP', 'URL', 'graph-search-es', 'http://192.168.1.101:10022/search/es/api', '', 'Y');
INSERT INTO `sys_config` (`id`, `type`, `sub_type`, `key`, `value`, `remark`, `enabled_flag`)
VALUES ('4', 'DMP', 'URL', 'graph-search', 'http://192.168.1.101:10020/search/api', '', 'Y');
INSERT INTO `sys_config` (`id`, `type`, `sub_type`, `key`, `value`, `remark`, `enabled_flag`)
VALUES ('5', 'DMP', 'URL', 'graph-dc-hbase', 'http://192.168.1.57:10015/dc/hbase/api', '', 'Y');
INSERT INTO `sys_config` (`id`, `type`, `sub_type`, `key`, `value`, `remark`, `enabled_flag`)
VALUES ('6', 'DMP', 'URL', 'graph-dc-inbound-api-url', 'http://192.168.1.57:10010/dc/inbound/api/bulk', 'kafka对外的接口地址', 'Y');
INSERT INTO `sys_config` (`id`, `type`, `sub_type`, `key`, `value`, `remark`, `enabled_flag`)
VALUES ('7', 'DMP', 'URL', 'graph-dc-store', 'http://192.168.1.57:10017/dc/store', '', 'Y');

-- 功能资源权限表增加字段
-- ALTER TABLE `sys_resource` ADD COLUMN `group` VARCHAR(30) DEFAULT NULL COMMENT '权限分组，eg:P,S';
-- ALTER TABLE `sys_resource` ADD COLUMN `code` VARCHAR(50) DEFAULT NULL COMMENT '权限code,前端使用';

-- 清空功能资源权限表
DELETE FROM sys_resource;

-- 初始化系统模块权限列表
-- DELETE from sys_resource WHERE type = 'SYS';
INSERT INTO `sys_resource`(`id`, `name`, `type`, `sub_type`, `parent_id`, `url`, `remark`, `enabled_flag`, `created_by`, `created_dt`, `updated_by`, `updated_dt`, `group`, `code`) VALUES
 (1, '系统角色管理', 'SYS', '', 0, '/api/sys/role', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 16:36:39', '', 'sys_role_manger'),
 (20, '查看列表', 'SYS', '', 1, '/api/sys/role/findSysRoleList', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 16:36:40', '', 'find_sys_role'),
 (30, '新建角色', 'SYS', '', 1, '/api/sys/role/saveOrUpdate', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 16:37:48', '', 'save_sys_role'),
 (40, '编辑角色', 'SYS', '', 1, '/api/sys/role/saveOrUpdate', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 16:37:48', '', 'update_sys_role'),
 (50, '删除角色', 'SYS', '', 1, '/api/sys/role/delete', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 16:38:31', '', 'delete_sys_role'),
 (100, '用户管理', 'SYS', '', 0, '/api/sys/user', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 16:36:44', '', 'sys_user_manager'),
 (110, '查看列表', 'SYS', '', 100, '/api/sys/user/findPage', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 16:41:10', '', 'find_sys_user'),
 (120, '新增用户', 'SYS', '', 100, '/api/sys/user/saveOrUpdate', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 16:41:12', '', 'save_sys_user'),
 (130, '编辑用户', 'SYS', '', 100, '/api/sys/user/saveOrUpdate', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 16:41:12', '', 'update_sys_user'),
 (140, '删除用户', 'SYS', '', 100, '/api/sys/user/delete', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 16:41:13', '', 'delete_sys_user'),
 (150, '重置密码', 'SYS', '', 100, '/api/sys/user/resetPassword', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 16:41:14', '', 'reset_user_pwd');


-- 初始化数据平台权限列表
-- DELETE from sys_resource WHERE type = 'DMP';
INSERT INTO `sys_resource`(`id`, `name`, `type`, `sub_type`, `parent_id`, `url`, `remark`, `enabled_flag`, `created_by`, `created_dt`, `updated_by`, `updated_dt`, `group`, `code`) VALUES
(10000, '数据源管理', 'DMP', '', 0, '/dc/store', '', 'Y', '', '2019-01-07 18:30:15', '', '2019-02-27 12:04:22', '', 'data_source_manager'),
(10010, '查看列表', 'DMP', '', 10000, '/dc/store/store/findPage', '', 'Y', '', '2019-01-07 18:30:15', '', '2019-02-27 12:04:22', '', 'find_data_source'),
(10020, '环境管理', 'DMP', '', 10000, '/dc/store/env', '', 'Y', '', '2019-01-07 18:30:15', '', '2019-02-27 12:04:22', '', 'env_manager'),
(10021, '环境列表', 'DMP', '', 10020, '/dc/store/env/find', '', 'Y', '', '2019-01-07 18:30:15', '', '2019-02-27 12:04:22', '', 'find_env'),
-- (10022, '查看环境', 'DMP', '', 10020, '/dc/store/env/findById', '', 'Y', '', '2019-01-07 18:30:15', '', '2019-02-27 12:04:22', '', 'detail_env'),
(10023, '新建环境', 'DMP', '', 10020, '/dc/store/env/save', '', 'Y', '', '2019-01-07 18:30:15', '', '2019-02-27 12:04:22', '', 'save_env'),
(10024, '编辑', 'DMP', '', 10020, '/dc/store/env/update', '', 'Y', '', '2019-01-07 18:30:15', '', '2019-02-27 12:04:22', '', 'update_env'),
(10025, '删除', 'DMP', '', 10020, '/dc/store/env/delete', '', 'Y', '', '2019-01-07 18:30:15', '', '2019-02-27 12:04:22', '', 'delete_env'),
(10030, '新增数据源', 'DMP', '', 10000, '/dc/store/store/saveOrUpdate', '', 'Y', '', '2019-01-07 18:30:15', '', '2019-02-27 12:04:22', '', 'save_data_source'),
(10040, '编辑', 'DMP', '', 10000, '/dc/store/store/saveOrUpdate', '', 'Y', '', '2019-01-07 18:30:15', '', '2019-02-27 12:04:22', '', 'update_data_source'),
(10050, '删除', 'DMP', '', 10000, '/dc/store/store/delete', '', 'Y', '', '2019-01-07 18:30:15', '', '2019-02-27 12:04:22', '', 'delete_data_source'),
(10100, '元数据管理', 'DMP', '', 0, '/api/graph', '', 'Y', '', '2019-01-07 18:30:15', '', '2019-02-27 12:04:22', '', 'meta_manager'),
(10110, '资源库', 'DMP', '', 10100, '/api/graph', '', 'Y', '', '2019-01-07 18:30:15', '', '2019-02-27 12:04:22', '', 'graph_mangar'),
(10111, '查看列表', 'DMP', '', 10110, '/api/graph/findGraphFrame', '', 'Y', '', '2019-01-07 18:30:15', '', '2019-02-27 12:04:22', '', 'find_graph'),
(10112, '新建资源库', 'DMP', '', 10110, '/api/graph/saveOrUpdate', '', 'Y', '', '2019-01-07 18:30:15', '', '2019-02-27 12:04:22', '', 'save_graph'),
(10113, '编辑', 'DMP', '', 10110, '/api/graph/saveOrUpdate', '', 'Y', '', '2019-01-07 18:30:15', '', '2019-02-27 12:04:22', '', 'update_graph'),
(10114, '删除', 'DMP', '', 10110, '/api/graph/delete', '', 'Y', '', '2019-01-07 18:30:15', '', '2019-02-27 12:04:22', '', 'delete_graph'),
(10120, '表管理', 'DMP', '', 10110, '/api/schema', '', 'Y', '', '2019-01-07 18:30:15', '', '2019-02-27 12:04:22', '', 'table_manager'),
(10121, '查看列表', 'DMP', '', 10120, '/api/schema/findPage', '', 'Y', '', '2019-01-07 18:30:15', '', '2019-02-27 12:04:22', '', 'find_table'),
(10122, '新建表', 'DMP', '', 10120, '/api/schema/saveOrUpdate', '', 'Y', '', '2019-01-07 18:30:15', '', '2019-02-27 12:04:22', '', 'save_table'),
(10123, '编辑表', 'DMP', '', 10120, '/api/schema/saveOrUpdate', '', 'Y', '', '2019-01-07 18:30:15', '', '2019-02-27 12:04:22', '', 'update_table'),
(10124, '删除表', 'DMP', '', 10120, '/api/schema/delete', '', 'Y', '', '2019-01-07 18:30:15', '', '2019-02-27 12:04:22', '', 'delete_table'),
(10130, '字段管理', 'DMP', '', 10120, '/api/field', '', 'Y', '', '2019-01-07 18:30:15', '', '2019-02-27 12:04:22', '', 'field_manager'),
(10131, '查看列表', 'DMP', '', 10130, '/api/field/findPage', '', 'Y', '', '2019-01-07 18:30:15', '', '2019-02-27 12:04:22', '', 'find_field'),
(10132, '新建字段', 'DMP', '', 10130, '/api/field/saveOrUpdate', '', 'Y', '', '2019-01-07 18:30:15', '', '2019-02-27 12:04:22', '', 'save_field'),
(10133, '编辑', 'DMP', '', 10130, '/api/field/saveOrUpdate', '', 'Y', '', '2019-01-07 18:30:15', '', '2019-02-27 12:04:22', '', 'update_field'),
(10134, '删除', 'DMP', '', 10130, '/api/field/delete', '', 'Y', '', '2019-01-07 18:30:15', '', '2019-02-27 12:04:22', '', 'delete_field'),
(10200, '数据接入', 'DMP', '', 0, '/api/task', '', 'Y', '', '2019-01-07 18:30:15', '', '2019-02-27 12:04:22', '', 'data_inboud'),
(10210, '查看列表', 'DMP', '', 10200, '/api/task/findTaskPage', '', 'Y', '', '2019-01-07 18:30:15', '', '2019-02-27 12:04:22', '', 'find_inboud_task'),
(10220, '新建任务', 'DMP', '', 10200, '/api/task/save', '', 'Y', '', '2019-01-07 18:30:15', '', '2019-02-27 12:04:22', '', 'save_inboud_task'),
(10230, '编辑', 'DMP', '', 10200, '/api/task/save', '', 'Y', '', '2019-01-07 18:30:15', '', '2019-02-27 12:04:22', '', 'update_inboud_task'),
(10240, '删除', 'DMP', '', 10200, '/api/task/delete', '', 'Y', '', '2019-01-07 18:30:15', '', '2019-02-27 12:04:22', '', 'delete_inboud_task'),
(10250, '操作', 'DMP', '', 10200, '/api/task', '', 'Y', '', '2019-01-07 18:30:15', '', '2019-02-27 12:04:22', '', 'operator_inboud_task'),
(10251, '执行/立即执行', 'DMP', '', 10250, '/api/task/runOnce', '', 'Y', '', '2019-01-07 18:30:15', '', '2019-02-27 12:04:22', '', 'start_inboud_task'),
(10252, '禁用', 'DMP', '', 10250, '/api/task/pause', '', 'Y', '', '2019-01-07 18:30:15', '', '2019-02-27 12:04:22', '', 'disable_inboud_task'),
(10253, '启用', 'DMP', '', 10250, '/api/task/resume', '', 'Y', '', '2019-01-07 18:30:15', '', '2019-02-27 12:04:22', '', 'enable_inboud_task'),
(10254, '终止', 'DMP', '', 10250, '/api/task/stop', '', 'Y', '', '2019-01-07 18:30:15', '', '2019-02-27 12:04:22', '', 'stop_inboud_task');



-- 初始化分析平台权限列表
-- DELETE FROM `sys_resource` WHERE type = 'GAP';
-- 系统权限
INSERT INTO `sys_resource`(`id`, `name`, `type`, `sub_type`, `parent_id`, `url`, `remark`, `enabled_flag`, `created_by`, `created_dt`, `updated_by`, `updated_dt`, `group`, `code`) VALUES
(20000, '项目列表', 'GAP', '', 0, '/gap/api/project', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:07', 'S', 'project_manager'),
(20010, '查看列表', 'GAP', '', 20000, '/gap/api/project/findPage', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:14', 'S', 'find_project'),
(20020, '改变项目状态', 'GAP', '', 20000, '/gap/api/project/updateStatus', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:20', 'S', 'update_project_status'),
(20030, '新建项目', 'GAP', '', 20000, '/gap/api/project/saveOrUpdate', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:18', 'S', 'save_project'),
(20040, '编辑项目', 'GAP', '', 20000, '/gap/api/project/saveOrUpdate', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:18', 'S', 'update_project'),
(20060, '删除项目', 'GAP', '', 20000, '/gap/api/project/delete', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:22', 'S', 'delete_project'),
(20070, '项目分配', 'GAP', '', 20000, '/gap/api/project/projectPrivileges', '', 'Y', '', '2019-06-20 16:51:55', '', '2019-06-20 16:55:44', 'S', 'project_privileges'),

(20100, '项目授权', 'GAP', '', 0, '/gap/api/project/graph', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:42:12', 'S', 'grant_project'),
(20110, '查看列表', 'GAP', '', 20100, '/gap/api/project/graph/findPage', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:42:16', 'S', 'find_project_grant'),
(20120, '添加项目', 'GAP', '', 20100, '/gap/api/project/graph/saveOrUpdate', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:42:19', 'S', 'save_project_grant'),
(20130, '编辑', 'GAP', '', 20100, '/gap/api/project/graph/saveOrUpdate', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:42:19', 'S', 'edit_project_grant'),
(20140, '表授权', 'GAP', '', 20100, '/gap/api/project/schema/findPage', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:42:27', 'S', 'find_project_schema'),
(20142, '授权', 'GAP', '', 20140, '/gap/api/project/schema/save', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:42:29', 'S', 'save_project_schema'),
(20143, '取消授权', 'GAP', '', 20140, '/gap/api/project/schema/delete', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:42:33', 'S', 'delete_project_schema'),
(20150, '改变授权状态', 'GAP', '', 20100, '/gap/api/project/graph/updateStatus', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:42:21', 'S', 'update_project_grant_status'),
(20160, '删除', 'GAP', '', 20100, '/gap/api/project/graph/delete', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:42:21', 'S', 'delete_project_grant'),
(20200, '权限映射', 'GAP', '', 0, '/gap/api/sys/mapping/table', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:42:12', 'S', 'permission_mapping_manger'),
(20210, '查看列表', 'GAP', '', 20200, '/gap/api/sys/mapping/table/find', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:42:16', 'S', 'find_permission_mapping'),
(20220, '新建映射表', 'GAP', '', 20200, '/gap/api/sys/mapping/table/saveOrUpdate', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:42:19', 'S', 'save_permission_mapping'),
(20230, '编辑', 'GAP', '', 20200, '/gap/api/sys/mapping/table/saveOrUpdate', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:42:21', 'S', 'update_permission_mapping'),
(20240, '删除', 'GAP', '', 20200, '/gap/api/sys/mapping/table/delete', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:42:24', 'S', 'delete_permission_mapping'),
(20250, '文件导入', 'GAP', '', 20200, '/gap/api/sys/mapping/value/importTemplate', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:42:27', 'S', 'import_permission_mapping');
-- (20260, '模板下载', 'GAP', '', 20200, '/gap/api/sys/mapping/value/exportTemplate', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:42:29', 'S', 'export_mapping_templete'),
-- (20270, '参数状态切换', 'GAP', '', 20200, '/gap/api/sys/mapping/value/updateStatus', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:42:33', 'S', 'update_mapping_status');


-- 项目权限
INSERT INTO `sys_resource`(`id`, `name`, `type`, `sub_type`, `parent_id`, `url`, `remark`, `enabled_flag`, `created_by`, `created_dt`, `updated_by`, `updated_dt`, `group`, `code`) VALUES
(21000, '项目管理', 'GAP', '', 0, '/gap/api/project', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:25', 'P', 'manager_project'),
-- (21010, '项目概览', 'GAP', '', 21000, '/gap/api/project/overview', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:29', 'P', 'overview_project'),
-- (21011, '查看列表', 'GAP', '', 21010, '/gap/api/project/findPage', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:29', 'P', 'overview_project_list'),

-- (21020, '组织管理', 'GAP', '', 21010, '/gap/api/project/org', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:32', 'P', 'project_org_manager'),
-- (21021, '组织列表', 'GAP', '', 21020, '/gap/api/project/org/find', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:36', 'P', 'find_project_org'),
-- (21022, '添加组织', 'GAP', '', 21020, '/gap/api/project/org/saveOrUpdate', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:49', 'P', 'save_project_org'),
-- (21023, '编辑组织', 'GAP', '', 21020, '/gap/api/project/org/saveOrUpdate', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:49', 'P', 'update_project_org'),
-- (21024, '删除组织', 'GAP', '', 21020, '/gap/api/project/org/delete', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:51', 'P', 'delete_project_org'),

(21030, '成员管理', 'GAP', '', 21000, '/gap/api/project/org/user', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:42:05', 'P', 'project_orgUser_manager'),
(21031, '查看列表', 'GAP', '', 21030, '/gap/api/project/org/user/find', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:42:07', 'P', 'find_project_orgUser'),
(21032, '改变成员状态', 'GAP', '', 21030, '/gap/api/project/org/user/updateStatus', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:42:10', 'P', 'update_project_orgUser_status'),
(21033, '添加成员', 'GAP', '', 21030, '/gap/api/project/org/user/saveOrUpdate', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:42:08', 'P', 'save_project_orgUser'),
(21034, '编辑成员', 'GAP', '', 21030, '/gap/api/project/org/user/saveOrUpdate', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:42:08', 'P', 'update_project_orgUser'),
(21035, '删除成员', 'GAP', '', 21030, '/gap/api/project/orgUser/delete', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:42:10', 'P', 'delete_project_orgUser'),

(21040, '角色管理', 'GAP', '', 21000, '/gap/api/project/role', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:53', 'P', 'project_role_manger'),
(21041, '角色列表', 'GAP', '', 21040, '/gap/api/project/role/findPage', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:56', 'P', 'find_project_role'),
(21042, '添加角色', 'GAP', '', 21040, '/gap/api/project/role/saveOrUpdate', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:42:00', 'P', 'save_project_role'),
(21043, '编辑角色', 'GAP', '', 21040, '/gap/api/project/role/saveOrUpdate', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:42:00', 'P', 'update_project_role'),
(21044, '删除角色', 'GAP', '', 21040, '/gap/api/project/role/delete', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:42:02', 'P', 'delete_project_role'),

(21050, '元数据', 'GAP', '', 21000, '/gap/api/project/', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:53', 'P', 'project_meta'),
(21051, '查看列表', 'GAP', '', 21050, '/gap/api/project/schema/findPage', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:56', 'P', 'find_project_meta'),

(21100, '图查询', 'GAP', '', 0, '/gap/api/search', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:25', 'P', 'gdb_search'),
(21110, '查询面板', 'GAP', '', 21100, '/gap/api/search/graph', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:25', 'P', 'gs_search_board'),
(21111, 'K层展开', 'GAP', '', 21110, '/gap/api/search/graph/**/kExpand', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:25', 'P', 'gs_k_expand'),
(21112, '最短路径', 'GAP', '', 21110, '/gap/api/search/graph/**/shortestPath', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:25', 'P', 'gs_shortestPath'),
(21113, '全路径', 'GAP', '', 21110, '/gap/api/search/graph/**/fullPath', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:25', 'P', 'gs_fullPath'),
(21114, 'GQL', 'GAP', '', 21110, '/gap/api/search/graph/**/graphSQL', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:25', 'P', 'gs_graphSQL'),

-- 右键菜单 SERVICE
(21120, '右键功能', 'GAP', '', 21100, '', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:25', 'P', 'gs_right_click'),
(21121, '标记/取消标记', 'GAP', '', 21120, '', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:25', 'P', 'gs_mark'),
(21122, '展开/折叠', 'GAP', '', 21120, '', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:25', 'P', 'gs_expand'),
(21123, '隐藏/显示', 'GAP', '', 21120, '', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:25', 'P', 'gs_hide'),
(21124, '图内路径', 'GAP', '', 21120, '', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:25', 'P', 'gs_path'),
-- (21125, '布局设置', 'GAP', '', 21120, '', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:25', 'P', 'layout'),
-- (21126, '前进/撤销', 'GAP', '', 21120, '', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:25', 'P', 'time_machine'),
-- (21125, '最短路径', 'GAP', '', 21120, '/gap/api/search/graph/**/shortestPath', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:25', 'P', 'gs_shortestPath'),
-- (21126, '全路径', 'GAP', '', 21120, '/gap/api/search/graph/**/graphSQL', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:25', 'P', 'gs_fullPath'),
(21130, '详情查看', 'GAP', '', 21100, '/gap/api/search/detail', '','Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:25', 'P', 'gs_detail'),
-- (21131, '实体详情', 'GAP', '', 21130, '/gap/api/search/detail/**', '','Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:25', 'P', 'gs_vertex_detail'),
-- (21132, '关系详情', 'GAP', '', 21130, '/gap/api/search/detail/**', '','Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:25', 'P', 'gs_edge_detail'),
(21140, '关系过滤', 'GAP', '', 21100, '/gap/api/search/detail/**', '','Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:25', 'P', 'edge_filter'),
(21150, '时间轴', 'GAP', '', 21100, '/gap/api/search/timeline', '','Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:25', 'P', 'use_timeline'),
(21160, '保存', 'GAP', '', 21100, '/gap/api/service/search/saveOrUpdate', '','Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:25', 'P', 'save_search'),
(21170, '服务发布', 'GAP', '', 21100, '/gap/api/service/service/saveOrUpdate', '','Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:25', 'P', 'gs_service_save'),
-- (21200, '服务管理', 'GAP', '', 0, '/gap/api/service', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:25', 'P', 'service_manager'),
-- (21210, '我的服务', 'GAP', '', 21200, '/gap/api/service/service', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:25', 'P', 'my_service_manager'),

-- (21220, '查看列表', 'GAP', '', 21110, '/gap/api/service/service/findPage', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:25', 'P', 'find_service'),
-- (21230, '操作', 'GAP', '', 21110, '/gap/api/service/service', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:25', 'P', 'service_operate'),
-- (21231, '上线/下线', 'GAP', '', 21230, '/gap/api/service/service/updateStatus', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:25', 'P', 'up_down_service'),
-- (21232, '注销', 'GAP', '', 21230, '/gap/api/service/service/updateStatus', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:25', 'P', 'stop_service'),
-- (21233, '阅览/API预览', 'GAP', '', 21230, '/gap/api/service/service/apiPreview', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:25', 'P', 'service_api_preview'),
-- (21240, '服务分组', 'GAP', '', 21110, '/gap/api/service/group', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:25', 'P', 'service_group_manager'),
-- (21241, '新建分组', 'GAP', '', 21240, '/gap/api/service/group/saveOrUpdate', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:25', 'P', 'add_service_group'),
-- (21242, '编辑', 'GAP', '', 21240, '/gap/api/service/group/saveOrUpdate', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:25', 'P', 'update_service_group'),
-- (21243, '删除', 'GAP', '', 21240, '/gap/api/service/group/delete', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:25', 'P', 'delete_service_group'),


-- (21250, '我的查询', 'GAP', '', 21200, '/gap/api/service/search', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:25', 'P', 'my_search_manager'),
-- (21260, '查看列表', 'GAP', '', 21250, '/gap/api/service/search/findPage', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:25', 'P', 'find_search'),
-- (21270, '操作', 'GAP', '', 21250, '/gap/api/service/search', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:25', 'P', 'search_operate'),
-- (21271, '编辑', 'GAP', '', 21270, '/gap/api/service/search/saveOrUpdate', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:25', 'P', 'add_search'),
-- (21272, '删除', 'GAP', '', 21270, '/gap/api/service/search/delete', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:25', 'P', 'delete_search'),
-- (21280, '查询分组', 'GAP', '', 21250, '/gap/api/service/group', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:25', 'P', 'search_group_manager'),
-- (21281, '新建分组', 'GAP', '', 21280, '/gap/api/service/group/saveOrUpdate', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:25', 'P', 'add_search_group'),
-- (21282, '编辑', 'GAP', '', 21280, '/gap/api/service/group/saveOrUpdate', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:25', 'P', 'update_search_group'),
-- (21283, '删除', 'GAP', '', 21280, '/gap/api/service/group/delete', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-06-20 15:41:25', 'P', 'delete_search_group'),

(21300, '可视化配置', 'GAP', '', 0, '/gap/api/style', '', 'Y', '', '2019-06-20 15:57:40', '', '2019-06-20 15:57:40', 'P', 'style'),
(21310, '实体配置', 'GAP', '', 21300, '/gap/api/style/vertex', '', 'Y', '', '2019-06-20 15:58:17', '', '2019-06-20 15:58:40', 'P', 'vertex_style'),
(21311, '查看列表', 'GAP', '', 21310, '/gap/api/style/vertex/findPage', '', 'Y', '', '2019-06-20 15:58:17', '', '2019-06-20 15:58:40', 'P', 'find_vertex_style'),
(21312, '恢复默认', 'GAP', '', 21310, '/gap/api/style/reset', '', 'Y', '', '2019-06-20 15:59:43', '', '2019-06-20 16:00:07', 'P', 'reset_vertex_style'),
(21313, '编辑', 'GAP', '', 21310, '/gap/api/style/vertex/save', '', 'Y', '', '2019-06-20 15:59:43', '', '2019-06-20 16:00:07', 'P', 'save_vertex_style'),
-- (21313, '修改展示名称', 'GAP', '', 21310, '/gap/api/style/vertex/save', '', 'Y', '', '2019-06-20 15:59:43', '', '2019-06-20 16:00:07', 'P', 'save_vertex_disformat_style'),
-- (21314, '样式配置', 'GAP', '', 21310, '/gap/api/style/vertex/save', '', 'Y', '', '2019-06-20 15:59:43', '', '2019-06-20 16:00:07', 'P', 'save_vertex_style_style'),
-- (21315, '颜色配置', 'GAP', '', 21310, '/gap/api/style/vertex/save', '', 'Y', '', '2019-06-20 15:59:43', '', '2019-06-20 16:00:07', 'P', 'save_vertex_color_style'),
-- (21316, '尺寸配置', 'GAP', '', 21310, '/gap/api/style/vertex/save', '', 'Y', '', '2019-06-20 15:59:43', '', '2019-06-20 16:00:07', 'P', 'save_vertex_size_style'),
-- (21317, '重置', 'GAP', '', 21310, '/gap/api/style/delete', '', 'Y', '', '2019-06-20 15:59:43', '', '2019-06-20 16:00:07', 'P', 'delete_vertex_size_style'),
-- (21318, '添加规则', 'GAP', '', 21310, '/gap/api/style/vertex/saveRule', '', 'Y', '', '2019-06-20 15:59:43', '', '2019-06-20 16:00:07', 'P', 'save_vertex_rule_style'),
(21320, '关系配置', 'GAP', '', 21300, '/gap/api/style/edge', '', 'Y', '', '2019-06-20 15:58:17', '', '2019-06-20 15:58:40', 'P', 'edge_style'),
(21321, '查看列表', 'GAP', '', 21320, '/gap/api/style/edge/findPage', '', 'Y', '', '2019-06-20 15:58:17', '', '2019-06-20 15:58:40', 'P', 'find_edge_style'),
(21322, '恢复默认', 'GAP', '', 21320, '/gap/api/style/reset', '', 'Y', '', '2019-06-20 15:59:43', '', '2019-06-20 16:00:07', 'P', 'reset_edge_style'),
(21323, '编辑', 'GAP', '', 21320, '/gap/api/style/edge/save', '', 'Y', '', '2019-06-20 15:59:43', '', '2019-06-20 16:00:07', 'P', 'save_edge_style'),
-- (21323, '修改展示名称', 'GAP', '', 21320, '/gap/api/style/edge/save', '', 'Y', '', '2019-06-20 15:59:43', '', '2019-06-20 16:00:07', 'P', 'save_edge_disformat_style'),
-- (21325, '颜色配置', 'GAP', '', 21320, '/gap/api/style/edge/save', '', 'Y', '', '2019-06-20 15:59:43', '', '2019-06-20 16:00:07', 'P', 'save_edge_color_style'),
-- (21326, '线条配置', 'GAP', '', 21320, '/gap/api/style/edge/save', '', 'Y', '', '2019-06-20 15:59:43', '', '2019-06-20 16:00:07', 'P', 'save_edge_size_style'),
-- (21327, '重置', 'GAP', '', 21320, '/gap/api/style/delete', '', 'Y', '', '2019-06-20 15:59:43', '', '2019-06-20 16:00:07', 'P', 'delete_edge_size_style'),
-- (21328, '添加规则', 'GAP', '', 21320, '/gap/api/style/edge/saveRule', '', 'Y', '', '2019-06-20 15:59:43', '', '2019-06-20 16:00:07', 'P', 'save_edge_rule_style'),
(21400, '时间轴配置', 'GAP', '', 0, '/gap/api/timeline', '', 'Y', '', '2019-07-02 11:21:13', '', '2019-07-02 12:09:08', 'P', 'timeline'),
(21410, '实体配置', 'GAP', '', 21400, '/gap/api/timeline/vertex', '', 'Y', '', '2019-07-02 11:22:12', '', '2019-07-02 12:09:10', 'P', 'vertex_timeline'),
(21411, '查看列表', 'GAP', '', 21410, '/gap/api/timeline/vertex/findPage', '', 'Y', '', '2019-07-02 11:23:07', '', '2019-07-02 12:09:12', 'P', 'find_vertex_timeline'),
(21412, '编辑', 'GAP', '', 21410, '/gap/api/timeline/save', '', 'Y', '', '2019-07-02 11:24:27', '', '2019-07-02 12:09:13', 'P', 'save_vertex_timeline'),
(21420, '关系配置', 'GAP', '', 21400, '/gap/api/timeline/edge', '', 'Y', '', '2019-07-02 11:28:04', '', '2019-07-02 12:09:15', 'P', 'edge_timeline'),
(21421, '查看列表', 'GAP', '', 21420, '/gap/api/timeline/edge/findPage', '', 'Y', '', '2019-07-02 11:29:20', '', '2019-07-02 12:09:17', 'P', 'find_edge_timeline'),
(21422, '编辑', 'GAP', '', 21420, '/gap/api/timeline/save', '', 'Y', '', '2019-07-02 11:29:28', '', '2019-07-02 12:09:20', 'P', 'save_edge_timeline'),


(22000, '右键功能', 'GAP', '', 0, '','', 'Y', '','2019-01-07 18:34:10', '', '2019-02-27 12:03:05',  'SERVICE', 'right_click'),
(22010, '展开/折叠', 'GAP', '', 22000, '', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-02-27 12:03:05','SERVICE','expand'),
(22011, '标记/取消标记', 'GAP', '', 22000, '', '', 'Y', '', '2019-01-07 18:34:10', '', '2019-02-27 12:03:05','SERVICE','mark'),
(22012, '隐藏/显示', 'GAP', '', 22000, '', '','Y', '', '2019-01-07 18:34:10', '', '2019-02-27 12:03:05', 'SERVICE','hide'),
(22013, '图内路径','GAP', '', 22000, '','', 'Y', '', '2019-01-07 18:34:10', '', '2019-02-27 12:03:05','SERVICE','path');
-- (22020, '布局设置', 'GAP', '', 0, '', '','Y', '', '2019-01-07 18:34:10', '', '2019-02-27 12:03:05', 'SERVICE','layout'),
-- (22030, '前进/撤销','GAP', '', 0, '', '','Y', '', '2019-01-07 18:34:10', '', '2019-02-27 12:03:05', 'SERVICE','time_machine');



-- 初始化系统角色-超级管理员权限
DELETE FROM `sys_role_resource` Where role_id = 1;
insert into `sys_role_resource`(`role_id`,`resource_id`)
select 1, id from `sys_resource` where `group` <> 'SERVICE';

-- 初始化系统角色-系统管理员权限
DELETE FROM `sys_role_resource` Where role_id = 2;
insert into `sys_role_resource`(`role_id`,`resource_id`)
select 2, id from `sys_resource` where `type` = 'SYS';

-- 初始化系统角色-数据管理员权限-数据平台
DELETE FROM `sys_role_resource` Where role_id = 3;
insert into `sys_role_resource`(`role_id`,`resource_id`)
select 3, id from `sys_resource` where `type` = 'DMP';
-- 初始化系统角色-数据管理员权限-分析平台
insert into `sys_role_resource`(`role_id`,`resource_id`)
select 3, id from `sys_resource` where `code`
in ('grant_project','find_project_grant','save_project_grant',
'delete_project_grant','save_project_schema','edit_project_grant',
'find_project_schema','delete_project_schema','update_project_grant_status');

-- 初始化系统角色-项目管理员权限[删除项目授权！！！]
DELETE FROM `sys_role_resource` Where role_id = 4;
insert into `sys_role_resource`(`role_id`,`resource_id`)
select 4, id from `sys_resource` where `type` = 'GAP' and `group` <> 'SERVICE'
 and `id` not in (select `id` from `sys_resource`
 where `code` in ('grant_project','find_project_grant','save_project_grant',
'delete_project_grant','save_project_schema','edit_project_grant',
'find_project_schema','delete_project_schema','update_project_grant_status'));
-- 菜单权限 -
-- insert into `sys_role_resource`(`role_id`,`resource_id`)
-- select 4, id from `sys_resource` where `parent_id` = 0;

-- 初始化系统角色-普通用户权限 [已经没有任何权限了]
DELETE FROM `sys_role_resource` Where role_id = 5;
-- insert into `sys_role_resource`(`role_id`,`resource_id`)
-- SELECT 5,id from `sys_resource` where `code`
-- in ('project_manager','gdb_search','style','timeline');

--



-- 本次调整：
-- 1.调整 时间轴【编辑】层级和列表层级相同
-- 2.删除 图详情 - 详情查看 下 【实体详情】和【关系详情】
-- 3.删除 右键功能 下【布局设置】和【前进/撤销】
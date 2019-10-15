DELETE FROM `dc_store` WHERE `name`='demo_graph';
DELETE FROM `dc_graph` WHERE `graph`='demo_graph';
DELETE FROM `dc_schema` WHERE `graph`='demo_graph';
DELETE FROM `dc_graph_store` WHERE `graph`='demo_graph';
DELETE FROM `dc_schema_field` WHERE `graph`='demo_graph';
DELETE FROM `dc_task_instance` WHERE task_id in (SELECT  ID FROM `dc_task` WHERE `graph`='demo_graph'  or `id`='16000');
DELETE FROM `dc_task_meta` WHERE task_id in (SELECT  ID FROM `dc_task` WHERE `graph`='demo_graph'  or `id`='16000');
DELETE FROM `dc_task` WHERE `graph`='demo_graph';
DELETE FROM `sys_file` WHERE `name`='demo_data.json';

INSERT INTO `dc_graph` (`id`, `graph`, `graph_name_cn`, `remark`, `enabled_flag`)
VALUES ('160000', 'demo_graph', 'demo_graph', 'sdsd', 'Y');

INSERT INTO `dc_store` (`id`, `name`, `type`, `env_id`, `url`, `user`, `password`, `remark`, `enabled_flag`)
VALUES ('160001', 'demo_graph', 'GDB', NULL, '192.168.1.176:8529', 'sss', 'sss', 'arango store remark', 'Y');
INSERT INTO `dc_store` (`id`, `name`, `type`, `env_id`, `url`, `user`, `password`, `remark`, `enabled_flag`)
VALUES ('160002', 'demo_graph', 'ES', NULL, '192.168.1.49:9300', 'sss', 'sss', 'arango store remark', 'Y');
INSERT INTO `dc_store` (`id`, `name`, `type`, `env_id`, `url`, `user`, `password`, `remark`, `enabled_flag`)
VALUES ('160003', 'demo_graph', 'Hbase', NULL, '192.168.1.16,192.168.1.17,192.168.1.18:2181', 'sss', 'sss', 'arango store remark', 'Y');

INSERT INTO `dc_graph_store` (`id`, `graph`, `store_id`, `store_type`)
VALUES ('160001', 'demo_graph', '160001', 'GDB');
INSERT INTO `dc_graph_store` (`id`, `graph`, `store_id`, `store_type`)
VALUES ('160002', 'demo_graph', '160002', 'ES');
INSERT INTO `dc_graph_store` (`id`, `graph`, `store_id`, `store_type`)
VALUES ('160003', 'demo_graph', '160003', 'Hbase');

INSERT INTO `dc_schema` (`id`, `graph`, `schema`, `schema_name_cn`, `type`, `search_weight`, `use_search`, `use_gdb`, `use_hbase`,  `remark`, `modifiable`, `sequence`, `enabled_flag`)
VALUES ('160000', 'demo_graph', 'demo_vertex', 'fsdf', 'VERTEX', '0', '1', '1', '1', '', '0', '0', 'Y');

INSERT INTO `dc_schema_field` (`id`, `graph`, `schema`, `field`, `field_name_cn`, `type`, `search_weight`, `is_main`, `modifiable`, `sequence`, `enabled_flag`)
VALUES ('160001', 'demo_graph', 'demo_vertex', 'object_key', 'object_key', 'STRING', '0', '0', '0', '0', 'Y');
INSERT INTO `dc_schema_field` (`id`, `graph`, `schema`, `field`, `field_name_cn`, `type`, `search_weight`, `is_main`, `modifiable`, `sequence`, `enabled_flag`)
VALUES ('160002', 'demo_graph', 'demo_vertex', 'demo_string_field', 'sdsdf', 'STRING', '0', '0', '1', '0', 'Y');
INSERT INTO `dc_schema_field` (`id`, `graph`, `schema`, `field`, `field_name_cn`, `type`, `search_weight`, `is_main`, `modifiable`, `sequence`, `enabled_flag`)
VALUES ('160003', 'demo_graph', 'demo_vertex', 'demo_long_field', 'sdfsdf', 'LONG', '0', '0', '1', '0', 'Y');
INSERT INTO `dc_schema_field` (`id`, `graph`, `schema`, `field`, `field_name_cn`, `type`, `search_weight`, `is_main`, `modifiable`, `sequence`, `enabled_flag`)
VALUES ('160004', 'demo_graph', 'demo_vertex', 'demo_double_field', 'sdfsd', 'DOUBLE', '0', '0', '1', '0', 'Y');
INSERT INTO `dc_schema_field` (`id`, `graph`, `schema`, `field`, `field_name_cn`, `type`, `search_weight`, `is_main`, `modifiable`, `sequence`, `enabled_flag`)
VALUES ('160005', 'demo_graph', 'demo_vertex', 'demo_date_field', 'sdsdf', 'DATETIME', '0', '0', '1', '0', 'Y');

INSERT INTO `dc_task` (`id`, `graph`, `schema`, `task_name`, `task_type`, `task_state`, `last_instance_id`, `remark`, `execution_type`, `cron`, `store_id`, `source`, `enabled_flag`, `operate_type`, `error_mode`)
VALUES ('160000', 'demo_graph', 'demo_vertex', 'demo_vertex', 'FILE', '4', '160000', NULL, 'ONCE', NULL, '160000', '{\"UPLOAD_FILE\":\"160000\"}', 'Y', 'UPSERT', '-1');

INSERT INTO `dc_task_instance` (`id`, `task_id`, `operate_dt`, `operate_type`, `state`, `total_size`, `total_rows`, `error_rows`, `hbase_affected_rows`, `es_affected_rows`, `gdb_affected_rows`, `yarn_app_info`, `enabled_flag`)
VALUES ('160000', '160000', '2019-05-30 18:31:24', 'UPSERT', '1', '9600', '100', '0', '0', '0', '0', NULL, 'Y');

INSERT INTO `sys_file` (`id`, `name`, `url`, `store_type`, `file_size`, `enabled_flag`)
VALUES ('160000', 'demo_data.json', '/tmp/task_1111111-1111-1111-1111-11111111.json', 'LOCAL', '9600', 'Y');



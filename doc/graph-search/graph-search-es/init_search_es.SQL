DELETE FROM `dc_store` WHERE `name`='crm_dev2' or `id`='17000';
DELETE FROM `dc_graph` WHERE `graph`='crm_dev2' or `id`='17000';
DELETE FROM `dc_schema` WHERE `graph`='crm_dev2' or `id`='17000';
DELETE FROM `dc_graph_store` WHERE `graph`='crm_dev2' or `id`='17000';

INSERT INTO `dc_graph` (`id`, `graph`, `graph_name_cn`, `remark`, `enabled_flag`)
VALUES ('170000', 'crm_dev2', 'crm_dev2', 'sdsd', 'Y');

INSERT INTO `dc_store` (`id`, `name`, `type`, `env_id`, `url`, `user`, `password`, `remark`, `enabled_flag`)
VALUES ('170001', 'crm_dev2', 'GDB', NULL, '192.168.1.176:8529', 'sss', 'sss', 'arango store remark', 'Y');
INSERT INTO `dc_store` (`id`, `name`, `type`, `env_id`, `url`, `user`, `password`, `remark`, `enabled_flag`)
VALUES ('170002', 'crm_dev2', 'ES', NULL, '192.168.1.49:9300', 'sss', 'sss', 'arango store remark', 'Y');
INSERT INTO `dc_store` (`id`, `name`, `type`, `env_id`, `url`, `user`, `password`, `remark`, `enabled_flag`)
VALUES ('170003', 'crm_dev2', 'Hbase', NULL, '192.168.1.16,192.168.1.17,192.168.1.18:2181', 'sss', 'sss', 'arango store remark', 'Y');

INSERT INTO `dc_graph_store` (`id`, `graph`, `store_id`, `store_type`)
VALUES ('170001', 'crm_dev2', '170001', 'GDB');
INSERT INTO `dc_graph_store` (`id`, `graph`, `store_id`, `store_type`)
VALUES ('170002', 'crm_dev2', '170002', 'ES');
INSERT INTO `dc_graph_store` (`id`, `graph`, `store_id`, `store_type`)
VALUES ('170003', 'crm_dev2', '170003', 'Hbase');

INSERT INTO `dc_schema` (`id`, `graph`, `schema`, `schema_name_cn`, `type`, `search_weight`, `use_search`, `use_gdb`, `use_hbase`,  `remark`, `modifiable`, `sequence`, `enabled_flag`)
VALUES ('170000', 'crm_dev2', 'Company', 'fsdf', 'VERTEX', '0', '1', '1', '1', '', '0', '0', 'Y');



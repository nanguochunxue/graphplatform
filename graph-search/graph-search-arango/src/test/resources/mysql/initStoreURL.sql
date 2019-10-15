INSERT INTO `graph_dev`.`dc_graph` (`id`, `graph`, `graph_name_cn`, `remark`, `enabled_flag`, `created_by`, `created_dt`, `updated_by`, `updated_dt`)
  VALUES ('9999', 'test', 'test', 'sdsd', 'Y', '', '2019-04-24 15:04:18', '', '2019-04-24 15:04:18');

INSERT INTO `graph_dev`.`dc_schema_field` (`id`,`graph`, `schema`, `field`, `field_name_cn`, `type`, `search_weight`, `is_main`, `modifiable`, `sequence`, `enabled_flag`, `created_by`, `created_dt`, `updated_by`, `updated_dt`)
  VALUES ('9999','test', 'schema_2222', 'date_field', '中文字段名', 'DATETIME', '2', '0', '1', '0', 'Y', '2', '2019-04-19 15:57:12', '', '2019-04-19 15:57:12');

INSERT INTO `graph_dev`.`dc_store` (`id`,`name`, `type`, `env_id`, `url`, `user`, `password`, `remark`, `enabled_flag`, `created_by`, `created_dt`, `updated_by`, `updated_dt`, `version_dict_id`)
  VALUES ('9999','test', 'GDB', '1', '192.168.1.176:8529', 'root', '', 'arango store remark', 'Y', '2', '2019-04-16 11:11:19', '', '2019-04-16 11:11:19', '12');

INSERT INTO `graph_dev`.`dc_graph_store` (`id`, `graph`, `store_id`, `store_type`)
  VALUES ('9999', 'test', '9999', 'GDB');

INSERT INTO `graph_dev`.`dc_schema_field` (`id`, `graph`, `schema`, `field`, `field_name_cn`, `type`)
  VALUES ('9999', 'test', 'schema_2222', 'date_field', '中文字段名', 'DATETIME');

INSERT INTO `graph_dev`.`dc_vertex_edge` (`id`, `graph`, `from_vertex`, `to_vertex`, `edge`) VALUES ('9999', 'test', 'Company', 'Person', 'te_guarantee');

INSERT INTO `graph_dev`.`dc_schema` (`id`, `graph`, `schema`, `schema_name_cn`, `type`) VALUES ('9991', 'test', 'Company', '公司', 'VERTEX');
INSERT INTO `graph_dev`.`dc_schema` (`id`, `graph`, `schema`, `schema_name_cn`, `type`) VALUES ('9992', 'test', 'Person', '法人', 'VERTEX');
INSERT INTO `graph_dev`.`dc_schema` (`id`, `graph`, `schema`, `schema_name_cn`, `type`) VALUES ('9993', 'test', 'te_invest', '法人', 'EDGE');
INSERT INTO `graph_dev`.`dc_schema` (`id`, `graph`, `schema`, `schema_name_cn`, `type`) VALUES ('9994', 'test', 'te_transfer', '法人', 'EDGE');
INSERT INTO `graph_dev`.`dc_schema` (`id`, `graph`, `schema`, `schema_name_cn`, `type`) VALUES ('9995', 'test', 'te_guarantee', '法人', 'EDGE');
INSERT INTO `graph_dev`.`dc_schema` (`id`, `graph`, `schema`, `schema_name_cn`, `type`) VALUES ('9996', 'test', 'te_officer', '法人', 'EDGE');

INSERT INTO `graph_dev`.`sys_config` (`id`, `type`, `sub_type`, `key`, `value`, `remark`)
VALUES ('1', 'DMP', 'URL', 'dmp.manager.url.graph-search-arango', 'http://localhost:10020/search/arango', '');

SELECT * FROM  dc_graph WHERE graph='test';
SELECT * FROM  dc_schema_field WHERE graph='test';
SELECT * FROM  dc_vertex_edge WHERE graph='test';
SELECT * FROM  dc_store WHERE name='test';
SELECT * FROM  dc_graph_store WHERE graph='test';



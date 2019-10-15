INSERT INTO `graph_dev`.`dc_store` (`id`, `name`, `type`, `env_id`, `url`, `user`, `password`, `remark`, )
VALUES ('6666', 'graph_ccb_dev', 'HBase', NULL, 'hbase://192.168.1.16,192.168.1.17,192.168.1.18:2181', NULL, NULL, NULL);

INSERT INTO `graph_dev`.`dc_graph_store` (`id`, `graph`, `store_id`, `store_type`)
VALUES ('6666', 'graph_ccb_dev', '6666', 'HBase');

INSERT INTO `graph_dev`.`dc_graph` (`id`, `graph`, `graph_name_cn`, `remark`)
VALUES ('6666', 'graph_ccb_dev', 'graph_ccb_dev', NULL, );


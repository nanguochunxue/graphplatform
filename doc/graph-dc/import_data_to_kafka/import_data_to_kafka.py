# -*- coding: utf-8 -*-
from kafka import KafkaProducer
import json
import time
import click
import os
import logging


# kafka_servers='192.168.1.17:9092'
# topic='haizhidev_zy.dc.data.graph_test.sue'
# batch_size=140
#total_count=5000000
# total_count=10
skip_count=0

pwd = os.getcwd()
os.environ['KRB5_CONFIG'] = pwd + '/krb5.conf'


@click.command()
@click.option("--kafka_servers", required=True, default='192.168.1.17:9092', help="example: 192.168.1.17:9092")
@click.option("--topic", required=True, help='example: haizhidev_zy.dc.data.graph_test.sue')
@click.option("--batch_size", default=100, help='每个批次包含的消息数量')
@click.option("--total_count", default=1000, help='发送的总消息数')
@click.option("--data_dir", required=True, help='example: /opt/performance/arangodb/data/sue.data.arango.json')
@click.option("--env", required=True, default='cdh', type=click.Choice(['cdh', 'fi']))
@click.option('--sasl_kerberos_domain_name', required=True, default='hadoop.hadoop.com')
@click.option('--debug', required=True, default='false', type=click.BOOL)
def run(kafka_servers, topic, batch_size, total_count, data_dir, env,
        sasl_kerberos_domain_name, debug):
    if debug:
        logging.basicConfig(level=logging.DEBUG)
    conf = {'bootstrap_servers': kafka_servers, 'acks': 'all'}
    if env == 'fi':
        conf['security_protocol'] = 'SASL_PLAINTEXT'
        conf['sasl_kerberos_domain_name'] = sasl_kerberos_domain_name
        conf['sasl_mechanism'] = 'GSSAPI'
    producer = KafkaProducer(**conf)

    topic_ary = topic.split('.')
    schema = topic_ary[-1]
    graph = topic_ary[-2]
    data = {
        "header": {
            "options": {
                "taskId": 1,
                "taskInstanceId": 1
            }
        },
        "graph": graph,
        "schema": schema,
        "operation": "CREATE_OR_UPDATE",
        "rows": []
    }
    start_time = time.time()
    with open(data_dir) as f:
        batch = []
        count = 0
        future = None
        batch_count = 0
        for line in f:
            count = count + 1
            if count <= skip_count:
                if count % 100000 == 0:
                    print(count, time.time()-start_time)
                continue
            line_obj = json.loads(line)
            obj = {}
            obj['object_key'] = line_obj['_record_id']
            obj['from_key'] = line_obj['_from']
            obj['to_key'] = line_obj['_to']
            obj['type'] = line_obj['type']
            batch.append(obj)
            if len(batch) >= batch_size:
                batch_count = batch_count + 1
                data['rows'] = batch
                batch = []
                future = producer.send(topic, json.dumps(data).encode('utf-8'))

                if batch_count % 1000 == 0:
                    print(count, time.time()-start_time)
                if batch_count % 1000 == 0 and future is not None:
                    future.get(timeout=60)
            if count >= total_count+skip_count:
                break

        if len(batch)>0:
            data['rows'] = batch
            batch = []
            future = producer.send(topic, json.dumps(data).encode('utf-8'))
        result = future.get(timeout=60)
        print(count, time.time()-start_time)

if __name__ == '__main__':
    run()

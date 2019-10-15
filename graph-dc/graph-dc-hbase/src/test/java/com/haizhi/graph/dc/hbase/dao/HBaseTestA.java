package com.haizhi.graph.dc.hbase.dao;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by chengangxiong on 2019/01/10
 */
public class HBaseTestA {

    public static void main(String[] args) throws IOException {

        Configuration config = HBaseConfiguration.create();
        config.addResource(new Path("/Users/haizhi/IdeaProjects/graph/graph-dc/graph-dc-hbase/src/test/resources/hbase-site-cdh.xml"));

        final Connection connection = ConnectionFactory.createConnection(config);

        final ExecutorService threadPool = Executors.newFixedThreadPool(1);
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1);

        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            final int basix = i;
                    final List mutator = new ArrayList<>();
                    for (int j = 0; j < 1000; j++) {
                        Put put = new Put(Bytes.toBytes("2000" + j + "test"));
                        put.addColumn(Bytes.toBytes("objects"), Bytes.toBytes("name"), Bytes.toBytes("hello"));
                        mutator.add(put);
                    }
                    try {

                        mutatorToHbase(connection, mutator);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                }
//
//            });
        }
        System.out.println("--------------------------");
        System.out.println(System.currentTimeMillis() - start);
        System.out.println("--------------------------");
    }

    static TableName tableName = TableName.valueOf("graph_one:schema_from");

    private static void mutatorToHbase(Connection connect, List list) throws IOException {

        BufferedMutatorParams params = new BufferedMutatorParams(tableName);
        BufferedMutator.ExceptionListener listeners = new BufferedMutator.ExceptionListener() {
            @Override
            public void onException(RetriesExhaustedWithDetailsException e, BufferedMutator mutator) throws
                    RetriesExhaustedWithDetailsException {
                for (int i = 0; i < e.getNumExceptions(); i++) {
                    System.out.println("插入失败 " + e.getRow(i) + ".");
                }
            }
        };
        params.listener(listeners);
        BufferedMutator bufferedMutator = connect.getBufferedMutator(params);
        bufferedMutator.mutate(list);
        bufferedMutator.close();
    }
}

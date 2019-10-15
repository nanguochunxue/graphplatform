package com.haizhi.graph.tag.analytics.engine.driver.handler;

import com.haizhi.graph.common.constant.FieldType;
import com.haizhi.graph.common.util.DateUtils;
import com.haizhi.graph.tag.analytics.bean.Partitions;
import com.haizhi.graph.tag.analytics.engine.conf.FlowTaskSchema;
import org.junit.Test;

/**
 * Created by chengmo on 2018/4/24.
 */
public class HiveSqlBuilderTest {

    @Test
    public void rebuildByPartitions(){
        String sql = "select t.name AS name,date_format(t.reg_date,'yyyy-MM-dd') AS statTime," +
                "count(1),min(t.reg_amount) from Company t " +
                "where t.reg_amount > 500000000 " +
                "and t.reg_date > date_sub(current_date(), 365*10) " +
                "group by t.name,date_format(t.reg_date,'yyyy-MM-dd')";
        Partitions partitions = new Partitions();
        //partitions.addPartition("day", "2018-04-23", "2018-04-24");
        partitions.addRangePartition("day", DateUtils.getYesterday(), null);
        sql = HiveSqlBuilder.rebuild(sql, partitions);
        System.out.println(sql);
    }

    @Test
    public void rebuildNonWhere(){
        String sql = "select t.name AS name,date_format(t.reg_date,'yyyy-MM-dd') AS statTime," +
                "count(1),min(t.reg_amount) from Company t";
        Partitions partitions = new Partitions();
        //partitions.addPartition("day", "2018-04-23", "2018-04-24");
        partitions.addRangePartition("day", DateUtils.getYesterday(), null);
        sql = HiveSqlBuilder.rebuild(sql, partitions);
        System.out.println(sql);
    }

    @Test
    public void rebuildWithUnionAll(){
        String sql = "select t.name AS name,date_format(t.reg_date,'yyyy-MM-dd') AS statTime," +
                "count(1),min(t.reg_amount) from Company t where t.reg_amount > 500000000" +
                " union all " +
                "select t.name AS name,date_format(t.reg_date,'yyyy-MM-dd') AS statTime," +
                "count(1),min(t.reg_amount) from Company t ";
        Partitions partitions = new Partitions();
        //partitions.addPartition("day", "2018-04-23", "2018-04-24");
        partitions.addRangePartition("day", DateUtils.getYesterday(), null);
        sql = HiveSqlBuilder.rebuild(sql, partitions);
        System.out.println(sql);
    }

    @Test
    public void selectByPartitions(){
        String graph = "crm_dev";
        FlowTaskSchema sch = this.getFlowTaskSchema();

        Partitions partitions = new Partitions();
        partitions.addPartition("day", "2018-04-23", "2018-04-24");

        String sql = HiveSqlBuilder.select(graph, sch, partitions);
        System.out.println(sql);
    }

    @Test
    public void selectByRangePartition(){
        String graph = "crm_dev";
        FlowTaskSchema sch = this.getFlowTaskSchema();

        Partitions partitions = new Partitions();
        partitions.addRangePartition("day", DateUtils.getYesterday(), null);

        String sql = HiveSqlBuilder.select(graph, sch, partitions);
        System.out.println(sql);
    }

    private FlowTaskSchema getFlowTaskSchema(){
        FlowTaskSchema sch = new FlowTaskSchema();
        sch.setSchema("Company");
        sch.addField("object_key", FieldType.STRING.name());
        sch.addField("reg_amount", FieldType.DOUBLE.name());
        sch.addField("reg_date", FieldType.DATETIME.name());
        return sch;
    }
}

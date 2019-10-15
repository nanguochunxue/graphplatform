package com.haizhi.graph.tag.analytics.util;

import org.junit.Test;

/**
 * Created by chengmo on 2018/4/9.
 */
public class SqlUtilsTest {

    @Test
    public void example1(){
        String sql = "select t.object_key AS objectKey," +
                "date_format(t.reg_date,'yyyy-MM-dd') AS statTime," +
                "t2.name1, t3.name1, count(1),min(t.capital) from Company t " +
                "left join table2 t2 on t2.name = t.name right join table3 t3 on t3.name = t.name " +
                "where (t.capital > $yearsAgo() or t.name rlike '.*(经营h范围|业务范围).*') " +
                "and t.name = 'hah'" +
                "group by t.object_key,date_format(t.reg_date,'yyyy-MM-dd')";
        System.out.println(SqlUtils.getProjections(sql));
        System.out.println(SqlUtils.getTableAndFields(sql));
    }

    @Test
    public void example2(){
        String sql = "select t.name AS name,date_format(t.reg_date,\'yyyy-MM-dd\') AS statTime," +
                "count(1),min(t.reg_amount) from Company t " +
                "where t.reg_amount > 500000000 " +
                "and t.reg_date > date_sub(current_date(), 365*10) " +
                "group by t.name,date_format(t.reg_date,\'yyyy-MM-dd\')";
        System.out.println(sql);
        System.out.println(SqlUtils.getProjections(sql));
        System.out.println(SqlUtils.getTableAndFields(sql));
    }

    @Test
    public void example3(){
        String sql = "select row_number()over(order by t.deposit_balance desc) as rank,t.company_key,t.deposit_balance from to_balance t";
        System.out.println(sql);
        System.out.println(SqlUtils.getProjections(sql));
        System.out.println(SqlUtils.getTableAndFields(sql));
    }

    @Test
    public void example4(){
        String sql = "select t.company_key,t.company_name from to_plaintiff_list t where t.filing_date > date_sub" +
                "(current_date(), 90) \n" +
                "union all\n" +
                "select t1.company_key,t1.company_name from to_defendant_list t1 where t1.filing_date > date_sub" +
                "(current_date(), 90)";
        System.out.println(sql);
        System.out.println(SqlUtils.getProjections(sql));
        System.out.println(SqlUtils.getTableAndFields(sql));
    }
}

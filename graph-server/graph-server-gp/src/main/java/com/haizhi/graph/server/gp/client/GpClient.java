package com.haizhi.graph.server.gp.client;

import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import org.springframework.stereotype.Repository;

import java.sql.*;

/**
 * Created by chengangxiong on 2019/05/06
 */
@Repository("gpClient")
public class GpClient {

    private static final GLog LOG = LogFactory.getLogger(GpClient.class);
    private static final String DRIVER = "org.postgresql.Driver";

    public void testConnect(String url, String user, String password){
        try {
            Class.forName(DRIVER);
            Connection conn = DriverManager.getConnection(url, user, password);

            PreparedStatement statement = conn.prepareStatement("SELECT 1;");
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())
                LOG.info(resultSet.getString(1));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

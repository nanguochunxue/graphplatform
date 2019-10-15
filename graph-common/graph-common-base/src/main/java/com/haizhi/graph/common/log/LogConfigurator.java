package com.haizhi.graph.common.log;

import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by chengmo on 2017/12/16.
 */
public class LogConfigurator {
    public static <T> void initialize(Class<T> clazz){
        URL url = clazz.getResource("/log4j2.xml");
        if (url == null){
            return;
        }
        initialize(url.getPath());
    }

    public static void initialize(String configLocation){
        ConfigurationSource source;
        try {
            source = new ConfigurationSource(new FileInputStream(configLocation));
            Configurator.initialize(null, source);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void initialize(InputStream inputStream){
        ConfigurationSource source;
        try {
            source = new ConfigurationSource(inputStream);
            Configurator.initialize(null, source);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

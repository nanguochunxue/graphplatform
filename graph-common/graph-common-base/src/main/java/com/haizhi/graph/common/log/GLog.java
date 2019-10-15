package com.haizhi.graph.common.log;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import java.text.MessageFormat;

/**
 * Created by chengmo on 2017/12/15.
 */
public class GLog {
    public Logger logger;
    public static final String FQCN = GLog.class.getName();
    public static boolean AUDIT_ENABLED = false;

    public GLog(Class clazz) {
        this.logger = (Logger) LogManager.getLogger(clazz);
    }

    public Logger getLogger() {
        return this.logger;
    }

    public void warn(Object obj) {
        if(obj instanceof Throwable){
            this.warn(null, (Throwable)obj);
            return;
        }
        logger.logIfEnabled(FQCN, Level.WARN, null, obj, null);
    }

    public void warn(String s, Throwable e) {
        logger.logIfEnabled(FQCN, Level.WARN, null, s, e);
    }

    public void warn(String pattern, Object... arguments) {
        logger.logIfEnabled(FQCN, Level.WARN, null, format(pattern, arguments));
    }

    public void warn(String pattern, Throwable e, Object... arguments) {
        logger.logIfEnabled(FQCN, Level.WARN, null, format(pattern, arguments), e);
    }

    public void info(Object obj) {
        if(obj instanceof Throwable){
            this.info(null, (Throwable)obj);
            return;
        }
        logger.logIfEnabled(FQCN, Level.INFO, null, obj, null);
    }

    public void info(String s, Throwable e) {
        logger.logIfEnabled(FQCN, Level.INFO, null, s, e);
    }

    public void info(String pattern, Object... arguments) {
        logger.logIfEnabled(FQCN, Level.INFO, null, format(pattern, arguments));
    }

    public void info(String pattern, Throwable e, Object... arguments) {
        logger.logIfEnabled(FQCN, Level.INFO, null, format(pattern, arguments), e);
    }

    public void audit(Object obj) {
        if (AUDIT_ENABLED){
            this.info(obj);
        }
    }

    public void audit(String s, Throwable e) {
        if (AUDIT_ENABLED){
            this.info(s, e);
        }
    }

    public void audit(String pattern, Object... arguments) {
        if (AUDIT_ENABLED){
            this.info(pattern, arguments);
        }
    }

    public void audit(String pattern, Throwable e, Object... arguments) {
        if (AUDIT_ENABLED){
            this.info(pattern, e, arguments);
        }
    }

    public void debug(Object obj) {
        if(obj instanceof Throwable){
            this.debug(null, (Throwable)obj);
            return;
        }
        logger.logIfEnabled(FQCN, Level.DEBUG, null, obj, null);
    }

    public void debug(String s, Throwable e) {
        logger.logIfEnabled(FQCN, Level.DEBUG, null, s, e);
    }

    public void debug(String pattern, Object... arguments) {
        logger.logIfEnabled(FQCN, Level.DEBUG, null, format(pattern, arguments));
    }

    public void debug(String pattern, Throwable e, Object... arguments) {
        logger.logIfEnabled(FQCN, Level.DEBUG, null, format(pattern, arguments), e);
    }

    public void error(Object obj) {
        if(obj instanceof Throwable){
            this.error(null, (Throwable)obj);
            return;
        }
        logger.logIfEnabled(FQCN, Level.ERROR, null, obj, null);
    }

    public void error(String s, Throwable e) {
        logger.logIfEnabled(FQCN, Level.ERROR, null, s, e);
    }

    public void error(String pattern, Object... arguments) {
        logger.logIfEnabled(FQCN, Level.ERROR, null, format(pattern, arguments));
    }

    public void error(String pattern, Throwable e, Object... arguments) {
        logger.logIfEnabled(FQCN, Level.ERROR, null, format(pattern, arguments), e);
    }

    public void trace(Object obj) {
        if(obj instanceof Throwable){
            this.trace(null, (Throwable)obj);
            return;
        }
        logger.logIfEnabled(FQCN, Level.TRACE, null, obj, null);
    }

    public void trace(String s, Throwable e) {
        logger.logIfEnabled(FQCN, Level.ERROR, null, s, e);
    }

    public void trace(String pattern, Object... arguments) {
        logger.logIfEnabled(FQCN, Level.ERROR, null, format(pattern, arguments));
    }

    public void trace(String pattern, Throwable e, Object... arguments) {
        logger.logIfEnabled(FQCN, Level.ERROR, null, format(pattern, arguments), e);
    }

    private static String format(String pattern, Object... arguments) {
        return MessageFormat.format(pattern, arguments);
    }
}

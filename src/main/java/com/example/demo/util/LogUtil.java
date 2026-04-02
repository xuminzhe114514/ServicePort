package com.example.demo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Deprecated
@Component
public class LogUtil {

    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    public static Logger getLogger(String name) {
        return LoggerFactory.getLogger(name);
    }

    public static void logBusiness(Logger logger, String operation, String userId, String details) {
        logger.info("[BUSINESS] Operation: {}, UserId: {}, Details: {}", operation, userId, details);
    }

    public static void logApiRequest(Logger logger, String method, String url, String userId, long duration) {
        logger.info("[API] {} {} - UserId: {} - Duration: {}ms", method, url, userId, duration);
    }

    public static void logError(Logger logger, String message, Throwable throwable) {
        logger.error("[ERROR] {}", message, throwable);
    }

    public static void logSql(Logger logger, String sql, Object... params) {
        if (logger.isDebugEnabled()) {
            logger.debug("[SQL] {}", sql);
            if (params != null && params.length > 0) {
                logger.debug("[SQL Params] {}", String.join(", ", 
                    java.util.Arrays.stream(params)
                        .map(String::valueOf)
                        .toArray(String[]::new)
                ));
            }
        }
    }
}

package com.example.demo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Deprecated
@Component
public class LogSystemValidator implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(LogSystemValidator.class);
    
    @Override
    public void run(String... args) throws Exception {
        logger.info("========================================");
        logger.info("              日志系统验证                 ");
        logger.info("========================================");
        logger.info("日志文件存储路径：d:/JavaProject/demo/logs");
        logger.debug("【DEBUG】调试级别日志 - 验证成功");
        logger.info("【INFO】信息级别日志 - 验证成功");
        logger.warn("【WARN】警告级别日志 - 验证成功");
        logger.error("【ERROR】错误级别日志 - 验证成功");
        
        LogUtil.logBusiness(logger, "SYSTEM_STARTUP", "SYSTEM", "日志系统初始化完成");
        LogUtil.logApiRequest(logger, "GET", "/api/test", "SYSTEM", 100);
        LogUtil.logSql(logger, "SELECT * FROM users WHERE id = ?", 1);
        
        logger.info("========================================");
        logger.info("                 验证完成                 ");
        logger.info("========================================");
        logger.info("日志文件说明:");
        logger.info("  - pharmacy-inventory-info.log: INFO 级别日志");
        logger.info("  - pharmacy-inventory-warn.log: WARN 级别日志");
        logger.info("  - pharmacy-inventory-error.log: ERROR 级别日志");
        logger.info("  - pharmacy-inventory-debug.log: DEBUG 级别日志");
        logger.info("  - pharmacy-inventory-all.log: 完整日志");
        logger.info("  - pharmacy-inventory-sql.log: SQL 日志");
        logger.info("  - pharmacy-inventory-business.log: 业务日志");
        logger.info("========================================");
    }
}

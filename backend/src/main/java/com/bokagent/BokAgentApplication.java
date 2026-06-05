package com.bokagent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.nio.charset.Charset;
import java.util.Map;

/**
 * BokAgent主应用类
 * AI Agent工作流编排系统
 */
@SpringBootApplication
@Slf4j
public class BokAgentApplication {

    public static void main(String[] args) {
        // 确保JVM使用UTF-8编码
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("sun.jnu.encoding", "UTF-8");

        SpringApplication app = new SpringApplication(BokAgentApplication.class);

        // 添加默认属性
        app.setDefaultProperties(Map.of(
            "spring.application.name", "BokAgent",
            "server.servlet.encoding.charset", "UTF-8",
            "server.servlet.encoding.enabled", "true",
            "server.servlet.encoding.force", "true"
        ));

        ConfigurableApplicationContext context = app.run(args);

        // 启动后验证编码
        logEncodingInfo();

        log.info("BokAgent应用启动成功! 🎉");
        log.info("支持中文和Emoji: 你好世界 ✨🚀💯");
    }

    private static void logEncodingInfo() {
        log.info("=== 编码信息 ===");
        log.info("file.encoding: {}", System.getProperty("file.encoding"));
        log.info("sun.jnu.encoding: {}", System.getProperty("sun.jnu.encoding"));
        log.info("user.language: {}", System.getProperty("user.language"));
        log.info("user.country: {}", System.getProperty("user.country"));
        log.info("Default Charset: {}", Charset.defaultCharset());
        log.info("支持中文测试: 你好世界 🎉🚀✨");
        log.info("================");
    }
}

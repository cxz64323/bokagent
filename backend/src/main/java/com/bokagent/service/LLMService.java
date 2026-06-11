package com.bokagent.service;

import lombok.extern.slf4j.Slf4j;
// import org.springframework.ai.chat.client.ChatClient;
// import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * LLM服务 - 集成Spring AI调用大模型
 * 注意：Spring AI 依赖暂时被注释，此服务暂不可用
 */
@Slf4j
@Service
public class LLMService {

    // @Autowired
    // private ChatClient chatClient;

    /**
     * 调用LLM生成回复
     * @param prompt 提示词
     * @param context 上下文数据
     * @return LLM的回复内容
     */
    public String chat(String prompt, Map<String, Object> context) {
        log.warn("LLM服务暂不可用 - Spring AI 依赖未配置");
        return "LLM服务暂未启用，请配置 Spring AI 依赖后重试";
        
        /*
        log.info("调用LLM，提示词长度: {}", prompt != null ? prompt.length() : 0);
        
        try {
            // 构建完整的提示词（包含上下文信息）
            String fullPrompt = buildFullPrompt(prompt, context);
            
            // 调用ChatClient
            String response = chatClient.prompt(fullPrompt).call().content();
            
            log.debug("LLM回复: {}", response);
            return response;
            
        } catch (Exception e) {
            log.error("LLM调用失败", e);
            throw new RuntimeException("LLM调用失败: " + e.getMessage(), e);
        }
        */
    }

    /**
     * 构建完整的提示词
     */
    private String buildFullPrompt(String prompt, Map<String, Object> context) {
        StringBuilder sb = new StringBuilder();
        
        // 添加上下文信息
        if (context != null && !context.isEmpty()) {
            sb.append("上下文信息:\n");
            context.forEach((key, value) -> {
                sb.append(key).append(": ").append(value).append("\n");
            });
            sb.append("\n");
        }
        
        // 添加用户提示词
        sb.append("任务:\n").append(prompt);
        
        return sb.toString();
    }
}

package com.bokagent.engine;

import lombok.Data;
import java.util.Map;

/**
 * 工作流执行结果
 */
@Data
public class ExecutionResult {
    private boolean success;
    private Map<String, Object> output;
    private String error;
    private long executionTime;

    public static ExecutionResult success(Map<String, Object> output, long executionTime) {
        ExecutionResult result = new ExecutionResult();
        result.setSuccess(true);
        result.setOutput(output);
        result.setExecutionTime(executionTime);
        return result;
    }

    public static ExecutionResult failure(String error, long executionTime) {
        ExecutionResult result = new ExecutionResult();
        result.setSuccess(false);
        result.setError(error);
        result.setExecutionTime(executionTime);
        return result;
    }
}

package com.haha.webflux.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "haha.chat-model")
public class ModelConfig {

    /**
     * https://ark.cn-beijing.volces.com/api/v3
     * https://ark.cn-beijing.volces.com/api/v3/bots
     * https://api.deepseek.com/v1
     * https://dashscope.aliyuncs.com/compatible-mode/v1
     */
//    private String baseUrl = "https://api.deepseek.com/v1";
    private String baseUrl = "https://dashscope.aliyuncs.com/compatible-mode/v1";

    /**
     *  /chat/completions
     */
    private String uri = "/chat/completions";

    /**
     *  "model", "bot-20250214102401-qkpbj", // R1联网
     *  "model", "ep-20250214103653-9lrrz", // R1
     *  "model", "ep-20250214103902-7p45t", // V3
     *  deepseek-chat  // deepseek v3
     *  deepseek-reasoner // deepseek r1
     *
     *  deepseek-v3 //百炼
     *  deepseek-r1 //百炼
     */
//    private String modelId = "deepseek-chat";
    private String modelId = "deepseek-r1";


    private String apiKey = "";

    private boolean stream = true;

}

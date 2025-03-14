package com.haha.webflux.factory;

import com.haha.webflux.vo.ChatBaseParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/***
 * @ClassName FlowChatContext
 * @Description 流式问答上下文
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlowChatContext {

    /**
     * 前端请求参数
     */
    private ChatBaseParam requestParam;

    /**
     * 问答id
     */
    private Long msgId;

    /**
     * 对话回答
     */
    private StringBuilder answer;

    /**
     * 思考开始时间
     */
    private long thinkStartTime = 0;

    /**
     * 对话对应索引
     */
    private StringBuilder docs;

    public FlowChatContext(ChatBaseParam requestParam) {
        this.requestParam = requestParam;
    }
}

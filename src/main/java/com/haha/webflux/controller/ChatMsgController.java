package com.haha.webflux.controller;

import com.haha.webflux.common.Result;
import com.haha.webflux.vo.ChatBaseParam;
import com.haha.webflux.vo.CreateSessionParam;
import com.haha.webflux.vo.SessionBaseParam;
import com.haha.webflux.vo.SessionVO;
import com.haha.webflux.service.IChatMsgService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.Duration;

@RestController
@RequestMapping("/chatMsg")
@CrossOrigin(origins = "*")
public class ChatMsgController {

    @Resource
    private IChatMsgService chatMsgService;

    /**
     * 创建会话
     */
    @PostMapping("/createSession")
    public Result<SessionVO> createSession(@Valid @RequestBody CreateSessionParam param) {
        return Result.ok(chatMsgService.createSession(param));
    }

    /**
     * 流式问答
     */
    @PostMapping(value = "/baseChat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> baseChat(@Valid @RequestBody ChatBaseParam param) {
        return chatMsgService.baseChat(param);
    }

    /**
     * 停止流式问答
     */
    @PostMapping("/stopChat")
    public Result<?> gptStopChat(@Valid @RequestBody SessionBaseParam param) {
        chatMsgService.stopChat(param);
        return Result.ok();
    }

    /**
     * 停止流式问答
     */
    @PostMapping("/historyMessage")
    public Result<?> getHistoryMessage(@Valid @RequestBody SessionBaseParam param) {
        chatMsgService.getHistoryMessage(param);
        return Result.ok();
    }


    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamText() {
        String fullText = "这是一个流式传输的示例文本|，用于演示打字效果。|"
                + "Spring WebFlux 的| Flux 可以高效处理流式数据。|"
                + "前端通过分块接收数|据并实现逐字显示效果。";

        // 逐字拆分，并过滤空字符
        return Flux.fromArray(fullText.split("\\|"))
                .delayElements(Duration.ofMillis(200))
                .log()
                .doOnNext(data -> System.out.println("Sending: " + data));
    }

}


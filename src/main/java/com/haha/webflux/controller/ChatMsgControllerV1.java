package com.haha.webflux.controller;

import com.haha.webflux.vo.ChatBaseParam;
import com.haha.webflux.service.IChatMsgService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/v1")
@CrossOrigin(origins = "*")
public class ChatMsgControllerV1 {

    @Resource
    private IChatMsgService chatMsgService;


    /**
     * 流式问答
     */
    @PostMapping(value = "/chat/completions", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> baseChat(@Valid @RequestBody ChatBaseParam param) {
        return chatMsgService.baseChat(param);
    }



}


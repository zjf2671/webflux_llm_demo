package com.haha.webflux.factory;

import com.haha.webflux.enums.FlowChatSceneEnum;
import reactor.core.publisher.Flux;

public interface IFlowChat {

    FlowChatSceneEnum getFlowChatEnum();

    Flux<String> request(FlowChatContext flowChatContext);

    void stop(String chatSessionId);
}

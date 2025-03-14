package com.haha.webflux.factory;

/***
 * @ClassName FlowChatCallBack
 * @Description 问答回调接口
 */
public interface FlowChatCallBack {

    /**
     * 流式处理过程中回调
     */
    String onNext(String data, FlowChatContext context);

    /**
     * 流式处理完成回调
     */
    void completed(FlowChatContext context);

    /**
     * 流式处理失败回调
     */
    void error(FlowChatContext context);
}

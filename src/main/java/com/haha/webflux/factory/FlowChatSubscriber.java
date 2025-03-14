package com.haha.webflux.factory;

import com.haha.webflux.common.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.Disposable;
import reactor.core.publisher.FluxSink;

import java.util.concurrent.ConcurrentHashMap;

/***
 * @ClassName FlowChatSubscriber
 * @Description 流式问答监听器
 */
@Slf4j
public class FlowChatSubscriber implements Subscriber<String>, Disposable {

    private final FluxSink<String> emitter;
    private Subscription subscription;
    private final FlowChatContext context;
    private final FlowChatCallBack callBack;
    private final ConcurrentHashMap<String, FlowChatSubscriber> subscriberMap;
    private boolean stopFlag = false;

    public void stop() {
        stopFlag = true;
    }

    public FlowChatSubscriber(FluxSink<String> emitter, FlowChatCallBack callBack,
                              FlowChatContext context, ConcurrentHashMap<String, FlowChatSubscriber> subscriberMap) {
        this.emitter = emitter;
        this.callBack = callBack;
        this.context = context;
        this.context.setAnswer(new StringBuilder());
        this.context.setDocs(new StringBuilder());
        this.subscriberMap = subscriberMap;
        log.info("subscriberMap in ChatSubscriber before remove: {}", JsonUtils.toJson(this.subscriberMap));
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        log.info("流式问答订阅开始, sessionId:{}, msgId:{}", context.getRequestParam().getChatSessionId(), context.getMsgId());
        // 订阅开始，初始化subscription
        this.subscription = subscription;
        // 请求接收一个数据项
        subscription.request(1);

    }

    @Override
    public void onNext(String data) {
        try {
            log.info("===============【api return】 data: {}", data);
            data = callBack.onNext(data, context);
        } catch (Exception e) {
            log.error("流式问答异常", e);
        } finally {
            // todo 临时打印日志
            log.info("=============== data: {}", data);
            if (stopFlag) {
                log.info("stopFlag is true");
                subscription.cancel();
                onComplete();
            } else {
                // 将数据发送给前端
                emitter.next(data);
                // 继续请求接收下一个数据项
                subscription.request(1);
            }
        }
    }

    @Override
    public void onError(Throwable t) {
        // 处理数据流完成后的回调逻辑
        try {
            callBack.error(context);
            log.error("流式问答异常, sessionId:{}, msgId:{}", context.getRequestParam().getChatSessionId(), context.getMsgId());
        } catch (Exception e) {
            log.error("流式问答异常", e);
        } finally {
            dispose();
        }
    }

    @Override
    public void onComplete() {
        // 处理数据流完成后的回调逻辑
        try {
            callBack.completed(context);
            log.info("流式问答订阅结束, sessionId:{}, msgId:{}", context.getRequestParam().getChatSessionId(), context.getMsgId());
        } catch (Exception e) {
            log.error("流式问答异常", e);
        } finally {
            dispose();
        }
    }

    @Override
    public void dispose() {
        emitter.complete();
        subscriberMap.remove(context.getRequestParam().getChatSessionId());
        log.info("subscriberMap in ChatSubscriber after remove: {}", JsonUtils.toJson(this.subscriberMap));
    }
}

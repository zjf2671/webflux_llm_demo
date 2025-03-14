package com.haha.webflux.factory;

import cn.hutool.core.collection.CollectionUtil;
import com.haha.webflux.common.CommonError;
import com.haha.webflux.common.JsonUtils;
import com.haha.webflux.config.ModelConfig;
import com.haha.webflux.enums.EStreamTypeEnum;
import com.haha.webflux.enums.EsIconTypeEnum;
import com.haha.webflux.vo.CreateSessionParam;
import com.haha.webflux.vo.EStreamDataVO;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/***
 * @ClassName AbstractFlowChatTemplate
 * @Description 流式问答抽象父类模板
 */
@Slf4j
@Component
public abstract class AbstractFlowChatTemplate implements IFlowChat, FlowChatCallBack {

    @Autowired
    private ModelConfig modelConfig;

    private WebClient webClient;

    @PostConstruct
    private void init() {
        webClient = WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();
    }

    private final ConcurrentHashMap<String, FlowChatSubscriber> subscriberMap = new ConcurrentHashMap<>();

    /**
     * 数据整理数据构建成Flux<String>
     */
    private Flux<String> buildDataProcessing(FlowChatContext context){

        // 数据整理中
        List<EStreamDataVO> dataVOS = doDataRequestBuild(context);
        if(CollectionUtil.isEmpty(dataVOS)){
            return null;
        }

        // 创建带延迟的 Flux（每100毫秒发送一个字符）
        Flux<String> dataResponse= Flux.interval(Duration.ofMillis(100))
                .take(dataVOS.size())
                .map(i -> JsonUtils.toJson(dataVOS.get(i.intValue())));
        return dataResponse;
    }

    /**
     * 问答入口
     */
    @Override
    public Flux<String> request(FlowChatContext context) {

        // 请求大模型问答之前的逻辑处理
        doPreRequest(context);

        // 请求大模型、处理回调逻辑
        return Flux.create(emitter -> {

            Boolean dataThinkingEnabled = context.getRequestParam().getDataThinkingEnabled();

            Flux<String> sequentialFlux = null;
            if(dataThinkingEnabled!= null && dataThinkingEnabled){
                // 数据整理中
                Flux<String> dataResponse = buildDataProcessing(context);
                if(dataResponse != null){
                    Flux<String> response = this.doRequest(context, buildRequest(context)).delaySubscription(dataResponse.then());
                    // 3. 合并流并保证顺序
                    sequentialFlux = Flux.concat(dataResponse, response)
                            .onErrorResume(e -> {
                                emitter.error(e);
                                return Flux.empty();
                            });
                }else{
                    sequentialFlux = this.doRequest(context, buildRequest(context));
                }

            }else{
                sequentialFlux = this.doRequest(context, buildRequest(context));
            }
            log.info("subscriberMap in AbstractChatService before put: {}", JsonUtils.toJson(subscriberMap));
            FlowChatSubscriber subscriber = new FlowChatSubscriber(emitter, this, context, subscriberMap);
            subscriberMap.put(context.getRequestParam().getChatSessionId(), subscriber);
            log.info("subscriberMap in AbstractChatService after put: " + JsonUtils.toJson(subscriberMap));
            sequentialFlux.subscribe(subscriber);
            emitter.onDispose(subscriber);
        });
    }

    @Override
    public void stop(String chatSessionId) {
        FlowChatSubscriber subscriber = subscriberMap.get(chatSessionId);
        if (subscriber == null) {
            return;
        }
        subscriber.stop();
    }

    /**
     * 保存对话内容
     */
    public Long createSession(CreateSessionParam sessionParam) {

        // 保存对话query

        return null;
    }

    /**
     * 保存对话内容
     */
    protected Long saveChatMsg(FlowChatContext context, String query) {

        // 保存对话query

        return null;
    }

    /**
     * 更新对话内容
     */
    protected void updateChatMsg(FlowChatContext context) {

        // 更新answer

    }

    /**
     * 构建响应参数
     */
    protected EStreamDataVO buildAnswer(String answer) {

//        res.put("answer", answer);
//        res.put("docs", Collections.emptyList());
//        res.put("process_docs", Collections.emptyList());
        return EStreamDataVO.builder().type(EStreamTypeEnum.text)
                .content(answer).build();
    }

    /**
     * 生成插入的Flux
     * @param context
     * @param countMap
     * @param data
     * @return
     */
    private Flux<String> generateInsertedFlux(FlowChatContext context, Map<String,AtomicInteger> countMap, String data) {
        JsonNode jsonNode = JsonUtils.toJsonNode(data);
        if (jsonNode == null || !jsonNode.isObject()) {
            return null;
        }
        ObjectNode objectNode = (ObjectNode) jsonNode;
        JsonNode choices = objectNode.get("choices");
        if(choices != null && !choices.isEmpty()) {
            JsonNode reasoning = choices.get(0).get("delta").get("reasoning_content");
            JsonNode answer = choices.get(0).get("delta").get("content");
            if(reasoning != null && reasoning.isTextual()){
                AtomicInteger reasoningContentCount = countMap.get("reasoning_content");
                if(reasoningContentCount == null){
                    reasoningContentCount = new AtomicInteger(0);
                    countMap.put("reasoning_content", reasoningContentCount);
                }
                reasoningContentCount.addAndGet(1);
            }
            if(answer != null && answer.isTextual()){
                AtomicInteger contentCount = countMap.get("content");
                if(contentCount == null){
                    contentCount = new AtomicInteger(0);
                    countMap.put("content", contentCount);
                }
                contentCount.addAndGet(1);
            }
            //判断是否是空字符串插入消息id
            if(answer != null && answer.isTextual() && answer.asText().isEmpty()){
                String insertStr = JSON.toJSONString(EStreamDataVO.builder().type(EStreamTypeEnum.meta).msgId("test msgId").build());
                return Flux.just(insertStr);
            }else if(reasoning != null && reasoning.isTextual() && reasoning.asText().isEmpty()){
                String insertStr = JSON.toJSONString(EStreamDataVO.builder().type(EStreamTypeEnum.meta).msgId("test msgId").build());
                return Flux.just(insertStr);
            }
        }
        if(countMap.size() == 2 && countMap.get("content").get() == 1){
            long thinkTime = (System.currentTimeMillis() - context.getThinkStartTime())/1000;
            String insertStr = JSON.toJSONString(EStreamDataVO.builder().type(EStreamTypeEnum.think).title(String.format(EsIconTypeEnum.two.getDesc(), thinkTime)).iconType(EsIconTypeEnum.two).content(":").build());
            return Flux.just(insertStr);
        }else{
            return null;
        }

    }

    /**
     * 请求大模型
     */
    private Flux<String> doRequest(FlowChatContext context, FLowChatRequest request) {
        log.info("请求大模型开始，URL:{}, 参数:{}", request.getUrl(), request.getJsonBody());
        Map<String,AtomicInteger> countMap = new ConcurrentHashMap<>();
        return webClient.post()
                .uri(request.getUrl())
                .header("Authorization", String.format("Bearer %s", modelConfig.getApiKey()))
                .accept(MediaType.TEXT_EVENT_STREAM)
                .bodyValue(request.getJsonBody())
                .retrieve()
                .bodyToFlux(String.class)
                .flatMap(str -> {
                    // 判断当前元素是否包含目标值
                    // 构建需要插入的Flux
                    Flux<String> insertedFlux = generateInsertedFlux(context, countMap, str);
                    if (insertedFlux != null) {
                        // 将当前元素与插入的Flux连接
                        return insertedFlux.concatWith(Flux.just(str));
                    } else {
                        return Flux.just(str);
                    }
                })
                .onErrorResume(WebClientResponseException.class, ex -> {
                    subscriberMap.remove(context.getRequestParam().getChatSessionId());
                    log.error("请求大模型接口异常", ex);
                    return Flux.just(JsonUtils.toJson(buildAnswer(CommonError.LLM_ERROR.getMsg())));
                })
                .onErrorResume(Throwable.class, ex -> {
                    subscriberMap.remove(context.getRequestParam().getChatSessionId());
                    log.error("系统异常", ex);
                    return Flux.just(JsonUtils.toJson(buildAnswer(CommonError.GLOBAL_ERROR.getMsg())));
                });
    }

    /**
     * 前置逻辑处理
     */
    protected abstract void doPreRequest(FlowChatContext context);

    /**
     * 数据构建逻辑处理
     */
    protected abstract List<EStreamDataVO> doDataRequestBuild(FlowChatContext context);

    /**
     * 构建大模型请求参数
     */
    protected abstract FLowChatRequest buildRequest(FlowChatContext context);
}

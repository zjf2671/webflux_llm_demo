package com.haha.webflux.factory.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.haha.webflux.common.JsonUtils;
import com.haha.webflux.config.ModelConfig;
import com.haha.webflux.dto.ChatMessage;
import com.haha.webflux.dto.ChatRequest;
import com.haha.webflux.enums.ChatRoleEnum;
import com.haha.webflux.enums.EStreamTypeEnum;
import com.haha.webflux.enums.EsIconTypeEnum;
import com.haha.webflux.enums.FlowChatSceneEnum;
import com.haha.webflux.factory.AbstractFlowChatTemplate;
import com.haha.webflux.factory.FLowChatRequest;
import com.haha.webflux.factory.FlowChatContext;
import com.haha.webflux.vo.EStreamDataVO;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service("baseChatService")
public class BaseChatService extends AbstractFlowChatTemplate {


    @Autowired
    private ModelConfig modelConfig;

    public static Map<String, List<ChatMessage>> chatMsgMap = new ConcurrentHashMap<>();

    @Override
    public FlowChatSceneEnum getFlowChatEnum() {
        return FlowChatSceneEnum.BASE_CHAT;
    }

    @Override
    public String onNext(String data, FlowChatContext context) {

        // 解析响应数据项
        JsonNode jsonNode = JsonUtils.toJsonNode(data);
        if (jsonNode == null || !jsonNode.isObject()) {
            return data;
        }
        ObjectNode objectNode = (ObjectNode) jsonNode;
        JsonNode choices = objectNode.get("choices");
        if(choices != null && !choices.isEmpty()){
            JsonNode reasoning = choices.get(0).get("delta").get("reasoning_content");
            JsonNode answer = choices.get(0).get("delta").get("content");

            if(reasoning != null && reasoning.isTextual()){
                if(context.getThinkStartTime() == 0){
                    context.setThinkStartTime(System.currentTimeMillis());
                }
                return JSON.toJSONString(EStreamDataVO.builder().type(EStreamTypeEnum.think).title(EsIconTypeEnum.one.getDesc()).iconType(EsIconTypeEnum.one).content(reasoning.asText()).build());
            }
            if(answer != null && answer.isTextual()){
                // 拼接answer
                context.getAnswer().append(answer!=null? answer.asText():"");
                return JSON.toJSONString(EStreamDataVO.builder().type(EStreamTypeEnum.text).content(answer.asText()).build());
            }
        }
        if("[DONE]".equals(data)){
            data = "[DONE]";
        }

        return data;
    }


    @Override
    public void completed(FlowChatContext context) {
        ChatMessage answerContent = ChatMessage.builder().role(ChatRoleEnum.assistant).content(context.getAnswer().toString()).build();
        chatMsgMap.get(context.getRequestParam().getChatSessionId())
                .add(answerContent);

        // 更新对话
        updateChatMsg(context);
    }

    @Override
    public void error(FlowChatContext context) {
        ChatMessage answerContent = ChatMessage.builder().role(ChatRoleEnum.assistant).content(context.getAnswer().toString()).build();
        chatMsgMap.get(context.getRequestParam().getChatSessionId())
                .add(answerContent);
        // 更新对话
        updateChatMsg(context);
    }

    String testSystem = "|一、你是一个医疗科研智能体，主要工作包括：帮助我了解更多医学领域的科研知识、辅助我挖掘研究课题、协助我调研课题的可行性和临床价值、帮助我梳理研究方案、撰写相关文档等。" + System.lineSeparator() +
            "|二、你需要结合用户目前已收集肺癌患者数据，来回答用户的问题，当前用户所积累的数据情况如下：" + System.lineSeparator() +
            "|1. 肺癌患者数：2789例" + System.lineSeparator() +
            "|2. 性别分布：男性占比 63.01%（1763例），女性占比36.95%（1034例）" + System.lineSeparator() +
            "|3. 年龄分布：1961例患者大于61岁（占比70.3%），671例患者在46-60岁区间（占比24.1%），153例患者在19-45岁区间（占比5.5%），4例患者小于18岁（占比0.1%）；" + System.lineSeparator() +
            "|4. 首次诊断年龄分布：xxx例患者首次诊断时大于61岁（占比一样%），..." + System.lineSeparator() +
            "|5. 吸烟情况：226例患者承认吸烟（占比8.08%），319例患者否认吸烟（占比11.4%），其余患者（2238例，占比79.99%）吸烟情况不详" + System.lineSeparator() +
            "|6. 病理分型-分期分布：" + System.lineSeparator() +
            "|非小细胞癌 1768例（占比63.26%），小细胞癌 133例（占比4.75%），其余患者病理分型（895例，占比31.99%）不详；" + System.lineSeparator() +
            "|  - 1768例非小细胞癌中，病理分型为腺癌的患者共1294例（占比73.1%），其中I期240例（占比18.5%）、II期49例（占比3.79%）、III期88例（占比6.80%）、IV期491例（占比37.94%）、其余患者（406例，占比31.38%）分期不详；病理分型为鳞癌的患者工396例（占比22.40%），其中I期26例（占比6.57%）、II期36例（占比9.09%）、III期120例（占比30.30%）、IV期103例（26.01%）、其余患者（107例，占比27.02%）分期不详  " + System.lineSeparator() +
            "|  - 133例小细胞癌中，I期3例（占比2.26%）、II期3例（占比2.26%）、III期26例（占比19.55%）、IV期54例（占比40.6%）、其余患者（46例，占比34.6%）分期不详" + System.lineSeparator() +
            "|8. 基因检测情况：56.42%的患者有基因检测报告（1571例），其中，1143例（72.76%）检测出基因突变，428例（27.24%）未检测出基因突变；其余1227例患者（占比43.85%）缺少基因检测报告" + System.lineSeparator() +
            "|9. 基因突变情况：" + System.lineSeparator() +
            "|  1443例检测过EGFR突变的患者中，有657例阳性；" + System.lineSeparator() +
            "|  282例检测过TP53突变的患者中，有271例阳性；" + System.lineSeparator() +
            "|   ......" + System.lineSeparator() +
            "|10. 转移情况：" + System.lineSeparator() +
            "|  1414例患者出现了转移（占比50.54%)，其中，54.17%（766例）为寡转移、45.83%（648例）为多发转移；" + System.lineSeparator() +
            "|转移部位按照发生率排名分别为胸膜（967例）、对侧肺（452例）、骨（447例）、淋巴结（341例）、肝脏（141例）、肾上腺（127例）、脑（79例）..." + System.lineSeparator() +
            "|11. 首次治疗方式分布情况：" + System.lineSeparator() +
            "|  I期患者（290例）中，76.89%（223例）首次治疗方式为手术、x%首次治疗方式为、其余xxx例患者（占比yy%）缺少治疗数据" + System.lineSeparator() +
            "|  II期患者（103例）中，50%（51例）首次治疗方式为手术、x%..." + System.lineSeparator() +
            "|  III期患者（279例）中，50%（139例）首次治疗方式为药物联合、" + System.lineSeparator() +
            "|  IV期患者（780例）中，32.56%（254例）首次治疗方式为药物联合、29.36%（229例）首次治疗为靶向治疗" + System.lineSeparator() +
            "|三、在思考并给出解答时，你需要注意以下几点：" + System.lineSeparator() +
            "|   1. 上述数据是患者的总体情况，可能存在数据错误和数据缺失；" + System.lineSeparator() +
            "|   2. 研究设计时需要考虑后续数据获取的可行性和难易程度；" + System.lineSeparator() +
            "|   3. 总体情况也许不能代表亚组人群间的特征，要结合真实世界的人群特征思考研究方向的可行性" + System.lineSeparator()

//            四、我的问题如下：
//            1. 我是一个三甲医院的医生，没有充分的实验室资源，想要结合我所积累的临床数据进行真实世界研究
//            2. 请结合近5年的文献资料和研究热点，给我推荐几个适合我（的数据）的研究方向或课题，并说明可行性和临床价值
            ;

    @Override
    protected FLowChatRequest buildRequest(FlowChatContext context) {
        // 请求大模型API地址
        String url = modelConfig.getBaseUrl() + modelConfig.getUri();
        // 构建请求参数
        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setModel(modelConfig.getModelId());
        chatRequest.setStream(true);
        List<ChatMessage> chatMessages = chatMsgMap.get(context.getRequestParam().getChatSessionId());
        chatRequest.setMessages(chatMessages);
//        Map<String, Object> streamOptions = Map.of("include_usage", true);
//        chatRequest.setStream_options(streamOptions);
        return FLowChatRequest.builder().url(url).jsonBody(JSON.toJSONString(chatRequest)).build();
    }


    @Override
    protected void doPreRequest(FlowChatContext context) {

        String query = context.getRequestParam().getQuery();
        List<ChatMessage> chatMessages = chatMsgMap.get(context.getRequestParam().getChatSessionId());
        if(CollectionUtil.isEmpty(chatMessages)){
            chatMessages = new ArrayList<>();
        }

        ChatMessage userChatMessage = ChatMessage.builder().role(ChatRoleEnum.user).content(context.getRequestParam().getQuery()).build();
        chatMessages.add(userChatMessage);
        chatMsgMap.put(context.getRequestParam().getChatSessionId(), chatMessages);

        // 保存query
        super.saveChatMsg(context, query);
    }

    @Override
    protected List<EStreamDataVO> doDataRequestBuild(FlowChatContext context) {
        String chatSessionId = context.getRequestParam().getChatSessionId();
        List<ChatMessage> chatMessages = chatMsgMap.get(chatSessionId);
        long count = chatMessages.stream().filter(c -> c.getRole() == ChatRoleEnum.system).count();
        if(count > 0){
            return Lists.newArrayList();
        }
        String[] dataArr = testSystem.split("\\|");
        List<EStreamDataVO> dataVOS = Lists.newArrayList();
        for (String data : dataArr) {
            EStreamDataVO eStreamDataVO = EStreamDataVO.builder().type(EStreamTypeEnum.data).title(EsIconTypeEnum.three.getDesc()).iconType(EsIconTypeEnum.three).content(data).build();
            dataVOS.add(eStreamDataVO);
        }
        dataVOS.add(EStreamDataVO.builder().type(EStreamTypeEnum.data).title(EsIconTypeEnum.four.getDesc()).iconType(EsIconTypeEnum.four).content("").build());

        log.info("整理后的数据: {}", JsonUtils.toJson(dataVOS));

        Boolean dataThinkingEnabled = context.getRequestParam().getDataThinkingEnabled();
        if (dataThinkingEnabled != null && dataThinkingEnabled) {
            ChatMessage sysContent = ChatMessage.builder().role(ChatRoleEnum.system).content(testSystem).build();
            List<ChatMessage> sysChatMessages = Lists.newArrayList(sysContent);
            chatMessages.addAll(0, sysChatMessages);
            chatMsgMap.put(chatSessionId, chatMessages);
        }

        return dataVOS;
    }
}

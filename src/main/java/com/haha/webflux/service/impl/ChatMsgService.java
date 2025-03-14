package com.haha.webflux.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import com.haha.webflux.dto.*;
import com.haha.webflux.enums.FlowChatSceneEnum;
import com.haha.webflux.factory.FlowChatContext;
import com.haha.webflux.factory.FlowChatFactory;
import com.haha.webflux.factory.impl.BaseChatService;
import com.haha.webflux.service.IChatMsgService;
import com.haha.webflux.vo.ChatBaseParam;
import com.haha.webflux.vo.CreateSessionParam;
import com.haha.webflux.vo.SessionBaseParam;
import com.haha.webflux.vo.SessionVO;
import com.haha.webflux.dto.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/***
 * @ClassName ChatMsgService
 * @Description TODO
 */
@Slf4j
@Service
public class ChatMsgService implements IChatMsgService {

    @Resource
    private FlowChatFactory chatFactory;

    @Autowired
    private BaseChatService baseChatService;

    @Override
    public SessionVO createSession(CreateSessionParam param) {
        String query = param.getQuery();
        String title = StringUtils.substring(query, 0, 10);
        // 创建会话实例存入数据库
        baseChatService.createSession(param);
        return new SessionVO(UUID.fastUUID().toString(), title, DateUtil.formatDateTime(new Date()));
    }

    @Override
    public Flux<String> baseChat(ChatBaseParam param) {
        return chatFactory.getInstance(FlowChatSceneEnum.BASE_CHAT)
                .request(new FlowChatContext(param));
    }

    @Override
    public void stopChat(SessionBaseParam param) {
        chatFactory.getInstance(FlowChatSceneEnum.BASE_CHAT)
                .stop(param.getChatSessionId());
    }

    @Override
    public void getHistoryMessage(SessionBaseParam param) {
        List<ChatMessage> chatMessages = BaseChatService.chatMsgMap.get(param.getChatSessionId());
        if (chatMessages != null) {
            log.info("chatMessages: {}", chatMessages);
        }
    }
}

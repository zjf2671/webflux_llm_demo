package com.haha.webflux.service;

import com.haha.webflux.vo.ChatBaseParam;
import com.haha.webflux.vo.CreateSessionParam;
import com.haha.webflux.vo.SessionBaseParam;
import com.haha.webflux.vo.SessionVO;
import reactor.core.publisher.Flux;

/***
 * @ClassName IChatMsgService
 * @Description TODO
 */
public interface IChatMsgService {

    SessionVO createSession(CreateSessionParam param);

    Flux<String> baseChat(ChatBaseParam param);

    void stopChat(SessionBaseParam param);

    void getHistoryMessage(SessionBaseParam param);
}

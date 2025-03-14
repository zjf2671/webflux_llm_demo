package com.haha.webflux.dto;

import com.haha.webflux.enums.ChatRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



/**
 * @desc 聊天内容对象
 * @author  harryzhang
 * @create  2025/3/6
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {

    private ChatRoleEnum role;

    private String content;

}

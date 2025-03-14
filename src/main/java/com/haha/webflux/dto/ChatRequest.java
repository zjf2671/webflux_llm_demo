package com.haha.webflux.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRequest {

    private String model;

    private List<ChatMessage> messages;

    private boolean stream;

    private Map<String, Object> stream_options;

}

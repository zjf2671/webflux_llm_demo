package com.haha.webflux.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionVO implements Serializable {

    private static final long serialVersionUID = -6200401549914847663L;
    private String chatSessionId;
    private String title;
    private String createTime;
}

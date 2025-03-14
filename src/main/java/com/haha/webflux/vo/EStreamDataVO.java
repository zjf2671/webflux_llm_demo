package com.haha.webflux.vo;

import com.haha.webflux.enums.EStreamTypeEnum;
import com.haha.webflux.enums.EsIconTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @desc 流式问答数据
 * @author  harryzhang
 * @create  2025/3/6
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EStreamDataVO {

    private EStreamTypeEnum type;

    private String title;

    private String content;
    private String msgId;

    private EsIconTypeEnum iconType;



}

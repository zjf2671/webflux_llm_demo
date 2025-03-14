package com.haha.webflux.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JsonUtils {

    public static ObjectMapper mapper;


    public JsonUtils(@Autowired ObjectMapper mapper) {
        JsonUtils.mapper = mapper;
    }

    public static <T> String toJson(T object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (Throwable e) {
            log.error("convert to json error , parameter is {}, error is ", object.toString(), e);
        }
        return "";
    }

    public static <T> T fromJson(String string, Class<T> clazz) {
        try {
            return mapper.readValue(string, clazz);
        } catch (JsonProcessingException e) {
            log.error("json to obj error , parameter is {}, error is ", string, e);
        }
        return null;
    }

    public static <T> T fromJson(String string, TypeReference<T> reference) {
        try {
            return mapper.readValue(string, reference);
        } catch (JsonProcessingException e) {
            log.error("json to obj error , parameter is {}, error is ", string, e);
        }
        return null;
    }

    public static JsonNode toJsonNode(String jsonStr) {
        try {
            if(StringUtils.isNotBlank(jsonStr) && "[DONE]".equals(jsonStr)){
                return null;
            }
            return mapper.readTree(jsonStr);
        } catch (JsonProcessingException e) {
            log.error("read json node error , parameter is {}, error is ", jsonStr, e);
        }
        return null;
    }
}

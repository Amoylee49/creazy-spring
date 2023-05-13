//package com.springcloud.common.core.util;
//
//import com.fasterxml.jackson.annotation.JsonInclude;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.DeserializationFeature;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.SerializationFeature;
//import lombok.extern.slf4j.Slf4j;
//
//import java.text.SimpleDateFormat;
//
//@Slf4j
//public class JsonUtils {
//    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
//
//    static {
//        //序列化的时候序列对象的所有属性
//        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//        //反序列化的时候如果多了其他属性,不抛出异常
//        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        //如果是空对象的时候,不抛异常
//        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
//        //取消时间的转化格式,默认是时间戳,可以取消,同时需要设置要表现的时间格式
//        OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
//        OBJECT_MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
//    }
//
//    public static String toStr(Object o) {
//        try {
//            return OBJECT_MAPPER.writeValueAsString(o);
//        } catch (JsonProcessingException e) {
//            log.error("json序列化成文本失败", e);
//        }
//        return null;
//    }
//
//    //    @SneakyThrows
//    public static void paseObject(String jsonStr) throws JsonProcessingException {
////        String string = OBJECT_MAPPER.writeValueAsString(jsonStr);
//        JsonNode jsonNode = OBJECT_MAPPER.readTree(jsonStr.toString());
//
////        List respMap = OBJECT_MAPPER.readValue(string, List.class);
//
//        jsonNode.fields().forEachRemaining(s -> {
//            System.out.println(s.getKey() + s.getValue());
//        });
//
//
//    }
//
//    public static <T> T toObj(String str, Class<T> clazz) {
//        try {
//            return OBJECT_MAPPER.readValue(str, clazz);
//        } catch (Exception e) {
//            log.error("序列化成对象失败，文本：{}。", str, e);
//        }
//        return null;
//    }
//
//    public static <T> T toObj(String str, TypeReference<T> valueTypeRef) {
//        try {
//            return OBJECT_MAPPER.readValue(str, valueTypeRef);
//        } catch (Exception e) {
//            log.error("序列化成对象失败，文本：{}。", str, e);
//        }
//        return null;
//    }
//}

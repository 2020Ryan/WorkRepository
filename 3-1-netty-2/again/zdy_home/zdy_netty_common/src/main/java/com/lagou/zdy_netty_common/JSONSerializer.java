package com.lagou.zdy_netty_common;

import com.alibaba.fastjson.JSON;

import java.io.IOException;

public class JSONSerializer implements Serializer {

    //json  转字节

    public byte[] serialize(Object object) throws IOException {
        return JSON.toJSONBytes(object);
    }
    //将字节数组转成对应对象

    public <T> T deserialize(Class<T> clazz, byte[] bytes) throws IOException {
        return JSON.parseObject(bytes,clazz);
    }
}

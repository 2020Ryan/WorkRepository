package com.lagou.io;

import java.io.InputStream;

public class Resource {

    //根据文件路径，加载字节输入流存到内存
    public static InputStream getResourceAsStream(String path){
        InputStream inputStream = Resource.class.getClassLoader().getResourceAsStream(path);
        return inputStream;

    }
}

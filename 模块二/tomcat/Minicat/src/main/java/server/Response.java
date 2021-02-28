package server;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 封装Response对象，需要依赖于OutputStream
 *
 * 该对象需要提供核⼼⽅法，输出html
 */
public class Response{
    private OutputStream outputStream;

    public Response(){

    }

    public Response(OutputStream outputStream){
        this.outputStream = outputStream;
    }

    //使用输出流输出自定字符串
    public void output(String content) throws IOException {
        outputStream.write(content.getBytes());
    }

    /**
     * 根据url获取绝对路径，根据绝对路径获取资源，最终输出
     */

    public void outputHtml(String path) throws IOException{

        //获取静态资源的文件的绝对路径
        String absolutePath  = StaticResourceUtil.getAbsolutePath(path);

        //创建文件对象
        File file = new File(absolutePath);
        if(file.exists()&&file.isFile()){
            //先读取，在输出
            StaticResourceUtil.outputStaticResource(new FileInputStream(file),outputStream);
        }else{
            //输出404
            output(HttpProtocolUtil.getHttpHeader404());
        }
    }
}
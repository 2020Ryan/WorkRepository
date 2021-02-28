package server;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 静态资源请求处理⼯具类
 */
public class StaticResourceUtil{

    //获取绝对路径
    public static String getAbsolutePath(String path){

        String absolutePath = StaticResourceUtil.class.getResource("/").getPath();
        return absolutePath.replaceAll("\\\\","/")+path;
    }

    /**
     * 读取静态资源⽂件输⼊流，通过输出流输出
     */

    public static void outputStaticResource(InputStream inputStream, OutputStream outputStream) throws IOException {

        int count = 0;
        while(count == 0){
            count = inputStream.available();

        }

        int resourceSize = count;
        //输出请求头在输出内容
        outputStream.write(HttpProtocolUtil.getHttpHeader200(resourceSize).getBytes());

        //输出内容
        long written = 0 ;//已读大小
        int byteSize = 1024;//每次缓冲大小
        byte[] bytes = new byte[byteSize];

        while(written < resourceSize){
            if(written + byteSize > resourceSize){
                byteSize = (int) (resourceSize - written);
                bytes = new byte[byteSize];
            }

            inputStream.read(bytes);
            outputStream.write(bytes);

            outputStream.flush();
            written += byteSize;
        }
    }
}
package server;

import java.io.IOException;

/**
 * HttpServlet抽象类定义
 */
public abstract class HttpServlet implements servlet{

    public abstract void doGet(Request request,Response response);

    public abstract void doPost(Request request,Response response);

    @Override
    public void service (Request request,Response response) throws IOException {
        if("GET".equalsIgnoreCase(request.getMethod())){
            doGet(request,response);
        }else{
            doPost(request,response);
        }
    }
}
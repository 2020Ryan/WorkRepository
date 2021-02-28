package server;

import server.mapper.Mapper;

import java.io.InputStream;
import java.net.Socket;
import java.util.Map;

public class RequestProcessor extends Thread{


    private Socket socket;
    private Mapper mapper;

    public RequestProcessor(Socket socket, Mapper mapper) {
        this.socket = socket;
        this.mapper = mapper;
    }


    @Override
    public void run() {
        try {

            InputStream inputStream = socket.getInputStream();

            //封装Request对象和Response对象
            Request request = new Request(inputStream);
            Response response = new Response(socket.getOutputStream());

            String host = request.getHost();
            String url = request.getUrl();

            GetServlet getServlet = new GetServlet();
            HttpServlet servlet = getServlet.getServlet(request, mapper);
            //静态资源 判断，处理
            if(servlet==null){
                response.outputHtml(request.getUrl());
            }else{
                //动态资源请求
                servlet.service(request,response);
            }
            socket.close();
        } catch (Exception e) {
            //TODO: handle exception
            e.printStackTrace();
        }
    }
}


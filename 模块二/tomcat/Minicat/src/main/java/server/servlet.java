package server;

public interface servlet {
    void init() throws Exception;
    void destory() throws Exception;
    void service(Request request,Response response) throws Exception;
}
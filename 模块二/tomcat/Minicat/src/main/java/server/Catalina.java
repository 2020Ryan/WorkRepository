package server;

import java.util.Map;

public class Catalina {


    public void load(Map<String, HttpServlet> servletMap) {

        Server server = new Server();
        server.load(servletMap);
    }


}

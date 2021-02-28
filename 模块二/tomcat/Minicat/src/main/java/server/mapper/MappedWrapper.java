package server.mapper;

import server.HttpServlet;

public class MappedWrapper extends MapperBase{

    private HttpServlet httpServlet;

    public MappedWrapper(String name, HttpServlet httpServlet) {
        super(name);
        this.httpServlet = httpServlet;
    }

    public HttpServlet getHttpServlet() {
        return httpServlet;
    }

    public void setHttpServlet(HttpServlet httpServlet) {
        this.httpServlet = httpServlet;
    }

}

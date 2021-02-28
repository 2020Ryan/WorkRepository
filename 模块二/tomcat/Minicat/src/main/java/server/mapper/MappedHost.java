package server.mapper;

import java.util.List;

public class MappedHost extends MapperBase{
    private String port;
    private String appBase;
    private List<MappedContext> contextList;

    public MappedHost(String name, String port, String appBase, List<MappedContext> contextList) {
        super(name);
        this.port = port;
        this.appBase = appBase;
        this.contextList = contextList;
    }



    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getAppBase() {
        return appBase;
    }

    public void setAppBase(String appBase) {
        this.appBase = appBase;
    }

    public List<MappedContext> getContextList() {
        return contextList;
    }

    public void setContextList(List<MappedContext> contextList) {
        this.contextList = contextList;
    }

}

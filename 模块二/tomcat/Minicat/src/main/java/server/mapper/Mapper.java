package server.mapper;

import java.util.List;

public class Mapper {
    private List<MappedHost> hostList;

    public Mapper(List<MappedHost> hostList) {
        this.hostList = hostList;
    }

    public List<MappedHost> getHostList() {
        return hostList;
    }

    public void setHostList(List<MappedHost> hostList) {
        this.hostList = hostList;
    }
}

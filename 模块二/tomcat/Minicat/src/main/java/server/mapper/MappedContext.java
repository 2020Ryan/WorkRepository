package server.mapper;

import java.util.List;

public class MappedContext  extends MapperBase{
    private List<MappedWrapper> wrapperList;
    public MappedContext(String name, List<MappedWrapper> wrapperList) {
            super(name);
        this.wrapperList = wrapperList;
    }

    public List<MappedWrapper> getWrapperList() {
        return wrapperList;
    }

    public void setWrapperList(List<MappedWrapper> wrapperList) {
        this.wrapperList = wrapperList;
    }

}

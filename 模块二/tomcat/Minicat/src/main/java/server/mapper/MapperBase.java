package server.mapper;

public class MapperBase {
    //主要让其他mapper子类继承共同需要的名称信息
    private String name;

    public MapperBase(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

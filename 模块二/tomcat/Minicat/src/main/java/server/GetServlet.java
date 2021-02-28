package server;

import server.mapper.MappedContext;
import server.mapper.MappedHost;
import server.mapper.MappedWrapper;
import server.mapper.Mapper;

import java.util.List;

public class GetServlet {
    // 通过url匹配到servlet
    public static HttpServlet getServlet(Request request, Mapper mapper){
        String host = request.getHost();
        String url = request.getUrl();

        List<MappedHost> hostList = mapper.getHostList();
        for (MappedHost mappedHost : hostList) {
            if (host.equals(mappedHost.getName())){
                for (MappedContext mappedContext : mappedHost.getContextList()) {
                    String contextName = mappedContext.getName();
                    for (MappedWrapper mappedWrapper : mappedContext.getWrapperList()) {
                        String wrapperName = mappedWrapper.getName();
                        if (url.equals("/" + contextName + wrapperName)){
                            System.out.println("url==========>"+url);
                            System.out.println(contextName + wrapperName);
                            System.out.println(mappedWrapper.getHttpServlet());
                            return mappedWrapper.getHttpServlet();
                        }
                    }
                }
            }
        }
        return null;
    }

}

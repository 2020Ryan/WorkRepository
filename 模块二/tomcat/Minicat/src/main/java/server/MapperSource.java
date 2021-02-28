package server;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import server.mapper.MappedContext;
import server.mapper.MappedHost;
import server.mapper.MappedWrapper;
import server.mapper.Mapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapperSource {


    public static Mapper getMapper() throws DocumentException, FileNotFoundException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        //创建容器对象
        List<MappedWrapper> wrapperList = new ArrayList<>();
        List<MappedContext> contextList = new ArrayList<>();
        List<MappedHost> hostList = new ArrayList<>();
        //创建获取server.xml信息对象
        XmlReader xmlReader = new XmlReader();
        Map<String, String> serverMap = xmlReader.ServerXML();
        String port = serverMap.get("port");
        String hostName = serverMap.get("name");
        String appBase = serverMap.get("appBase");
        // 输入静态资源文件
        File webapps = new File(appBase);
        File[] files = webapps.listFiles();
        for (int i = 0; i < files.length; i++) {
            File[] listFiles = files[i].listFiles();
            for (File file : listFiles) {
                if (file.isFile() && file.getName().equals("web.xml")) {
                    InputStream fileInputStream = new FileInputStream(file);
                    SAXReader saxReader = new SAXReader();
                    Document document = saxReader.read(fileInputStream);
                    Element rootElement = document.getRootElement();
                    List<Element> selectNodes = rootElement.selectNodes("//servlet");
                    for (int j = 0; j < selectNodes.size(); j++) {
                        Element element = selectNodes.get(j);
                        // <servlet-name>lagou</servlet-name>
                        Element servletnameElement = (Element) element.selectSingleNode("servlet-name");
                        String servletName = servletnameElement.getStringValue();
                        // <servlet-class>server.LagouServlet</servlet-class>
                        Element servletclassElement = (Element) element.selectSingleNode("servlet-class");
                        String servletClass = servletclassElement.getStringValue();
                        // 根据servlet-name的值找到url-pattern
                        Element servletMapping = (Element) rootElement.selectSingleNode("/web-app/servlet-mapping[servlet-name='" + servletName + "']");
                        // /lagou
                        String urlPattern = servletMapping.selectSingleNode("url-pattern").getStringValue();
                        //将servletClass处理
                        String className = servletClass.replace(".","\\")+ ".class";//+ ".class"

                        System.out.println(className);
                        //创建获取对象的对象
                        GetClass getClass = new GetClass();
                        System.out.println(files[i].getAbsolutePath() + "\\+++" + className);
                        Class<?> aClass = getClass.findClass(files[i].getAbsolutePath()+"\\", servletClass);
                        //Class<?> aClass = getClass.findClass(files[i].getAbsolutePath() + "/", servletClass);
                        //填充
                        String name =  files[i].getAbsolutePath() + "\\" + className;
                        //MappedWrapper mappedWrapper = new MappedWrapper(urlPattern,  (HttpServlet)Class.forName(name).newInstance());
                        MappedWrapper mappedWrapper = new MappedWrapper(urlPattern, (HttpServlet) aClass.getDeclaredConstructor().newInstance());
                        //使用wrapperList装wrapper
                        wrapperList.add(mappedWrapper);
                    }

                }

            }
            contextList.add(new MappedContext(files[i].getName(), wrapperList));
            //置为空
            wrapperList = new ArrayList<MappedWrapper>();
        }
        hostList.add(new MappedHost(hostName + ":" + port, port, appBase, contextList));
        return new Mapper(hostList);
    }


}

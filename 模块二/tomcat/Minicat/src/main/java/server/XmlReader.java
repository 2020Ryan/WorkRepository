package server;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class XmlReader {

    public Map<String,String> ServerXML() throws DocumentException {
        //加载server.xml
        InputStream resourceAsStream = XmlReader.class.getClassLoader().getResourceAsStream("server.xml");
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(resourceAsStream);
        Element rootElement = document.getRootElement();
        Element connector = (Element) rootElement.selectSingleNode("//Connector");
        //socket端口
        String port = connector.attributeValue("port");
        Element host = (Element) rootElement.selectSingleNode("//Host");
        //虚拟主机地址
        String hostname = host.attributeValue("name");
        //webapps文件夹路径
        String appBase = host.attributeValue("appBase");

        Map<String,String> map = new HashMap<>();
        map.put("port",port);
        map.put("name",hostname);
        map.put("appBase",appBase);
        return  map;
    }
}

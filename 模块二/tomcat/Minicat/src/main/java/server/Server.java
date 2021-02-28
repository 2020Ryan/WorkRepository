package server;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class Server {


    public void load(Map<String, HttpServlet> servletMap) {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("web.xml");
        SAXReader saxReader = new SAXReader();

        Document document = null;
        try {
            document = saxReader.read(resourceAsStream);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        Element rootElement = document.getRootElement();
        List<Element> selectNodes = rootElement.selectNodes("//server");
        for(int i = 0; i < selectNodes.size(); i++){

        }
    }
}

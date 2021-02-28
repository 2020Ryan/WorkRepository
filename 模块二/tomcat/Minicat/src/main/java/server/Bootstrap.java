package server;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import server.mapper.Mapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/*

    自定义tomcat启动类
*/
public class Bootstrap{

    //定义socket监听的端口号
    private int port = 8080;

    //get / set 方法
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }


    //启动方法，初始化一些操作
    public void start () throws Exception {
        // 加载mapper组件
        Mapper mapper = MapperSource.getMapper();


        //利用线程池来优化
        int corePoolSize = 10 ;
        int maximumPoolSize = 50;
        long keepAliveTime = 100L;
        //设置单位时间
        TimeUnit unit = TimeUnit.SECONDS;
        //阻塞队列创建
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(50);

        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        //策略
        RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();

        //创建
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                workQueue,
                threadFactory,
                handler
        );

        //创建服务端
        ServerSocket serverSocket = new ServerSocket(Integer.parseInt(mapper.getHostList().get(0).getPort()));
        System.out.println("check on port ==="+port);


        //多线程
        while(true){
            Socket socket = serverSocket.accept();
            RequestProcessor requestProcessor = new RequestProcessor(socket,mapper);
            threadPoolExecutor.execute(requestProcessor);
        }

    }

    //存放动态servlet的容器
    private Map<String,HttpServlet> servletMap = new HashMap<String,HttpServlet>();

    private void loadServlet(){
        Catalina catalina = new Catalina();
        catalina.load(servletMap);
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("web.xml");
        SAXReader saxReader = new SAXReader();

        try{
            Document document = saxReader.read(resourceAsStream);
            Element rootElement = document.getRootElement();
            List<Element> selectNodes = rootElement.selectNodes("//servlet");
            for(int i = 0; i < selectNodes.size(); i++){
                Element element = selectNodes.get(i);
                // 取<servlet-name>lagou</servlet-name>
                Element servletnameElement = (Element) element.selectSingleNode("servlet-name");
                String servletName = servletnameElement.getStringValue();
                // 取<servlet-class>server.LagouServlet</servlet-class>
                Element servletclassElement = (Element) element.selectSingleNode("servlet-class");
                String servletClass = servletclassElement.getStringValue();
                //根据servlet-name的值找到url-pattern
                Element servletMapping = (Element) rootElement.selectSingleNode("/web-app/servlet-mapping[servlet-name='" + servletName + "']");

                //把具体的url的路径拿到  <url-pattern>/lagou</url-pattern>
                String urlPattern = servletMapping.selectSingleNode("url-pattern").getStringValue();
                servletMap.put(urlPattern,(HttpServlet)Class.forName(servletClass).newInstance());
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * tomcat入口main()
     */
    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        try {
            // 启动Minicat
            bootstrap.start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}




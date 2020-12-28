package com.lagou.factory;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class BeanFactory {

    /**
     * bean工厂有俩个任务
     * 任务一：解析xml，通过反射技术实例化对象并且存储map中待用
     * 任务二：对外提供根据id获取获取实例对象的接口
     */

    private static Map<String,Object> map = new HashMap<>();  // 存储对象

    //静态代码块，确保类加载，该部分就已经实现
    static {
        // 加载xml成流对象
        InputStream resourceAsStream = BeanFactory.class.getClassLoader().getResourceAsStream("beans.xml");
        // 进行解析xml
        SAXReader saxReader = new SAXReader();
        try {
            //文本对象
            Document document = saxReader.read(resourceAsStream);
            //获取根节点：beans
            Element rootElement = document.getRootElement();
            //子节点bean
            List<Element> beanList = rootElement.selectNodes("//bean");
            for (int i = 0; i < beanList.size(); i++) {
                Element element =  beanList.get(i);
                // 获取到该bean的id 和 class 属性
                String id = element.attributeValue("id");        // accountDao
                String clazz = element.attributeValue("class");  // com.lagou.edu.dao.impl.JdbcAccountDaoImpl
                // 通过反射技术实例化对象
                Class<?> aClass = Class.forName(clazz);
                Object o = aClass.newInstance();
                // 存储到map中待用
                map.put(id,o);

            }

            // 接下来需要是通过ref找寻对象之间的依赖关系，在通过set方法传值
            // 我们的bean.xml中带有property子元素的对象就需要传值
            List<Element> propertyNodes = rootElement.selectNodes("//property");
            // 解析property，获取name和ref的值
            for (int i = 0; i < propertyNodes.size(); i++) {
                Element element =  propertyNodes.get(i);
                String name = element.attributeValue("name");
                String ref = element.attributeValue("ref");

                //需要获取父节点
                Element parent = element.getParent();

                // 父元素对象的反射
                String parentId = parent.attributeValue("id");
                Object parentObject = map.get(parentId);
                // 寻找set方法，并实现注入
                Method[] methods = parentObject.getClass().getMethods();
                for (int j = 0; j < methods.length; j++) {
                    Method method = methods[j];
                    //name==AccountDao ，忽略大小写，进行匹配
                    if(method.getName().equalsIgnoreCase("set" + name)) {
                       //找到之后就是赋值
                        method.invoke(parentObject,map.get(ref));
                    }
                }

                // 把处理之后的parentObject重新放到map中
                map.put(parentId,parentObject);

            }


        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }


    // 任务二：对外提供获取实例对象的接口（根据id获取），类似value
    public static  Object getBean(String id) {
        return map.get(id);
    }

}

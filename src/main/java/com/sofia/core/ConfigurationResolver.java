package com.sofia.core;

import com.sofia.annotation.Autowired;
import com.sofia.utils.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Created by yingbo.gu on 2018-03-21.
 */
public class ConfigurationResolver extends DefaultServlet {

    public ConfigurationResolver(Document document) throws Exception {
        NodeList nodeList = document.getElementsByTagName("bean");//获取文本中为bean的节点
        for(int i = 0;i<nodeList.getLength();i++){
            NamedNodeMap node = nodeList.item(i).getAttributes();
            Map<String,String> nodeMap = getNodeMap(node);
            String beanId = nodeMap.get("id");
            String beanClass = nodeMap.get("class");
            if(StringUtils.isBlank(beanClass)||StringUtils.isBlank(beanId)){
                continue;
            }
            Class clazz = Class.forName(beanClass);
            Object object = clazz.newInstance();
            Field[] fields = clazz.getDeclaredFields();
            for(Field field:fields){
                field.setAccessible(true);
                Autowired autowired = field.getAnnotation(Autowired.class);
                if(null != autowired){
                    //找到带Autowired 注解的参数,实例化
                    field.set(object,field.getType().newInstance());
                }
            }
            beanFactory.put(beanId,object);//将bean放到map
        }
    }
}

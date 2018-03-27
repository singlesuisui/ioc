package com.sofia.core;


import com.sofia.utils.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

import javax.servlet.http.HttpServlet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * Created by yingbo.gu on 2018-03-25.
 */
public class DefaultServlet implements ApplicationEventMulticaster{

    public static Map<String,Object> beanFactory ;//bean工厂,所有初始化的Bean存放位置

    public static Map<String,ApplicationListener> eventFactory;//事件工厂

    private static String springXml = "/spring.xml";

    static{
        beanFactory = new HashMap<String, Object>();
        eventFactory = new HashMap<String, ApplicationListener>();
    }

    public void init() throws Exception{
        String xml = springXml;
        setConfigLocations(xml);
        refresh();
    }

    public Object getBean(String beanName){
        Object object = beanFactory.get(beanName);
        if(null==object){
            throw new RuntimeException("找不到对应的依赖:"+beanName);
        }
        return object;
    }

    public void printBeanFactory(){
        for(Map.Entry<String,Object> entry:beanFactory.entrySet()){
            System.out.println(entry.getKey() +"   "+ entry.getValue().getClass().getCanonicalName());
        }
    }

    private void setConfigLocations(String configXml){
        //什么都不做 当然可以做一些环境的检查 将配置的提取用一个类去处理等等 我这偷个懒
    }

    private void refresh() throws Exception{
        //注册bean
        invokeBeanFactory();
        //登记监听者
        registerListeners();
        //j结束刷新 表面程序已经启动 可以广播这个刷新完毕事件了 广播事件
        finishRefresh();
    }
    private void finishRefresh(){
//        publishEvent(this);
    }

    private void invokeBeanFactory() throws Exception{
        InputStream ins = null;
        ins = DefaultServlet.class.getResourceAsStream(springXml)==null?
                new FileInputStream(springXml):DefaultServlet.class.getResourceAsStream(springXml);
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(ins);
        new ConfigurationResolver(document);
        new AnnotationResolver(document);
    }

    protected Map<String,String> getNodeMap(NamedNodeMap node){
        Map<String,String> nodeMap = new HashMap<String, String>();
        for(int j = 0;j<node.getLength();j++){
            String beanNodeName = node.item(j).getNodeName();
            String beanNodeValue = null;
            if(StringUtils.isNotBlank(beanNodeName)){
                beanNodeValue = node.item(j).getNodeValue();
            }
            if(StringUtils.isNotBlank(beanNodeValue)){
                nodeMap.put(beanNodeName,beanNodeValue);
            }
        }
        return nodeMap;
    }

    /**
　　 * 从beanfactory找到那些是监听者类型的bean
　　*/
    private void registerListeners(){
        Iterator<String> it = beanFactory.keySet().iterator();
        while(it.hasNext()){
            String key=it.next();
            if(beanFactory.get(key) instanceof  ApplicationListener){
                eventFactory.put(key,(ApplicationListener)eventFactory.get(key));
                it.remove();
            }
        }
    }

    //广播事件
    public void publishEvent(ApplicationEvent event) {
        Iterator<String> it = eventFactory.keySet().iterator();
        while(it.hasNext()){
            eventFactory.get(it.next()).onApplicationEvent(event);
        }
    }
}

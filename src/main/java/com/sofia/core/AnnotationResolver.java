package com.sofia.core;

import com.sofia.annotation.Autowired;
import com.sofia.annotation.Comment;
import com.sofia.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;


/**
 * Created by yingbo.gu on 2018-03-22.
 */
public class AnnotationResolver extends DefaultServlet {

    private static Logger logger = LoggerFactory.getLogger(AnnotationResolver.class);
    private ClassLoader classLoader;
    {
        classLoader = AnnotationResolver.class.getClassLoader();
    }

    public AnnotationResolver(Document document) throws Exception {
        NodeList nodeList = document.getElementsByTagName("context:component-scan");//获取文本中为bean的节点
        Set<String> classNameSet = new HashSet<String>();
        for(int i = 0;i<nodeList.getLength();i++){
            NamedNodeMap node = nodeList.item(i).getAttributes();
            Map<String,String> nodeMap = super.getNodeMap(node);
            String basePackagePath = nodeMap.get("base-package");
            if(StringUtils.isBlank(basePackagePath)){
                continue;
            }
            String[] packagePath = basePackagePath.split(",");
            if(null==packagePath||packagePath.length==0){
                throw new RuntimeException("未指定扫描路径");
            }
            try {
                for (String path : packagePath) {
                    scanPackage(path,classNameSet);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        for(String className:classNameSet){
            Class clazz = Class.forName(className);
            Annotation[] annotations = clazz.getAnnotations();
            if(null==annotations){
                return;
            }
            Comment comment = getAnnotationOfComment(annotations);
            if(null==comment){
                return;
            }
            Object object = clazz.newInstance();
            String name = comment.name();
            if(StringUtils.isBlank(name)){
                throw new RuntimeException("Comment注解name属性不能为空,Class:"+clazz.getCanonicalName());
            }
            Field[] fields = clazz.getDeclaredFields();
            for(Field field:fields){
                field.setAccessible(true);
                Autowired autowired = field.getAnnotation(Autowired.class);
                if(null != autowired){
                    //找到带Autowired 注解的参数,实例化
                    Class cla = field.getType();
                    cla.newInstance();
//                    field.set(object,cla.newInstance());
                }
            }
            beanFactory.put(name,object);
        }
    }

    private void scanPackage(String packagePath,Set<String> classNameSet) throws Exception{
        String splashPath = StringUtils.dotToSplash(packagePath);
        URL url = classLoader.getResource(splashPath);
        String filePath = StringUtils.getRootPath(url);
        List<String> names = null;
        if (isJarFile(filePath)) {
            if (logger.isDebugEnabled()) {
                logger.debug("{} 是一个JAR包", filePath);
            }
            names = readFromJarFile(filePath, splashPath);
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("{} 是一个目录", filePath);
            }
            names = readFromDirectory(filePath);
        }
        for (String name : names) {
            if (isClassFile(name)) {
                classNameSet.add(toFullyQualifiedName(name, packagePath));
            } else {
                scanPackage(packagePath + "." + name,classNameSet);
            }
        }
        if (logger.isDebugEnabled()) {
            for (String n : classNameSet) {
                logger.debug("找到{}", n);
            }
        }
    }
    //检查是否是Jar包
    private Boolean isJarFile(String filePath){
        return filePath.endsWith(".jar")?true:false;
    }
    //检查是否是Class文件
    private Boolean isClassFile(String filePath){
        return filePath.endsWith(".class")?true:false;
    }
    //从Jar包中读取文件
    private List<String> readFromJarFile(String jarPath, String splashedPackageName) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("从JAR包中读取类: {}", jarPath);
        }
        JarInputStream jarIn = new JarInputStream(new FileInputStream(jarPath));
        JarEntry entry = jarIn.getNextJarEntry();
        List<String> nameList = new ArrayList<String>();
        while (null != entry) {
            String name = entry.getName();
            if (name.startsWith(splashedPackageName) && isClassFile(name)) {
                nameList.add(name);
            }
            entry = jarIn.getNextJarEntry();
        }
        return nameList;
    }
    private String toFullyQualifiedName(String shortName, String basePackage) {
        StringBuilder sb = new StringBuilder(basePackage);
        sb.append('.');
        sb.append(StringUtils.trimExtension(shortName));
        return sb.toString();
    }
    //从文件夹中读取
    private List<String> readFromDirectory(String path) {
        File file = new File(path);
        String[] names = file.list();

        if (null == names) {
            return null;
        }
        return Arrays.asList(names);
    }

    private Comment getAnnotationOfComment(Annotation[]  annotations){
        if(null!=annotations&&annotations.length>0){
            for(Annotation annotation:annotations){
                if (annotation.annotationType () == Comment.class) {
                    return (Comment) annotation;
                }
            }
        }
        return null;
    }
}

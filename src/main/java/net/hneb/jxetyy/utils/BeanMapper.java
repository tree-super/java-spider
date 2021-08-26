package net.hneb.jxetyy.utils;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by zhangshuai on 2020/12/9.
 */


/**
 * 简单封装Dozer, 实现深度转换Bean<->Bean的Mapper.实现:
 * <p>
 * 1. 持有Mapper的单例.
 * 2. 返回值类型转换.1
 * 3. 批量转换Collection中的所有对象.
 * 4. 区分创建新的B对象与将对象A值复制到已存在的B对象两种函数.
 */
public class BeanMapper {

    private static Logger logger = LoggerFactory.getLogger(BeanMapper.class);
    /**
     * 持有Dozer单例, 避免重复创建DozerMapper消耗资源.
     */
    private static DozerBeanMapper dozer = new DozerBeanMapper();

    private static ObjectMapper om = new ObjectMapper();

    static{
        om.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        //空值不序列化
        om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //JSON to OBJECT 时，未知属性不报错
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        //序列化时，日期的统一格式
        om.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        //驼峰转下划线
        om.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    }

    public static ObjectMapper getObjectMapper(){
        return om;
    }

    public static <T> List<T> parseJSONList(String source, Class<T> valueType) {
        return JSON.parseArray(source, valueType);
    }

    /**
     * 基于Dozer将对象A的值拷贝到对象B中.
     */
    public static void copy(Object source, Object destinationObject) {
        dozer.map(source, destinationObject);
    }

    /**
     * 基于Dozer转换对象的类型.
     */
    public static <T> T map(Object source, Class<T> destinationClass) {
        if(source == null)
            return null;
        return dozer.map(source, destinationClass);
    }

    /**
     * 基于Dozer转换Collection中对象的类型.
     */
    public static <T> List<T> mapList(Collection sourceList, Class<T> destinationClass) {
        List<T> destinationList = new ArrayList<T>();
        for (Object sourceObject : sourceList) {
            T destinationObject = dozer.map(sourceObject, destinationClass);
            destinationList.add(destinationObject);
        }
        return destinationList;
    }

    public static <T> T parseJSON(String source, Class<T> destinationClass) {
        try {
            return om.readValue(source, destinationClass);
        } catch (Exception e) {
            logger.error("JSON转换异常{}", source, e.getMessage());
        }
        return null;
    }

    public static String toJSONString(Object source) {
        try {
            return om.writeValueAsString(source);
        } catch (Exception e) {
            logger.error("JSON转换异常{}", e.getMessage());
        }
        return null;
    }

    public static Map<String, Object> transBean2Map(Object obj) {
        if (obj == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            if (obj instanceof Map) {
                BeanMapper.copy(obj, map);
                ((Map<String, Object>) obj).forEach((key, value) -> map.put(key, value));
            } else {
                BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
                PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
                for (PropertyDescriptor property : propertyDescriptors) {
                    String key = property.getName();

                    // 过滤class属性
                    if (!key.equals("class")) {
                        // 得到property对应的getter方法
                        Method getter = property.getReadMethod();
                        Object value = getter.invoke(obj);

                        map.put(key, value);
                    }

                }
            }
        } catch (Exception e) {
            System.out.println("transBean2Map Error " + e);
        }

        return map;

    }


    public static Map<String, Object> transBean2MapWithSnake(Object obj) {
        if (obj == null) {
            return null;
        }
        PropertyNamingStrategy.PropertyNamingStrategyBase strategy = new PropertyNamingStrategy.SnakeCaseStrategy();
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            if (obj instanceof Map) {
                BeanMapper.copy(obj, map);
                ((Map<String, Object>) obj).forEach((key, value) -> map.put(strategy.translate(key), value));
            } else {
                BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
                PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
                for (PropertyDescriptor property : propertyDescriptors) {
                    String key = property.getName();

                    // 过滤class属性
                    if (!key.equals("class")) {
                        // 得到property对应的getter方法
                        Method getter = property.getReadMethod();
                        Object value = getter.invoke(obj);

                        map.put(strategy.translate(key), value);
                    }

                }
            }
        } catch (Exception e) {
            System.out.println("transBean2Map Error " + e);
        }

        return map;

    }

}
package com.haizhi.graph.common.util;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.constant.GraphStatus;
import com.haizhi.graph.common.exception.UnexpectedStatusException;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by chengmo on 2018/3/15.
 */
public class ClassUtils {

    public static Class<?> loadClass(String className) {
        if (className == null) {
            return null;
        }
        Class<?> klass = null;
        try {
            klass = Class.forName(className);
        } catch (ClassNotFoundException ex) {
            //log.debug("Exception while loading class: " + className, ex);
        }
        return klass;
    }

    public static String jarForClass(String className) {
        Class<?> klass = loadClass(className);
        return jarForClass(klass);
    }

    public static String jarForClass(Class<?> klass) {
        return klass.getProtectionDomain().getCodeSource().getLocation().getPath();
    }


    /**
     * @description 获取Class<T>中T对应的Class, Class<T>需要有父类
     * @param classzz
    * @param genericClassIndex
     * @return java.lang.Class
     * @author liulu
     * @date 2018/10/18
     */
    public static Class forGenericClass(Class<?> classzz, Integer genericClassIndex){
        //System.out.println(classzz);
        //class com.dfsj.generic.UserDaoImpl因为是该类调用的该法，所以this代表它

        //返回表示此 Class 所表示的实体类的 直接父类 的 Type。注意，是直接父类
        //这里type结果是 com.dfsj.generic.GetInstanceUtil<com.dfsj.generic.User>
        Type type = classzz.getGenericSuperclass();

        // 判断 是否泛型
        if (type instanceof ParameterizedType) {
            // 返回表示此类型实际类型参数的Type对象的数组.
            // 当有多个泛型类时，数组的长度就不是1了
            Type[] ptypes = ((ParameterizedType) type).getActualTypeArguments();
            if (ptypes.length > genericClassIndex){
                return (Class) ptypes[genericClassIndex];  //将对应泛型T对应的类返回
            }
        }
        return Object.class;//若没有给定泛型，则返回Object类

    }

    /**
     * @description 根据源对象和类(具有无参构造函数)，创建类的一个新实例，并将源对象的属性拷贝过去
     * @param source
    * @param classz
     * @return T 创建的新实例
     * @author liulu
     * @date 2018/10/19
     */
    public static <T> T copyInstance(Object source,Class<T> tClass){
        if (null == source){
            return null;
        }
        T target;
        try {
            target = tClass.newInstance();
            BeanUtils.copyProperties(source,target);
        } catch (Exception e) {
            throw new UnexpectedStatusException(GraphStatus.COPY_FAILE,e);
        }
        return target;
    }

    /**
     * @description 根据源对象和类(具有无参构造函数)，创建类的一组新实例，并将源对象的属性拷贝过去
     * @param source
    * @param classz
     * @return List<T> 创建的新实例列表
     * @author liulu
     * @date 2018/10/19
     */
    public static <T1,T2> List<T1> copyInstances(List<T2> source, Class<T1> tClass){
        if (null == source){
            return Collections.emptyList();
        }
        List<T1> target = new ArrayList<>();
        try {
            for (T2 obj: source){
                T1 newObj = copyInstance(obj,tClass);
                target.add(newObj);
            }
        } catch (Exception e) {
            throw new UnexpectedStatusException(GraphStatus.COPY_FAILE,e);
        }
        return target;
    }

    /**
     * @description 深度拷贝
     * @param source
     * @return T
     * @author liulu
     * @date 2018/11/9
     */
    public static <T1,T2> T1 deepCopyByJson(final T2 source,Class<T1> tClass) {
        String json = JSON.toJSONString(source);
        return JSON.parseObject(json, tClass);
    }

    /**
     * @description 深度拷贝
     * @param source
     * @return T
     * @author liulu
     * @date 2018/11/9
     */
    public static <T1,T2> List<T1> deepCopyByJson(final List<T2> source,Class<T1> tClass) {
        List<T1> result = new ArrayList<>();
        source.stream().forEach(one ->
                result.add(deepCopyByJson(one,tClass))
        );
        return result;
    }


    private ClassUtils() {
        // Disable explicit object creation
    }
}

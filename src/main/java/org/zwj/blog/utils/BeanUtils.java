/*
 *  Created by ZhongWenjie on 2019-01-08 15:25
 */

package org.zwj.blog.utils;

import com.google.common.collect.Lists;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BeanUtils {

    public static void copyProperties(
            Object source,
            Object target,
            @Nullable Function<Object, Boolean> ignoreFunction,
            String... otherIgnoreProperties) throws IllegalAccessException {

        List<String> ignoreProperties = Lists.newArrayList();
        if (Util.valid(otherIgnoreProperties))
            ignoreProperties.addAll(Arrays.asList(otherIgnoreProperties));
        Class<?> sourceClass = source.getClass();
        Class<?> targetClass = target.getClass();
        Field[] fields = sourceClass.getDeclaredFields();
        for (Field field : fields) {
            Field targetField = null;
            try {
                targetField = targetClass.getDeclaredField(field.getName());
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            if (!Util.valid(targetField)) continue;
            field.setAccessible(true);
            if ( Util.valid(ignoreFunction) && !ignoreFunction.apply(field.get(source)) )
                ignoreProperties.add(field.getName());
        }
        org.springframework.beans.BeanUtils.copyProperties(source,target,ignoreProperties.toArray(new String[0]));
    }

    public static void copyProperties(
            Object source,
            Object target,
            @Nullable Function<Object, Boolean> ignoreFunction,
            String[] notIgnoreProperties,
            String[] otherIgnoreProperties) throws IllegalAccessException {

        List<String> ignoreProperties = Lists.newArrayList();
        if (Util.valid(otherIgnoreProperties))
            ignoreProperties.addAll(Arrays.asList(otherIgnoreProperties));
        Class<?> sourceClass = source.getClass();
        Class<?> targetClass = target.getClass();
        Field[] fields = sourceClass.getDeclaredFields();
        for (Field field : fields) {
            Field targetField = null;
            try {
                targetField = targetClass.getDeclaredField(field.getName());
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            if (!Util.valid(targetField)) continue;
            field.setAccessible(true);
            if ( Util.valid(ignoreFunction) && !ignoreFunction.apply(field.get(source)) )
                ignoreProperties.add(field.getName());
        }

        if (Util.valid(notIgnoreProperties)) {
            List<String> list = Arrays.asList(notIgnoreProperties);
            List<String> collect = ignoreProperties
                    .stream()
                    .filter((item) -> !list.contains(item))
                    .collect(Collectors.toList());
            ignoreProperties = (Util.valid(collect)) ? collect : Lists.newArrayList();
        }

        org.springframework.beans.BeanUtils.copyProperties(source,target,ignoreProperties.toArray(new String[0]));
    }
}

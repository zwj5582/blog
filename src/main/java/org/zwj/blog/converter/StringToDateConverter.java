/*
 *  Created by ZhongWenjie on 2019-01-21 16:48
 */

package org.zwj.blog.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Date;

//@Component
public class StringToDateConverter implements Converter<String, Date> {

    @Override
    public Date convert(String source) {
        return null;
    }
}

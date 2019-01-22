/*
 *  Created by ZhongWenjie on 2019-01-22 10:27
 */

package org.zwj.blog.common;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.zwj.blog.utils.Util;

import java.util.Map;

public class ResponseEntitys<T> extends ResponseEntity<T> {


    public ResponseEntitys(HttpStatus status) {
        super(status);
    }

    public ResponseEntitys(T body, HttpStatus status) {
        super(body, status);
    }

    public ResponseEntitys(MultiValueMap<String, String> headers, HttpStatus status) {
        super(headers, status);
    }

    public ResponseEntitys(T body, MultiValueMap<String, String> headers, HttpStatus status) {
        super(body, headers, status);
    }

    public static ResponseEntity success(Object body) {
        if ( body instanceof Page )
            return ok(createPageInfo((Page) body));
        return ok(body);
    }

    private static Map createPageInfo(Page page) {
        if ( !Util.valid(page) ) return ImmutableMap.of();
        return ImmutableMap.of(
                "content", page.getContent(),
                "totalElements",page.getTotalElements(),
                "totalPages",page.getTotalPages(),
                "size",page.getSize(),
                "number",page.getNumber()
        );
    }

}

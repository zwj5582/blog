/*
 *  Created by ZhongWenjie on 2019-01-21 17:16
 */

package org.zwj.blog.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;
import org.zwj.blog.entity.PageContent;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class PageContentVO extends PageContent {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date begin;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date end;

}

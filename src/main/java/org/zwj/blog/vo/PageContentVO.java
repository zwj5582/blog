/*
 *  Created by ZhongWenjie on 2019-01-21 17:16
 */

package org.zwj.blog.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.zwj.blog.entity.PageContent;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class PageContentVO extends PageContent {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
    private Date begin = DateTime.now().dayOfMonth().withMinimumValue().toDate();
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
    private Date end = DateTime.now().dayOfMonth().withMaximumValue().toDate();

}

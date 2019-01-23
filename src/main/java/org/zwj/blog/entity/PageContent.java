/*
 *  Created by ZhongWenjie on 2019-01-05 22:40
 */

package org.zwj.blog.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;
import org.zwj.blog.utils.Util;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "page_content")
@Data
public class PageContent implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String type = "page";

    private String title;

    private String info;

    private String htmlLocation;

    private String originalLocation;

    private String filename;

    private Boolean publicity;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;

    @Transient private String createTimeStr;

    @Transient private MultipartFile file;

    public static PageContent create(PageContent pageContent) {
        String createTimeStr = pageContent.getCreateTimeStr();
        if (Util.valid(createTimeStr))
            pageContent.setCreateTime(
                    DateTime.parse(createTimeStr, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"))
                            .toDate());
        else
            pageContent.setCreateTime(new Date());
        return pageContent;
    }
}

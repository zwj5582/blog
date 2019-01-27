/*
 *  Created by ZhongWenjie on 2019-01-05 22:40
 */

package org.zwj.blog.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

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

    private Integer historyId;

    private Long versionNum = 1L;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Transient private MultipartFile file;

}

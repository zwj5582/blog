/*
 *  Created by ZhongWenjie on 2019-01-26 11:17
 */

package org.zwj.blog.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "page_history")
@Data
public class PageHistory implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer version;

    private Integer pageId;

    private String title;

    private String info;

    private String htmlLocation;

    private String originalLocation;

    private Boolean publicity;

    private String filename;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime = new Date();

    private Boolean curr = true;

    @Transient private Long versionNum;

}

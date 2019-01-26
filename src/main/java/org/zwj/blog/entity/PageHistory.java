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
    private Integer id;

    private Integer pageId;

    private Integer version;

    private String htmlLocation;

    private String originalLocation;

    private String filename;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;

    private Boolean curr;

}

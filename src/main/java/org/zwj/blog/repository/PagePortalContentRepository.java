/*
 *  Created by ZhongWenjie on 2019-02-01 15:40
 */

package org.zwj.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.zwj.blog.entity.PagePortalContent;

public interface PagePortalContentRepository extends JpaRepository<PagePortalContent, Integer>, JpaSpecificationExecutor<PagePortalContent> {

}

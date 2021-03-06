/*
 *  Created by ZhongWenjie on 2019-01-05 22:45
 */

package org.zwj.blog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.zwj.blog.entity.PageContent;

import java.util.List;

public interface PageContentRepository extends JpaRepository<PageContent, Integer>, JpaSpecificationExecutor<PageContent> {

    Page<PageContent> findAllByPublicityIsTrueAndStateNotIn(Pageable pageable, List<String> notInStates);

}

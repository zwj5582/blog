/*
 *  Created by ZhongWenjie on 2019-01-26 11:20
 */

package org.zwj.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.zwj.blog.entity.PageHistory;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.Optional;

public interface PageHistoryRepository extends JpaRepository<PageHistory, Integer>, JpaSpecificationExecutor<PageHistory> {

    Optional<PageHistory> findFirstByPageIdAndCurrIsTrue(Integer pageId);

    Long countByPageId(Integer pageId);

}

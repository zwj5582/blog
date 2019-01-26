/*
 *  Created by ZhongWenjie on 2019-01-26 11:20
 */

package org.zwj.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.zwj.blog.entity.PageHistory;

public interface PageHistoryRepository extends JpaRepository<PageHistory, Integer>, JpaSpecificationExecutor<PageHistory> {
}

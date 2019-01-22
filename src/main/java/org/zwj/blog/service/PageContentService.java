/*
 *  Created by ZhongWenjie on 2019-01-05 22:47
 */

package org.zwj.blog.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zwj.blog.entity.PageContent;
import org.zwj.blog.repository.PageContentRepository;
import org.zwj.blog.utils.Util;
import org.zwj.blog.vo.PageContentVO;

import javax.persistence.criteria.Predicate;
import java.util.List;

@Service
@Transactional
public class PageContentService {

    @Autowired
    private PageContentRepository pageContentRepository;

    public List<PageContent> findAll(){
        return pageContentRepository.findAll();
    }

    public PageContent findById(Integer id) {
        return pageContentRepository.findById(id).orElse(new PageContent());
    }

    public void savePageContent(PageContent pageContent) {
        pageContentRepository.save(pageContent);
    }

    public Page<PageContent> findByConditionAndPageable(PageContentVO pageContentVO, Pageable pageable) {
        Specification<PageContent> where = Specification.where(
                (Specification<PageContent>) (root, query, criteriaBuilder) -> {
                    Predicate predicate = criteriaBuilder.conjunction();
                    if (Util.valid(pageContentVO.getBegin()) && Util.valid(pageContentVO.getEnd())) {
                        predicate.getExpressions().add(criteriaBuilder.between(root.get("createTime"), pageContentVO.getBegin(), pageContentVO.getEnd()));
                    }
                    if (Util.valid(pageContentVO.getTitle())) {
                        predicate.getExpressions().add(criteriaBuilder.like(root.get("title"), "%" + StringUtils.trim(pageContentVO.getTitle()) + "%"));
                    }
                    if (Util.valid(pageContentVO.getInfo())) {
                        predicate.getExpressions().add(criteriaBuilder.like(root.get("info"), "%" + StringUtils.trim(pageContentVO.getInfo()) + "%"));
                    }
                    return predicate;
                });
        return pageContentRepository.findAll(where, pageable);
    }
}

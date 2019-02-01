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
import org.zwj.blog.entity.PageHistory;
import org.zwj.blog.repository.PageContentRepository;
import org.zwj.blog.repository.PageHistoryRepository;
import org.zwj.blog.utils.BeanUtils;
import org.zwj.blog.utils.Util;
import org.zwj.blog.vo.PageContentVO;

import javax.persistence.criteria.Predicate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PageContentService {

    @Autowired private PageContentRepository pageContentRepository;

    @Autowired private PageHistoryRepository pageHistoryRepository;

    public List<PageContent> findAll() {
        return pageContentRepository.findAll();
    }

    public PageContent findById(Integer id) {
        return pageContentRepository
                .findById(id)
                .map(pageContent -> {
                    if (!Util.valid(pageContent.getMdContent())) pageContent.setMdContent("");
                    return pageContent;
                })
                .orElse(new PageContent());
    }

    public void savePageContent(PageContent pageContent) throws IllegalAccessException {
        pageContentRepository.save(pageContent);
        pageHistoryRepository
                .findFirstByPageIdAndCurrIsTrue(pageContent.getId())
                .orElse(new PageHistory())
                .setCurr(false);
        PageHistory currPageHistory = new PageHistory();
        BeanUtils.copyProperties(pageContent, currPageHistory, null, "createTime");
        currPageHistory.setPageId(pageContent.getId());
        pageHistoryRepository.save(currPageHistory);
        pageContent.setHistoryId(currPageHistory.getVersion());
    }

    public void saveContent(PageContent page) {
        pageContentRepository.save(page);
    }

    public PageHistory updateWithFile(PageContent pageContent) throws Exception {
        PageContent dbPageContent =
                pageContentRepository
                        .findById(pageContent.getId())
                        .orElseThrow(() -> new Exception("Can not find Entity ID;"));
        BeanUtils.copyProperties(
                pageContent, dbPageContent, Util::valid, "id", "type", "file", "versionNum");
        pageHistoryRepository
                .findFirstByPageIdAndCurrIsTrue(dbPageContent.getId())
                .orElse(new PageHistory())
                .setCurr(false);
        PageHistory currPageHistory = new PageHistory();
        BeanUtils.copyProperties(dbPageContent, currPageHistory, null, "createTime");
        currPageHistory.setPageId(dbPageContent.getId());
        pageHistoryRepository.save(currPageHistory);
        final Long versionNum = pageHistoryRepository.countByPageId(dbPageContent.getId());
        currPageHistory.setVersionNum(versionNum);
        dbPageContent.setVersionNum(versionNum);
        dbPageContent.setHistoryId(currPageHistory.getVersion());
        return currPageHistory;
    }

    public Page<PageContent> findByConditionAndPageable(
            PageContentVO pageContentVO, Pageable pageable) {
        Specification<PageContent> where =
                Specification.where(
                        (Specification<PageContent>)
                                (root, query, criteriaBuilder) -> {
                                    Predicate predicate = criteriaBuilder.conjunction();
                                    if (Util.valid(pageContentVO.getBegin())
                                            && Util.valid(pageContentVO.getEnd())) {
                                        predicate
                                                .getExpressions()
                                                .add(
                                                        criteriaBuilder.between(
                                                                root.get("createTime"),
                                                                pageContentVO.getBegin(),
                                                                pageContentVO.getEnd()));
                                    }
                                    if (Util.valid(pageContentVO.getTitle())) {
                                        predicate
                                                .getExpressions()
                                                .add(
                                                        criteriaBuilder.like(
                                                                root.get("title"),
                                                                "%"
                                                                        + StringUtils.trim(
                                                                                pageContentVO
                                                                                        .getTitle())
                                                                        + "%"));
                                    }
                                    if (Util.valid(pageContentVO.getInfo())) {
                                        predicate
                                                .getExpressions()
                                                .add(
                                                        criteriaBuilder.like(
                                                                root.get("info"),
                                                                "%"
                                                                        + StringUtils.trim(
                                                                                pageContentVO
                                                                                        .getInfo())
                                                                        + "%"));
                                    }
                                    if (pageContentVO.getPublicity() != null) {
                                        predicate
                                                .getExpressions()
                                                .add(
                                                        (pageContentVO.getPublicity())
                                                                ? criteriaBuilder.isTrue(
                                                                        root.get("publicity"))
                                                                : criteriaBuilder.isFalse(
                                                                        root.get("publicity")));
                                    }
                                    return predicate;
                                });
        return pageContentRepository.findAll(where, pageable);
    }

    public Page<PageContent> findByPage(Pageable pageable) {
        return pageContentRepository.findAllByPublicityIsTrue(pageable);
    }

    public void update(PageContent pageContent) throws Exception {
        PageContent content =
                pageContentRepository
                        .findById(pageContent.getId())
                        .orElseThrow(() -> new Exception("Can not find Entity ID;"));
        BeanUtils.copyProperties(
                pageContent,
                content,
                Util::valid,
                new String[] {"publicity"},
                new String[] {"id", "versionNum"});
    }

    public PageHistory findByVersion(Integer version) {
        return pageHistoryRepository.findById(version).orElse(new PageHistory());
    }

    public void saveMdContent(PageContent pageContent) {
        pageContentRepository
                .findById(pageContent.getId())
                .orElse(new PageContent())
                .setMdContent(pageContent.getMdContent());
    }
}

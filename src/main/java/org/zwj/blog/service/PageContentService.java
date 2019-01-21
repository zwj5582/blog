/*
 *  Created by ZhongWenjie on 2019-01-05 22:47
 */

package org.zwj.blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zwj.blog.entity.PageContent;
import org.zwj.blog.repository.PageContentRepository;

import java.util.List;

@Service
@Transactional
public class PageContentService {

    @Autowired
    private PageContentRepository pageContentRepository;

    public List<PageContent> findAll(){
        return pageContentRepository.findAll();
    }

    public Page<PageContent> findByPage(Pageable pageable){
        return pageContentRepository.findAll(pageable);
    }

    public PageContent findById(Integer id) {
        return pageContentRepository.findById(id).orElse(new PageContent());
    }

    public void savePageContent(PageContent pageContent) {
        pageContentRepository.save(pageContent);
    }
}

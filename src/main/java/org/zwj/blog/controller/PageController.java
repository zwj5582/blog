/*
 *  Created by ZhongWenjie on 2019-01-08 21:20
 */

package org.zwj.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.zwj.blog.common.ResponseEntitys;
import org.zwj.blog.entity.PageContent;
import org.zwj.blog.service.PageContentService;
import org.zwj.blog.vo.PageContentVO;

@Controller
public class PageController {

    @Autowired private PageContentService pageContentService;

    @RequestMapping(value = "/page{id}")
    public String page(@PathVariable(value = "id") Integer id, Model model) {
        model.addAttribute("page", pageContentService.findById(id));
        return "page";
    }

    @GetMapping(value = "/page/toList")
    public @ResponseBody ResponseEntity toList(
            @PageableDefault(
                            size = 7,
                            sort = {"createTime"})
                    Pageable pageable) {
        return ResponseEntitys.success(
                pageContentService.findByPage(
                        PageRequest.of(
                                pageable.getPageNumber() - 1,
                                pageable.getPageSize(),
                                pageable.getSort())));
    }

    @PostMapping(
            value = "/page/update",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody ResponseEntity update(@RequestBody PageContent pageContent) throws Exception {
        pageContentService.update(pageContent);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/page/list")
    public @ResponseBody ResponseEntity list(
            @PageableDefault(
                            size = 20,
                            sort = {"createTime"})
                    Pageable pageable,
            PageContentVO pageContentVO) {
        return ResponseEntitys.success(
                pageContentService.findByConditionAndPageable(
                        pageContentVO,
                        PageRequest.of(
                                pageable.getPageNumber() - 1,
                                pageable.getPageSize(),
                                pageable.getSort())),
                pageContentVO);
    }

    public @ResponseBody ResponseEntity pageHistoryList(){
        return null;
    }

}

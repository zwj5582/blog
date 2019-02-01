/*
 *  Created by ZhongWenjie on 2019-02-01 15:45
 */

package org.zwj.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zwj.blog.common.ResponseEntitys;
import org.zwj.blog.service.PageContentService;

@Controller
@RequestMapping("/page")
public class PagePortalController {

    @Autowired
    private PageContentService pageContentService;

    @GetMapping(value = "/list")
    public @ResponseBody
    ResponseEntity list(
            @PageableDefault(
                    size = 7,
                    sort = {"createTime"})
                    Pageable pageable) {
        return ResponseEntitys.success(
                pageContentService.findPageContentByPage(
                        PageRequest.of(
                                pageable.getPageNumber() - 1,
                                pageable.getPageSize(),
                                pageable.getSort())));
    }

    @RequestMapping(value = "/{id}")
    public String page(@PathVariable(value = "id") Integer id, Model model) {
        model.addAttribute("page", pageContentService.findPagePortalContentById(id));
        return "page";
    }

}

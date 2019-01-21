/*
 *  Created by ZhongWenjie on 2019-01-08 21:20
 */

package org.zwj.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zwj.blog.entity.PageContent;
import org.zwj.blog.service.PageContentService;

@Controller
@RequestMapping("/page")
public class PageController {

    @Autowired private PageContentService pageContentService;

    @RequestMapping(value = "{id}")
    public String page(@PathVariable(value = "id") Integer id, Model model) {
        model.addAttribute("page", pageContentService.findById(id));
        return "page";
    }

    @GetMapping(value = "/list")
    public @ResponseBody ResponseEntity<Page> list(Pageable pageable){
        final Page<PageContent> page = pageContentService.findByPage(pageable);
        return ResponseEntity.ok(page);
    }
}

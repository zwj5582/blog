/*
 *  Created by ZhongWenjie on 2019-01-08 21:20
 */

package org.zwj.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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
}

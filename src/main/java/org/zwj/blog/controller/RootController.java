/*
 *  Created by ZhongWenjie on 2019-01-05 22:48
 */

package org.zwj.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.zwj.blog.service.PageContentService;

@Controller
public class RootController {

    @Autowired private PageContentService pageContentService;

    @RequestMapping("/")
    public String root(Model model) {
        model.addAttribute("pageContents", pageContentService.findAll());
        return "index";
    }

}

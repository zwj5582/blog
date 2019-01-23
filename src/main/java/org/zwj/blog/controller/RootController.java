/*
 *  Created by ZhongWenjie on 2019-01-05 22:48
 */

package org.zwj.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.zwj.blog.service.PageContentService;

@Controller
public class RootController {

    @Autowired private PageContentService pageContentService;

}

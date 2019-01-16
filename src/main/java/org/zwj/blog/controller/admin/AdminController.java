/*
 *  Created by ZhongWenjie on 2019-01-13 16:04
 */

package org.zwj.blog.controller.admin;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.zwj.blog.entity.PageContent;
import org.zwj.blog.service.PageContentService;
import org.zwj.blog.utils.FileUtils;
import org.zwj.blog.utils.Util;

import java.io.File;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Value("${html.content.location}")
    private String location;

    @Autowired private PageContentService pageContentService;

    @RequestMapping("/doUpload")
    public String upload(PageContent page) throws Exception {
        page = PageContent.create(page);
        String uuid = Util.randomUUIDToString();
        String baseDir = FilenameUtils.concat(location, uuid);
        String originalFileAbsolutePath = FileUtils.saveFile(page.getFile(), baseDir, false);
        page.setOriginalLocation(FileUtils.toRelativelyPathWithUnix(location,originalFileAbsolutePath));
        File html = FileUtils.findFileFirstOrLikeFirst(baseDir, "root.html", "html");
        page.setHtmlLocation(FileUtils.toRelativelyPathWithUnix(location, html.getAbsolutePath()));
        pageContentService.savePageContent(page);
        return "redirect:/";
    }
}

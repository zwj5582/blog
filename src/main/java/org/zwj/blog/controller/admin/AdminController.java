/*
 *  Created by ZhongWenjie on 2019-01-13 16:04
 */

package org.zwj.blog.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.zwj.blog.entity.PageContent;
import org.zwj.blog.service.PageContentService;
import org.zwj.blog.utils.FileUtils;
import org.zwj.blog.utils.Util;

import javax.transaction.NotSupportedException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Value("${html.content.location}")
    private String location;

    @Autowired
    private PageContentService pageContentService;

    @RequestMapping("/doUpload")
    public String upload(PageContent page)
            throws IOException, InvocationTargetException, NotSupportedException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        page = PageContent.create(page);
        String dirName = FileUtils.unzip(page.getFile(), location);
        File dir = new File(location, dirName);
        File targetHtml = new File(dir, page.getHtmlLocation());
        Collection<File> listFiles = org.apache.commons.io.FileUtils.listFiles(dir, new String[]{"html"}, true);
        File target = null;
        File firstHtml = null;
        for (File file : listFiles) {
            if (!Util.valid(firstHtml)) {
                firstHtml = file;
            }
            if (file.equals(targetHtml)) {
                target = file;
                break;
            }
        }
        if (!Util.valid(target)) {
            target = firstHtml;
        }
        String path = target.getAbsolutePath().replace(location,"");
        while (path.indexOf("/") == 0 || path.indexOf("\\") == 0) {
            path = path.substring(1);
        }
        page.setHtmlLocation(path.replaceAll("\\\\","/"));
        pageContentService.savePageContent(page);
        return "redirect:/";
    }

}

/*
 *  Created by ZhongWenjie on 2019-01-08 21:20
 */

package org.zwj.blog.controller;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.zwj.blog.common.ResponseEntitys;
import org.zwj.blog.entity.PageContent;
import org.zwj.blog.entity.PageHistory;
import org.zwj.blog.service.PageContentService;
import org.zwj.blog.utils.FileUtils;
import org.zwj.blog.utils.Util;
import org.zwj.blog.vo.PageContentVO;

import javax.transaction.NotSupportedException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

@Controller
public class PageController {

    @Value("${html.content.location}")
    private String location;

    @Autowired private PageContentService pageContentService;

    @RequestMapping(value = "/page/{id}")
    public String page(@PathVariable(value = "id") Integer id, Model model) {
        model.addAttribute("page", pageContentService.findById(id));
        return "page";
    }

    @GetMapping(value = "/page/list")
    public @ResponseBody ResponseEntity list(
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
            value = "/admin/page/update",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody ResponseEntity update(@RequestBody PageContent pageContent) throws Exception {
        pageContentService.update(pageContent);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/admin/page/list")
    public @ResponseBody ResponseEntity adminPageList(
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

    @RequestMapping(value = "/admin/page/doUpload")
    public @ResponseBody ResponseEntity upload(PageContent page) throws Exception {
        String uuid = Util.randomUUIDToString();
        String baseDir = FilenameUtils.concat(location, uuid);
        String originalFileAbsolutePath = FileUtils.saveFile(page.getFile(), baseDir, false);
        page.setOriginalLocation(FileUtils.toRelativelyPathWithUnix(location,originalFileAbsolutePath));
        File html = FileUtils.findFileFirstOrLikeFirst(baseDir, "root.html", "html");
        page.setHtmlLocation(FileUtils.toRelativelyPathWithUnix(location, html.getAbsolutePath()));
        pageContentService.savePageContent(page);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/admin/file/upload")
    public @ResponseBody ResponseEntity fileUpload(PageContent page) throws Exception {
        String uuid = Util.randomUUIDToString();
        String baseDir = FilenameUtils.concat(location, uuid);
        String originalFileAbsolutePath = FileUtils.saveFile(page.getFile(), baseDir, false);
        page.setOriginalLocation(FileUtils.toRelativelyPathWithUnix(location,originalFileAbsolutePath));
        File html = FileUtils.findFileFirstOrLikeFirst(baseDir, "root.html", "html");
        page.setHtmlLocation(FileUtils.toRelativelyPathWithUnix(location, html.getAbsolutePath()));
        final PageHistory currPageHistory = pageContentService.updateWithFile(page);
        return ResponseEntity.ok(
                ImmutableMap.of("version", currPageHistory.getVersionNum())
        );
    }

    @RequestMapping(value = "/admin/page/{version}")
    public String pageByVersion(@PathVariable(value = "version") Integer version ,Model model) {
        model.addAttribute("page", pageContentService.findByVersion(version));
        return "page";
    }

}

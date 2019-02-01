/*
 *  Created by ZhongWenjie on 2019-01-08 21:20
 */

package org.zwj.blog.controller;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
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
import org.zwj.blog.service.PageContentService;
import org.zwj.blog.utils.FileUtils;
import org.zwj.blog.utils.Util;
import org.zwj.blog.vo.PageContentVO;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class PageController {

    @Value("${html.content.location}")
    private String location;

    private static List<String> imagesEx = Lists.newArrayList();

    static {
        imagesEx.add("jpg");
        imagesEx.add("gif");
        imagesEx.add("jpeg");
        imagesEx.add("png");
        imagesEx.add("bmp");
        imagesEx.add("webp");

        imagesEx.add("jpg".toUpperCase());
        imagesEx.add("gif".toUpperCase());
        imagesEx.add("jpeg".toUpperCase());
        imagesEx.add("png".toUpperCase());
        imagesEx.add("bmp".toUpperCase());
        imagesEx.add("webp".toUpperCase());
    }

    @Autowired private ServletContext servletContext;

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
    public @ResponseBody ResponseEntity update(@RequestBody PageContent pageContent)
            throws Exception {
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
        if ("page".equals(page.getType())) {
            String uuid = Util.randomUUIDToString();
            String baseDir = FilenameUtils.concat(location, uuid);
            String originalFileAbsolutePath = FileUtils.saveFile(page.getFile(), baseDir, false);
            page.setOriginalLocation(
                    FileUtils.toRelativelyPathWithUnix(location, originalFileAbsolutePath));
            File html = FileUtils.findFileFirstOrLikeFirst(baseDir, "root.html", "html");
            page.setHtmlLocation(
                    FileUtils.toRelativelyPathWithUnix(location, html.getAbsolutePath()));
            pageContentService.savePageContent(page);
        } else pageContentService.saveContent(page);
        return ResponseEntity.ok(
                ImmutableMap.of("id",page.getId())
        );
    }

    @RequestMapping(value = "/admin/page/md/edit/{id}")
    public String mdEdit(@PathVariable(value = "id") Integer id, Model model) {
        model.addAttribute("page",pageContentService.findById(id));
        return "admin/mdEdit";
    }

    @RequestMapping(value = "/admin/save/md")
    public @ResponseBody ResponseEntity saveMdContent(PageContent pageContent) {
        pageContentService.saveMdContent(pageContent);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/admin/file/upload")
    public @ResponseBody ResponseEntity fileUpload(PageContent page) throws Exception {
        String uuid = Util.randomUUIDToString();
        String baseDir = FilenameUtils.concat(location, uuid);
        String originalFileAbsolutePath = FileUtils.saveFile(page.getFile(), baseDir, false);
        page.setOriginalLocation(
                FileUtils.toRelativelyPathWithUnix(location, originalFileAbsolutePath));
        File html = FileUtils.findFileFirstOrLikeFirst(baseDir, "root.html", "html");
        page.setHtmlLocation(FileUtils.toRelativelyPathWithUnix(location, html.getAbsolutePath()));
        pageContentService.updateWithFile(page);
        return ResponseEntity.ok(ImmutableMap.of("version", 0));
    }

    @RequestMapping(value = "/admin/page/{version}")
    public String pageByVersion(@PathVariable(value = "version") Integer version, Model model) {
        model.addAttribute("page", pageContentService.findByVersion(version));
        return "page";
    }

    @RequestMapping(value = "/admin/upload/md/img")
    public @ResponseBody ResponseEntity saveImage(
            @RequestParam(value = "editormd-image-file") MultipartFile file) throws IOException {
        String suffixPath =
                "images/"
                        + Util.randomUUIDToString()
                        + "."
                        + FilenameUtils.getExtension(file.getOriginalFilename());
        final String concat = FilenameUtils.concat(location, suffixPath);
        file.transferTo(new File(concat));
        final String httpServerHost = (String) servletContext.getAttribute("httpServerHost");
        return ResponseEntity.ok(
                ImmutableMap.of(
                        "success",
                        1,
                        "message",
                        "上传成功",
                        "url",
                        httpServerHost + "/data/" + suffixPath));
    }

    @RequestMapping(value = "/admin/upload/images")
    public @ResponseBody ResponseEntity fetchAllUploadImg() {
        Collection<File> images =
                FileUtils.listFiles(
                        new File(FilenameUtils.concat(location, "images")),
                        imagesEx.toArray(new String[0]));
        if (Util.valid(images)) {
            return ResponseEntity.ok(
                    images.stream()
                            .sorted(
                                    (f1, f2) -> {
                                        try {
                                            return Files.readAttributes(
                                                            f2.toPath(), BasicFileAttributes.class)
                                                    .lastModifiedTime()
                                                    .compareTo(
                                                            Files.readAttributes(
                                                                            f1.toPath(),
                                                                            BasicFileAttributes.class)
                                                                    .lastModifiedTime()
                                                    );
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        return 0;
                                    })
                            .map(file -> FilenameUtils.getName(file.getAbsolutePath()))
                            .collect(Collectors.toList()));
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/admin/upload/md/{imageName}", method = RequestMethod.DELETE)
    public @ResponseBody ResponseEntity deleteImg(@PathVariable String imageName) throws IOException {
        List<File> images = FileUtils.findFile(FilenameUtils.concat(location, "images"), imageName, imagesEx.toArray(new String[0]));
        if (Util.valid(images)) {
            for (File image : images) {
                org.apache.commons.io.FileUtils.forceDelete(image);
            }
        }
        return ResponseEntity.ok().build();
    }
}

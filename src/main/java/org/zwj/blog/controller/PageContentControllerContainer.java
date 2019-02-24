/*
 *  Created by ZhongWenjie on 2019-02-01 16:01
 */

package org.zwj.blog.controller;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
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
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class PageContentControllerContainer {

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

    @Controller
    @RequestMapping("/admin")
    static public class PageContentControllerReturnPage {

        @Autowired
        private PageContentService pageContentService;

        @RequestMapping(value = "/page/md/edit/{id}")
        public String mdEdit(@PathVariable(value = "id") Integer id, Model model) {
            model.addAttribute("page",pageContentService.findPageContentById(id));
            return "admin/mdEdit";
        }

        @RequestMapping(value = "/page/{version}")
        public String pageByVersion(@PathVariable(value = "version") Integer version, Model model) {
            model.addAttribute("page", pageContentService.findByVersion(version));
            return "page";
        }

    }

    @RestController
    @RequestMapping(value = "/admin")
    static public class PageContentControllerWithRest {

        @Value("${html.content.location}")
        private String location;

        @Autowired private ServletContext servletContext;

        @Autowired private PageContentService pageContentService;

        @PostMapping(
                value = "/page/update",
                consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
                produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
        public ResponseEntity update(@RequestBody PageContent pageContent)
                throws Exception {
            pageContent.setLastUpdate(new Date());
            pageContentService.update(pageContent);
            return ResponseEntity.ok().build();
        }

        @GetMapping(value = "/page/list")
        public ResponseEntity adminPageList(
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

        @RequestMapping(value = "/save/md")
        public ResponseEntity saveMdContent(PageContent pageContent) {
            pageContentService.saveMdContent(pageContent);
            return ResponseEntity.ok().build();
        }

        @RequestMapping(value = "/page/doUpload")
        public ResponseEntity upload(PageContent page) throws Exception {
            if (Util.valid(page.getFile())) doFileUpload(page);
            page.setState("waiting_publish");
            page.setOriginalCreateTime(new Date());
            page.setLastUpdate(new Date());
            pageContentService.saveContent(page);
            return ResponseEntity.ok(
                    ImmutableMap.of("id",page.getId(),"type",page.getType())
            );
        }

        @RequestMapping(value = "/file/upload")
        public ResponseEntity fileUpload(PageContent page) throws Exception {
            if (!Util.valid(page.getFile())) throw new Exception("Not Found Files!");
            doFileUpload(page);
            page.setLastUpdate(new Date());
            pageContentService.updateWithFile(page);
            return ResponseEntity.ok().build();
        }

        private void doFileUpload(PageContent page) throws Exception{
            if ("md".equals(FilenameUtils.getExtension(page.getFile().getOriginalFilename()))) {
                page.setType("md");
                page.setMdContent(IOUtils.toString(page.getFile().getInputStream(), Charset.forName("UTF-8")));
            }else {
                String uuid = Util.randomUUIDToString();
                String baseDir = FilenameUtils.concat(location, uuid);
                String originalFileAbsolutePath = FileUtils.saveFile(page.getFile(), baseDir, false);
                File editFile = FileUtils.findFileFirstOrLikeFirst(baseDir, "root.html", "html");
                final String extension = FilenameUtils.getExtension(editFile.getName()).toUpperCase();
                if (!"html".equals(extension)) {
                    org.apache.commons.io.FileUtils.forceDelete(new File(baseDir));
                    throw new Exception("Not Found HTML file!");
                }
                page.setType("html");
                page.setOriginalLocation(
                        FileUtils.toRelativelyPathWithUnix(location, originalFileAbsolutePath));
                page.setHtmlLocation(
                        FileUtils.toRelativelyPathWithUnix(location, editFile.getAbsolutePath()));
            }
        }

        @RequestMapping(value = "/upload/md/img")
        public ResponseEntity saveImage(
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

        @RequestMapping(value = "/upload/images")
        public ResponseEntity fetchAllUploadImg() {
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

        @RequestMapping(value = "/upload/md/{imageName}", method = RequestMethod.DELETE)
        public ResponseEntity deleteImg(@PathVariable String imageName) throws IOException {
            List<File> images = FileUtils.findFile(FilenameUtils.concat(location, "images"), imageName, imagesEx.toArray(new String[0]));
            if (Util.valid(images)) {
                for (File image : images) {
                    org.apache.commons.io.FileUtils.forceDelete(image);
                }
            }
            return ResponseEntity.ok().build();
        }
    }


}

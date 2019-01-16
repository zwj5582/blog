/*
 *  Created by ZhongWenjie on 2019-01-14 14:45
 */

package org.zwj.blog.utils;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.NotSupportedException;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.file.FileSystemException;
import java.util.*;

public class FileUtils {

    private static final int BUFFER_SIZE = 1024;

    public static final String ZIP = "zip";
    public static final String TAR = "tar";

    private static Map<String, Class<? extends ArchiveInputStream>> map =
            ImmutableMap.of(
                    "zip", ZipArchiveInputStream.class,
                    "tar", TarArchiveInputStream.class);

    private static ArchiveInputStream createInputStream(String type, InputStream in)
            throws NotSupportedException, IllegalAccessException, InvocationTargetException,
                    InstantiationException, NoSuchMethodException {
        Assert.notNull(type, " File type can not null ");
        Class<? extends ArchiveInputStream> inputClass = map.get(type);
        if (!Util.valid(inputClass)) throw new NotSupportedException(" File type " + type);
        Constructor<? extends ArchiveInputStream> constructor =
                inputClass.getConstructor(InputStream.class);
        return constructor.newInstance(in);
    }

    public static String deCompress(File file, String baseDir, Boolean delete)
            throws IOException, InvocationTargetException, NoSuchMethodException,
                    NotSupportedException, InstantiationException, IllegalAccessException {
        baseDir = Util.valid(baseDir) ? baseDir : file.getParent();
        File baseDirFile = new File(baseDir);
        if (!baseDirFile.exists()) org.apache.commons.io.FileUtils.forceMkdir(baseDirFile);
        try (ArchiveInputStream archiveInputStream =
                createInputStream(
                        FilenameUtils.getExtension(file.getName()), new FileInputStream(file))) {
            ArchiveEntry archiveEntry;
            String tmpBaseDir = baseDir;
            while (Util.valid(archiveEntry = archiveInputStream.getNextEntry())) {
                if (archiveEntry.isDirectory()) {
                    String name = archiveEntry.getName();
                    if (Util.isSpecialChar(archiveEntry.getName()))
                        name = Util.randomUUIDToString();
                    File dir = new File(tmpBaseDir, name);
                    org.apache.commons.io.FileUtils.forceMkdir(dir);
                    tmpBaseDir = dir.getAbsolutePath();
                } else {
                    String baseName = FilenameUtils.getBaseName(archiveEntry.getName());
                    String extension = FilenameUtils.getExtension(archiveEntry.getName());
                    if (Util.isSpecialChar(baseName))
                        baseName = Util.randomUUIDToString();
                    String name = baseName + "." + extension;
                    try (OutputStream out =
                            new BufferedOutputStream(
                                    new FileOutputStream(new File(tmpBaseDir, name)),
                                    BUFFER_SIZE)) {
                        IOUtils.copy(archiveInputStream, out);
                    }
                }
            }
        }
        String absolutePath = file.getAbsolutePath();
        if (Util.valid(delete)) {
            org.apache.commons.io.FileUtils.forceDelete(file);
        }
        return absolutePath;
    }

    public static String deCompress(MultipartFile file, String baseDir, Boolean delete)
            throws IOException, NotSupportedException, InstantiationException,
                    IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        File baseDirFile = new File(baseDir);
        if (!baseDirFile.exists()) org.apache.commons.io.FileUtils.forceMkdir(baseDirFile);
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String baseName = FilenameUtils.getBaseName(file.getOriginalFilename());
        if ( Util.isSpecialChar(baseName) )
            baseName = Util.randomUUIDToString();
        File tmp = new File(baseDir, baseName + "." + extension);
        file.transferTo(tmp);
        deCompress(tmp, null, delete);
        return tmp.getAbsolutePath();
    }

    public static void copyFile(File source, File target) throws IOException {
        org.apache.commons.io.FileUtils.copyFile(source, target);
    }

    public static void saveFile(File file, String baseDir, Boolean delete)
            throws NoSuchMethodException, IOException, NotSupportedException,
                    InstantiationException, IllegalAccessException, InvocationTargetException {
        File dir = new File(baseDir);
        if (!dir.exists())
            org.apache.commons.io.FileUtils.forceMkdir(dir);
        if (map.keySet().contains(FilenameUtils.getExtension(file.getName()).toLowerCase()))
            deCompress(file, baseDir, delete);
        else {
            String extension = FilenameUtils.getExtension(file.getName());
            String baseName = FilenameUtils.getBaseName(file.getName());
            if ( Util.isSpecialChar(baseName) )
                baseName = Util.randomUUIDToString();
            File tmp = new File(baseDir, baseName + "." + extension);
            copyFile(file, tmp);
        }
    }

    public static String saveFile(MultipartFile file, String baseDir, Boolean delete)
            throws NoSuchMethodException, IOException, NotSupportedException,
            InstantiationException, IllegalAccessException, InvocationTargetException {
        File dir = new File(baseDir);
        if (!dir.exists())
            org.apache.commons.io.FileUtils.forceMkdir(dir);
        if (map.keySet().contains(FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase()))
            return deCompress(file, baseDir, delete);
        else{
            String extension = FilenameUtils.getExtension(file.getOriginalFilename());
            String baseName = FilenameUtils.getBaseName(file.getOriginalFilename());
            if ( Util.isSpecialChar(baseName) )
                baseName = Util.randomUUIDToString();
            File tmp = new File(baseDir, baseName + "." + extension);
            file.transferTo(tmp);
            return tmp.getAbsolutePath();
        }
    }

    public static Collection<File> listFiles(File dir, String... extensions) {
        return org.apache.commons.io.FileUtils.listFiles(dir, extensions, true);
    }

    public static File listFilesFirst(String baseDir, String... extensions) {
        return Util.getFirst(listFiles(new File(baseDir), extensions));
    }

    public static File findFileFirst(String baseDir, String fileName, String... extensions) {
        for (File file : listFiles(new File(baseDir), extensions)) {
            if (FilenameUtils.equalsNormalizedOnSystem(file.getName(), fileName)) {
                return file;
            }
        }
        return null;
    }

    public static File findFileFirstOrLikeFirst(
            String baseDir, String fileName, String... extensions) {
        File first = Util.getFirst(findFile(baseDir, fileName, extensions));
        if (Util.valid(first)) return first;
        return listFilesFirst(baseDir, extensions);
    }

    public static List<File> findFile(String baseDir, String fileName, String... extensions) {
        List<File> files = Lists.newArrayList();
        for (File file : listFiles(new File(baseDir), extensions)) {
            if (FilenameUtils.equalsNormalizedOnSystem(file.getName(), fileName)) {
                files.add(file);
            }
        }
        return files;
    }

    public static String toRelativelyPathWithUnix(String baseDir, String absolutePath) {
        String relativelyPath =
                FilenameUtils.separatorsToUnix(absolutePath)
                        .replace(FilenameUtils.separatorsToUnix(baseDir), "");
        while (relativelyPath.indexOf("/") == 0) {
            relativelyPath = relativelyPath.substring(1);
        }
        return relativelyPath;
    }
}

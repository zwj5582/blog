/*
 *  Created by ZhongWenjie on 2019-01-14 14:45
 */

package org.zwj.blog.utils;

import com.google.common.collect.ImmutableMap;
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
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.NotSupportedException;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.FileSystemException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class FileUtils {

    private static final int BUFFER_SIZE = 1024;

    public static final String ZIP = "zip";
    public static final String TAR = "tar";

    private static Map<String, Class<? extends ArchiveInputStream>> map = ImmutableMap.of(
            "zip", ZipArchiveInputStream.class,
            "tar", TarArchiveInputStream.class
    );

    private static ArchiveInputStream createInputStream(String type ,InputStream in)
            throws NotSupportedException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        Assert.notNull(type, " File type can not null ");
        Class<? extends ArchiveInputStream> inputClass = map.get(type);
        if (!Util.valid(inputClass)) throw new NotSupportedException(" File type " + type);
        Constructor<? extends ArchiveInputStream> constructor = inputClass.getConstructor(InputStream.class);
        return constructor.newInstance(in);
    }

    public static void deCompress(File file, String baseDir ,Boolean delete)
            throws IOException, InvocationTargetException, NoSuchMethodException, NotSupportedException, InstantiationException, IllegalAccessException {
        baseDir = Util.valid(baseDir) ? baseDir : file.getParent();
        try(ArchiveInputStream archiveInputStream = createInputStream(FilenameUtils.getExtension(file.getName()), new FileInputStream(file))){
            ArchiveEntry archiveEntry;
            while (Util.valid( archiveEntry = archiveInputStream.getNextEntry() )){
                if (archiveEntry.isDirectory()){
                    File dir = new File(baseDir,archiveEntry.getName());
                    if (dir.mkdirs())
                        throw new NotActiveException();
                }else {
                    try(OutputStream out = new BufferedOutputStream(new FileOutputStream(new File(baseDir, archiveEntry.getName())), BUFFER_SIZE)){
                        IOUtils.copy(archiveInputStream, out);
                    }
                }
            }
        }
        if (Util.valid(delete)){
            file.delete();
        }
    }

    public static void deCompress(MultipartFile file, String baseDir, Boolean delete)
            throws IOException, NotSupportedException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        File tmp = new File(baseDir, Objects.requireNonNull(file.getOriginalFilename()));
        file.transferTo(tmp);
        deCompress(tmp, null, delete);
    }

    if

}

package com.customization.yll.common.util;

import cn.hutool.core.util.StrUtil;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @author yaolilin
 * @desc 压缩包工具类
 * @date 2024/9/4
 **/
@UtilityClass
public class ZipUtil {

    /**
     * 将指定文件打包成压缩包
     *
     * @param sourcePath 要压缩的文件路径，可以是文件或目录
     * @return 压缩后的 ZIP 文件
     * @throws IOException 如果压缩过程中出现 I/O 错误
     */
    public static File zip(String sourcePath) throws IOException {
        return zip(sourcePath, null, true);
    }

    /**
     * 将指定文件打包成压缩包
     *
     * @param sourcePath  要压缩的文件路径，可以是文件或目录
     * @param zipFilePath 压缩后的 ZIP 文件路径，如果存在该路径文件，则会覆盖该文件
     * @return 压缩后的 ZIP 文件
     * @throws IOException 如果压缩过程中出现 I/O 错误
     */
    public static File zip(String sourcePath, @Nullable String zipFilePath) throws IOException {
        return zip(sourcePath, zipFilePath, true);
    }

    /**
     * 将指定文件打包成压缩包（使用默认 UTF-8 编码）
     *
     * @param sourcePath  要压缩的文件路径，可以是文件或目录
     * @param zipFilePath 压缩后的 ZIP 文件路径，如果存在该路径文件，则会覆盖该文件
     * @param withRoot    是否包含根目录。true: 压缩包中包含根目录；false: 只包含根目录下的所有文件
     * @return 压缩后的 ZIP 文件
     * @throws IOException 如果压缩过程中出现 I/O 错误
     */
    public static File zip(String sourcePath, @Nullable String zipFilePath, boolean withRoot) throws IOException {
        return zip(sourcePath, zipFilePath, withRoot, StandardCharsets.UTF_8);
    }

    /**
     * 将指定文件打包成压缩包（使用指定字符集编码）
     *
     * @param sourcePath  要压缩的文件路径，可以是文件或目录
     * @param zipFilePath 压缩后的 ZIP 文件路径，如果存在该路径文件，则会覆盖该文件
     * @param withRoot    是否包含根目录。true: 压缩包中包含根目录；false: 只包含根目录下的所有文件
     * @param charset     字符集编码，用于压缩包内文件名的编码
     * @return 压缩后的 ZIP 文件
     * @throws IOException 如果压缩过程中出现 I/O 错误
     */
    public static File zip(String sourcePath, @Nullable String zipFilePath, boolean withRoot, Charset charset) throws IOException {
        if (StrUtil.isBlank(sourcePath)) {
            throw new IllegalArgumentException("要压缩的文件路径不能为空");
        }
        if (charset == null) {
            throw new IllegalArgumentException("字符集编码不能为空");
        }
        Path sourcePathObj = Paths.get(sourcePath);
        if (Files.notExists(sourcePathObj)) {
            throw new FileNotFoundException("源文件路径不存在: " + sourcePath);
        }
        zipFilePath = getZipFilePath(sourcePath, zipFilePath);

        try (ZipOutputStream zipOut = new ZipOutputStream(Files.newOutputStream(Paths.get(zipFilePath)), charset);
             Stream<Path> walk = Files.walk(sourcePathObj)) {
            // 基础路径，用于计算压缩文件在压缩包内的路径
            Path basePath;
            if (Files.isDirectory(sourcePathObj)) {
                // 确定基础路径：如果 withRoot 为 true，使用源目录的父目录；否则使用源目录本身
                basePath = withRoot ? sourcePathObj.getParent() : sourcePathObj;
            } else {
                // 如果要压缩的文件不是目录，则基础路径为文件的父目录
                basePath = sourcePathObj.getParent();
            }
            Iterator<Path> iterator = walk.iterator();
            while (iterator.hasNext()) {
                Path path = iterator.next();
                // 如果压缩不包含根目录，则跳过根目录本身
                if (!withRoot && Files.isDirectory(path) && path.equals(sourcePathObj)) {
                    continue;
                }

                // 添加文件到压缩包
                addZipFile(basePath, path, zipOut);
            }
        }
        return new File(zipFilePath);
    }

    @NotNull
    private static String getZipFilePath(String sourcePath, @Nullable String zipFilePath) {
        Path sourcePathObj = Paths.get(sourcePath);
        if (StrUtil.isBlank(zipFilePath)) {
            String fileName = FileUtil.getFileNameWithoutSuffix(sourcePath);
            Path parent = sourcePathObj.getParent();
            if (parent != null) {
                zipFilePath = parent.resolve(fileName + ".zip").toString();
            } else {
                zipFilePath = FileUtil.getSeparator() + fileName + ".zip";
            }
        }
        return zipFilePath;
    }

    private static void addZipFile(Path basePath, Path zipFilePath, ZipOutputStream zipOut) throws IOException {
        // 获取基础路径与压缩文件路径的相对路径，也就是压缩文件在压缩包内的路径
        Path relativePath = basePath.relativize(zipFilePath);
        String entryName = relativePath.toString().replace("\\", "/");

        // 如果文件是目录，则添加目录条目（以斜杠结尾）
        if (Files.isDirectory(zipFilePath)) {
            if (!entryName.endsWith(FileUtil.getSeparator())) {
                entryName += FileUtil.getSeparator();
            }
            ZipEntry zipEntry = new ZipEntry(entryName);
            zipOut.putNextEntry(zipEntry);
            zipOut.closeEntry();
        } else {
            // 添加文件条目
            ZipEntry zipEntry = new ZipEntry(entryName);
            zipOut.putNextEntry(zipEntry);
            Files.copy(zipFilePath, zipOut);
            zipOut.closeEntry();
        }
    }


    /**
     * 解压文件，解压后会在当前压缩包目录生成一个和压缩包同名的文件夹，里面包含解压的文件
     *
     * @param zipFilePath 压缩文件路径
     * @return 解压后的文件夹
     * @throws IOException 解压出现的IO异常
     */
    public static File unzip(String zipFilePath) throws IOException {
        return unzip(zipFilePath, null);
    }

    /**
     * 解压文件
     *
     * @param zipFilePath 压缩文件路径
     * @param savePath    解压文件保存路径，该路径内将会包含所有被解压的文件，如果传入空，则会在当前压缩包目录生成一个和压缩包同名的文件夹，
     *                    将文件解压到该文件夹内。
     * @return 解压后的文件夹
     * @throws IOException 解压出现的IO异常
     */
    public static File unzip(String zipFilePath, @Nullable String savePath) throws IOException {
        if (StrUtil.isBlank(zipFilePath)) {
            throw new IllegalArgumentException("zip 文件路径不能为空");
        }
        if (Files.notExists(Paths.get(zipFilePath))) {
            throw new FileNotFoundException("zip 文件不存在：" + zipFilePath);
        }
        if (StrUtil.isBlank(savePath)) {
            if (zipFilePath.contains(".")) {
                savePath = zipFilePath.substring(0, zipFilePath.lastIndexOf("."));
            } else {
                savePath = zipFilePath;
            }
        }
        try (ZipFile zipFile = new ZipFile(zipFilePath)) {
            // 解压路径为压缩文件所在的目录，会先创建一个与压缩文件名称一样的目录
            Path saveDir = Paths.get(savePath);
            if (!Files.exists(saveDir)) {
                Files.createDirectory(saveDir);
            }
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();
                Path outFile = Paths.get(savePath + FileUtil.getSeparator() + zipEntry.getName());
                // 如果解压的文件已存在，则跳过
                if (Files.exists(outFile)) {
                    continue;
                }
                // 如果文件的上级目录不存在，则创建目录（树）
                if (!Files.exists(outFile.getParent())) {
                    Files.createDirectories(outFile.getParent());
                }
                if (zipEntry.isDirectory()) {
                    Files.createDirectory(outFile);
                    continue;
                }
                // 对包内的文件进行解压，复制文件
                Files.copy(zipFile.getInputStream(zipEntry), outFile);
            }
        }
        return new File(savePath);
    }

    /**
     * 将指定文件打包成压缩包（使用指定字符集编码）
     *
     * @param sourcePath 要压缩的文件路径，可以是文件或目录
     * @param charset    字符集编码，用于压缩包内文件名的编码
     * @return 压缩后的 ZIP 文件
     * @throws IOException 如果压缩过程中出现 I/O 错误
     */
    public static File zip(String sourcePath, Charset charset) throws IOException {
        return zip(sourcePath, null, true, charset);
    }

    /**
     * 将指定文件打包成压缩包（使用指定字符集编码）
     *
     * @param sourcePath  要压缩的文件路径，可以是文件或目录
     * @param zipFilePath 压缩后的 ZIP 文件路径，如果存在该路径文件，则会覆盖该文件
     * @param charset     字符集编码，用于压缩包内文件名的编码
     * @return 压缩后的 ZIP 文件
     * @throws IOException 如果压缩过程中出现 I/O 错误
     */
    public static File zip(String sourcePath, @Nullable String zipFilePath, Charset charset) throws IOException {
        return zip(sourcePath, zipFilePath, true, charset);
    }
}

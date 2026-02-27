package com.customization.yll.common.util;

import lombok.experimental.UtilityClass;
import org.apache.pdfbox.pdmodel.PDDocument;
import weaver.general.GCONST;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author yaolilin
 * @desc 文件工具类
 * @date 2024/9/11
 **/
@UtilityClass
public class FileUtil {

    /**
     * 获取文件md5（大写）
     *
     * @param filePath 文件路径
     * @return 文件md5
     * @throws IOException              文件异常
     * @throws NoSuchAlgorithmException 算法异常
     */
    public static String getFileMd5ToUpperCase(String filePath) throws IOException, NoSuchAlgorithmException {
        return getFileMd5ToLowerCase(filePath).toUpperCase();
    }

    /**
     * 获取文件md5（小写）
     *
     * @param filePath 文件路径
     * @return 文件md5
     * @throws IOException              文件异常
     * @throws NoSuchAlgorithmException 算法异常
     */
    public static String getFileMd5ToLowerCase(String filePath) throws IOException, NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("md5");
        try (FileInputStream fis = new FileInputStream(filePath);
             DigestInputStream digestInputStream = new DigestInputStream(fis, md5)) {
            // read的过程中进行MD5处理，直到读完文件
            byte[] buffer = new byte[4096];
            while (digestInputStream.read(buffer) > 0) {
            }
            // 获取最终的MessageDigest
            md5 = digestInputStream.getMessageDigest();
            // 拿到结果，也是字节数组，包含16个元素
            byte[] rb = md5.digest();
            // 把字节数组转换成32位十六进制的字符串
            StringBuilder sb = new StringBuilder();
            for (byte b : rb) {
                String a = Integer.toHexString(0XFF & b);
                if (a.length() < 2) {
                    a = '0' + a;
                }
                sb.append(a);
            }
            //返回转换后的字符串
            return sb.toString().toLowerCase();
        }
    }

    /**
     * 获取pdf页数
     *
     * @param filePath 文件路径
     * @return pdf 页数
     * @throws IOException 读取pdf文件出现的IO异常
     */
    public static int getPdfPageNumber(String filePath) throws IOException {
        try (PDDocument document = PDDocument.load(new File(filePath))) {
            return document.getNumberOfPages();
        }
    }

    public static String getSeparator() {
        return FileSystems.getDefault().getSeparator();
    }

    /**
     * 获取文件扩展名
     *
     * @param fileName 文件名
     * @return 文件扩展名
     */
    public static String getSuffix(String fileName) {
        if (fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        return "";
    }

    /**
     * 获取服务器中的临时文件夹路径
     *
     * @return 临时文件夹路径
     */
    public static String getTempDirPath() {
        return GCONST.getRootPath() + "filesystem" + getSeparator() + "temp";
    }

    /**
     * 获取文件名,不包含后缀
     *
     * @param fullFileName 文件全名
     * @return 文件名
     */
    public static String getFileNameWithoutSuffix(String fullFileName) {
        if (fullFileName.contains(".")) {
            if (fullFileName.contains(getSeparator())) {
                return fullFileName.substring(fullFileName.lastIndexOf(getSeparator()) + 1,
                        fullFileName.lastIndexOf("."));
            }
            return fullFileName.substring(0, fullFileName.lastIndexOf("."));
        }
        return fullFileName;
    }
}

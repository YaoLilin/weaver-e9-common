package com.customization.yll.common.doc;

import com.customization.yll.common.doc.bean.DocQRConfig;
import com.customization.yll.common.util.FileUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.Data;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSpacing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STLineSpacingRule;
import weaver.general.GCONST;
import weaver.integration.logging.Logger;
import weaver.integration.logging.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author 姚礼林
 * @desc word文档顶部添加二维码，可设置二维码的位置，如右边，左边，居中，只支持docx文档
 * @date 2025/3/28
 **/
@Data
public class DocInsertQrManager {
    private static final String TEMP_DIR = GCONST.getRootPath() + FileUtil.getSeparator() + "filesystem" +
            FileUtil.getSeparator() + "temp" + FileUtil.getSeparator();
    private static final String DOC_QR_IMAGE_NAME = "head_qr.png";
    private static final double MIN_RATIO = 0.001;
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private DocQRConfig qrConfig;

    public DocInsertQrManager(DocQRConfig qrConfig) {
        this.qrConfig = qrConfig;
    }

    /**
     * 插入二维码到文档，如果之前已经插入过二维码，则删除二维码重新插入，只支持docx文档
     *
     * @param qrContent         二维码内容
     * @param sourceDocFilePath 原始文档路径
     * @param outDocPath        输出文档路径
     * @return 是否成功
     */
    public boolean insertQr(String qrContent, String sourceDocFilePath, String outDocPath) {
        String suffix = FileUtil.getSuffix(sourceDocFilePath);
        if (!"docx".equals(suffix)) {
            log.error("格式不支持，只支持 docx 格式文档，当前格式：" + suffix);
            return false;
        }
        if (!createTempDir()) {
            log.error("创建临时目录失败");
            return false;
        }
        String qrImageFilePath = TEMP_DIR + "qr-" + UUID.randomUUID() + ".png";
        if (!generateQRCode(qrContent, qrImageFilePath)) {
            return false;
        }
        if (!insertQrImageToDoc(sourceDocFilePath, outDocPath, qrImageFilePath)) {
            return false;
        }
        deleteTempQr(qrImageFilePath);
        return true;
    }

    private boolean insertQrImageToDoc(String sourceDocFilePath, String outDocPath, String qrImageFilePath) {
        // 设置更小的最小解压比例以处理触发安全机制的字体文件
        ZipSecureFile.setMinInflateRatio(MIN_RATIO);
        try (FileInputStream fis = new FileInputStream(sourceDocFilePath);
             InputStream qrInputSt = Files.newInputStream(Paths.get(qrImageFilePath));
             XWPFDocument document = new XWPFDocument(fis)) {

            if (containsQRCode(sourceDocFilePath)) {
                // 如果存在二维码，则删除第一个段落
                document.removeBodyElement(0);
            }
            // 获取文档的第一个段落的位置
            XmlCursor cursor = document.getDocument().getBody().getPArray(0).newCursor();
            // 在文档的最前面插入一个新的段落
            XWPFParagraph newParagraph = document.insertNewParagraph(cursor);
            // 设置段落对齐方式
            newParagraph.setAlignment(this.qrConfig.getQrAlign());
            setParagraphSpacing(newParagraph);
            XWPFRun run = newParagraph.createRun();
            // 插入二维码图片
            run.addPicture(qrInputSt, Document.PICTURE_TYPE_PNG,
                    DOC_QR_IMAGE_NAME, Units.toEMU(this.qrConfig.getQrSize()), Units.toEMU(this.qrConfig.getQrSize()));
            cursor.dispose();

            // 保存文档
            try (FileOutputStream out = new FileOutputStream(outDocPath)) {
                document.write(out);
            } catch (IOException e) {
                log.error("保存文档发生异常", e);
                return false;
            }
        } catch (IOException | InvalidFormatException e) {
            log.error("插入二维码发生异常", e);
            return false;
        }
        return true;
    }

    /**
     * 检查文档中是否已经插入了二维码（仅检查使用本类插入的二维码）
     *
     * @param docFilePath 文档路径
     * @return 是否已经插入了二维码
     */
    public boolean containsQRCode(String docFilePath) throws IOException {
        return !getQrCodePictureContent(docFilePath).isEmpty();
    }

    /**
     * 获取之前插入该文档的二维码中的内容（仅适用于使用本类插入的二维码）
     *
     * @param docFilePath 文档路径
     * @return 如果没有二维码则返回空字符串，否则返回二维码内容
     * @throws IOException IO异常
     */
    public String getQrCodeContent(String docFilePath) throws IOException {
        return getQrCodePictureContent(docFilePath);
    }

    private boolean createTempDir() {
        Path tempDirPath = Paths.get(TEMP_DIR);
        if (!Files.exists(tempDirPath)) {
            try {
                Files.createDirectory(tempDirPath);
                return true;
            } catch (IOException e) {
                log.error("创建临时目录失败");
                return false;
            }
        }
        return true;
    }

    private void setParagraphSpacing(XWPFParagraph paragraph) {
        // 设置段落边距，即二维码的距离
        CTPPr pPr = paragraph.getCTP().getPPr();
        if (pPr == null) {
            pPr = paragraph.getCTP().addNewPPr();
        }
        CTSpacing spacing = pPr.getSpacing();
        if (spacing == null) {
            spacing = pPr.addNewSpacing();
        }
        // 距离顶部距离
        spacing.setBefore(BigInteger.valueOf(this.qrConfig.getQrMarginTop() * 20L));
        // 距离底部距离
        spacing.setAfter(BigInteger.valueOf(this.qrConfig.getQrMarginBottom() * 20L));

        // 设置行距为单倍行距，确保二维码图片显示完整
        // 单倍行距对应值为240（20磅 * 12 = 240）
        spacing.setLine(BigInteger.valueOf(240));
        // 设置行距规则为单倍行距
        spacing.setLineRule(STLineSpacingRule.AUTO);

        if (this.qrConfig.getQrAlign() == ParagraphAlignment.RIGHT) {
            // 设置右缩进
            paragraph.setIndentationRight(this.qrConfig.getQrMarginX() * 20);
        } else {
            // 设置左缩进
            paragraph.setIndentationLeft(this.qrConfig.getQrMarginX() * 20);
        }
    }

    private void deleteTempQr(String qrFilePath) {
        Path qrPath = Paths.get(qrFilePath);
        if (Files.exists(qrPath)) {
            try {
                Files.delete(qrPath);
            } catch (IOException e) {
                log.error("删除临时二维码失败");
            }
        }
    }

    private boolean generateQRCode(String text, String qrFilePath) {
        try {
            BitMatrix bitMatrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE,
                    this.qrConfig.getQrImageSize(), this.qrConfig.getQrImageSize());
            MatrixToImageWriter.writeToFile(bitMatrix, "png", new File(qrFilePath));
            return true;
        } catch (Exception e) {
            log.error("生成二维码失败");
            return false;
        }
    }

    private String getQrCodePictureContent(String docFilePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(docFilePath)) {
            Optional<XWPFPictureData> pictureData = getQrCodePicture(fis);
            if (pictureData.isPresent()) {
                return decodeQRCode(pictureData.get().getData());
            }
        }
        return "";
    }


    private static Optional<XWPFPictureData> getQrCodePicture(FileInputStream fis) throws IOException {
        // 设置更小的最小解压比例以处理触发安全机制的字体文件
        ZipSecureFile.setMinInflateRatio(MIN_RATIO);
        try (XWPFDocument document = new XWPFDocument(fis)) {
            // 获取文档的第一个段落
            XWPFParagraph firstParagraph = document.getParagraphs().get(0);
            // 检查第一个段落中有没有二维码图片
            for (XWPFRun run : firstParagraph.getRuns()) {
                List<XWPFPicture> pictures = run.getEmbeddedPictures();
                for (XWPFPicture picture : pictures) {
                    if (picture.getDescription().equals(DOC_QR_IMAGE_NAME)) {
                        return Optional.of(picture.getPictureData());
                    }
                }
            }
        }
        return Optional.empty();
    }


    private String decodeQRCode(byte[] imageData) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData)) {
            BufferedImage bufferedImage = ImageIO.read(inputStream);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(bufferedImage)));
            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText();
        } catch (Exception e) {
            log.error("解码二维码失败", e);
            return "";
        }
    }
}

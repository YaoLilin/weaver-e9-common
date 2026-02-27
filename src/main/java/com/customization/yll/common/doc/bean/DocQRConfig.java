package com.customization.yll.common.doc.bean;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;

/**
 * @author 姚礼林
 * @desc 文档插入二维码配置
 * @date 2025/3/28
 **/
public class DocQRConfig {
    /**
     * 二维码在文档中的大小
     */
    private int qrSize = 70;
    /**
     * 二维码位置
     */
    private ParagraphAlignment qrAlign = ParagraphAlignment.RIGHT;
    /**
     * 二维码距离顶部距离，单位：磅
     */
    private int qrMarginTop = 0;
    /**
     * 二维码距离底部距离，单位：磅
     */
    private int qrMarginBottom = 0;
    /**
     * 二维码横向距离，如果二维码位置是在右边，则是距离右边多少距离，其它位置都是距离左边多少距离，单位：磅
     */
    private int qrMarginX = 0;
    /**
     * 二维码图像大小，单位：像素
     */
    private int qrImageSize = 500;

    public int getQrSize() {
        return qrSize;
    }

    public void setQrSize(int qrSize) {
        this.qrSize = qrSize;
    }

    public ParagraphAlignment getQrAlign() {
        return qrAlign;
    }

    public void setQrAlign(ParagraphAlignment qrAlign) {
        this.qrAlign = qrAlign;
    }

    public int getQrMarginTop() {
        return qrMarginTop;
    }

    public void setQrMarginTop(int qrMarginTop) {
        this.qrMarginTop = qrMarginTop;
    }

    public int getQrMarginBottom() {
        return qrMarginBottom;
    }

    public void setQrMarginBottom(int qrMarginBottom) {
        this.qrMarginBottom = qrMarginBottom;
    }

    public int getQrMarginX() {
        return qrMarginX;
    }

    public void setQrMarginX(int qrMarginX) {
        this.qrMarginX = qrMarginX;
    }

    public int getQrImageSize() {
        return qrImageSize;
    }

    public void setQrImageSize(int qrImageSize) {
        this.qrImageSize = qrImageSize;
    }
}

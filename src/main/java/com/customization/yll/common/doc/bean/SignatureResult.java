package com.customization.yll.common.doc.bean;

import lombok.Data;
import java.util.List;

/**
 * @author 姚礼林
 * @desc 文件签名信息 - 对应契约锁API接口返回结构
 * @date 2025/8/3
 **/
@Data
public class SignatureResult {

    // ========== 接口返回的顶层字段 ==========

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 响应信息
     */
    private String message;

    /**
     * 验签结果码
     * 0：未被修改或文件无数字签名；1：已被修改
     */
    private Integer statusCode;

    /**
     * 验签结果
     */
    private String statusMsg;

    /**
     * 签署文档id
     */
    private String documentId;

    /**
     * 签名详情列表
     */
    private List<SignatureInfo> signatureInfos;

    /**
     * 签名详情信息
     */
    @Data
    public static class SignatureInfo {
        /**
         * 校验结果码
         * 0：校验成功 1：校验失败
         */
        private Integer code;

        /**
         * 校验结果
         */
        private String msg;

        /**
         * 签署完成后是否修改
         */
        private Boolean modified;

        /**
         * 签署方
         */
        private String signatory;

        /**
         * 签名时间
         */
        private String signTime;

        /**
         * 签名原因
         */
        private String signReason;

        /**
         * 签名规则（摘要算法）
         */
        private String hashAlg;

        /**
         * 颁发机构
         */
        private String organization;

        /**
         * 签名算法
         */
        private String strAlgName;

        /**
         * 签名后的摘要字符串
         */
        private String signedDiget;

        /**
         * 签名是否覆盖全文
         */
        private Boolean signatureCoversWholeDocument;

        /**
         * 是否有可见签名
         */
        private Boolean visibleSignature;

        /**
         * 是否有时间戳
         */
        private Boolean hasTimeStamp;

        /**
         * 时间戳内容
         */
        private String timeStamp;

        /**
         * 时间戳校验结果
         */
        private Boolean verifyTimestamp;

        /**
         * 证书
         */
        private String cert;

        /**
         * 证书引证
         */
        private List<String> certChain;

        /**
         * 证书序列号
         */
        private String certSerialNo;

        /**
         * 证书有效期开始时间
         */
        private String certDateFrom;

        /**
         * 证书有效期结束时间
         */
        private String certDateTo;

        /**
         * 加密算法
         */
        private String encryptionAlg;

        /**
         * 文档Id
         */
        private String documentId;

        /**
         * 签署平台
         */
        private String signPlatform;

        /**
         * 修改的表单列表
         */
        private List<String> annotItems;

        /**
         * 修改或添加的注释
         */
        private String fieldName;

        /**
         * 印章id
         * 签名时不返回
         */
        private String sealId;

        /**
         * 印章名称
         * 签名时不返回
         */
        private String sealName;

        /**
         * 个人签名id
         * 签章 和 法人章 时不返回
         */
        private String signatureId;

        /**
         * 个人签名名称
         * 签章 和 法人章 时不返回
         */
        private String signatureName;

        /**
         * 公钥
         */
        private String publicKey;
    }
}

package com.customization.yll.common.manager;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.customization.yll.common.util.CacheUtil;
import com.customization.yll.common.web.exception.ApiCallException;
import com.customization.yll.common.web.exception.ApiResultFailedException;
import org.apache.commons.lang.StringUtils;
import weaver.general.Util;
import weaver.integration.logging.Logger;
import weaver.integration.logging.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yaolilin
 * @desc 获取 ecology token，token 可用于访问需要进行认证的接口。<br>
 * 获取到的 token 会进行缓存，下次获取时会从缓存中获取token，token 缓存时间为 {@link #TEN_MINUTES}。调用注册接口返回的公钥和密钥（secret）
 * 也会进行缓存，缓存时间为 {@link #SEVEN_DAY_SECOND}，当调用获取 token 接口时，会从缓存中获取公钥和密钥。
 * @date 2024/7/5
 */
public class EcologyTokenManager {
    private static final String TOKEN_CACHE_KEY = "DEV_SERVER_TOKEN";
    private static final String TOKEN_TIME_KEY = "DEV_TOKEN_TIME";
    private static final int TEN_MINUTES = 60 * 10;
    public static final int SEVEN_DAY_SECOND = 60 * 60 * 24 * 7;
    private static final String SECRET_CACHE_KEY = "DEV_SERVER_SECRET";
    private static final String PUBLIC_KEY_CACHE_KEY = "DEV_SERVER_PUBLIC_KEY";
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final String appId;
    private final String serverAddress;

    /**
     * @param appId         ecology系统发放的授权许可证(appid)
     * @param serverAddress ecology系统地址
     */
    public EcologyTokenManager(String appId, String serverAddress) {
        this.appId = appId;
        this.serverAddress = serverAddress;
    }

    /**
     * 获取携带token的请求头，将该请求头的全部参数添加到接口中可进行认证
     * 获取到的token会存入到缓存中，而不是每次都获取新的token
     * @param userId 要调用接口的用户id,为OA中的用户id
     * @return 携带token的请求头, 如果获取token失败将会返回空的map
     */
    public Map<String, String> getHeaderWithToken(String userId) {
        return getHeaderWithToken(userId,false);
    }

    /**
     * 获取携带token的请求头，将该请求头的全部参数添加到接口中可进行认证
     *
     * @param userId 要调用接口的用户id,为OA中的用户id
     * @param isNewToken 是否重新获取token，而不是使用缓存中的token
     * @return 携带token的请求头, 如果获取token失败将会返回空的map
     */
    public Map<String, String> getHeaderWithToken(String userId,boolean isNewToken) {
        // 从缓存获取公钥，可能为空
        String publicKey = Util.null2String(CacheUtil.getCache(PUBLIC_KEY_CACHE_KEY));
        String token = getToken(isNewToken, publicKey);
        if (StrUtil.isBlank(token)) {
            return Collections.emptyMap();
        }
        // 重新获取公钥
        publicKey = Util.null2String(CacheUtil.getCache(PUBLIC_KEY_CACHE_KEY));
        Map<String, String> header = new HashMap<>(3);
        header.put("appid", appId);
        header.put("token", token);
        header.put("userid", encryptUserid(userId, publicKey));
        return header;
    }

    /**
     * 获取token，如果获取不到返回空字符串
     * 获取到的token会存入到缓存中，而不是每次都获取新的token
     *
     * @return token，如果获取失败返回空字符串
     */
    public String getToken() {
        return getToken(false);
    }

    /**
     * 获取token，如果获取不到返回空字符串
     * @param isNewToken 是否重新获取token，而不是使用缓存中的token
     * @param publicKey  RSA 公钥，也就是注册接口返回的公钥，如果为空则重新注册
     * @return token，如果获取失败返回空字符串
     */
    public String getToken(boolean isNewToken, String publicKey) {
        Object cacheValue = CacheUtil.getCache(TOKEN_CACHE_KEY);
        log.info("缓存中的token是否存在："+ (cacheValue != null));
        if (StrUtil.isNotEmpty(publicKey) && !isTokenOverDue() && !isNewToken) {
            log.info("获取缓存中的token");
            return (String) cacheValue;
        }
        log.info("获取新的token");
        String secret = Util.null2String(CacheUtil.getCache(SECRET_CACHE_KEY));
        String encryptSecret = getEncryptSecret(publicKey, secret);
        if (encryptSecret.isEmpty()) {
            return "";
        }
        try {
            String token = fetchToken(encryptSecret);
            CacheUtil.putCache(TOKEN_CACHE_KEY, token, TEN_MINUTES);
            CacheUtil.putCache(TOKEN_TIME_KEY, System.currentTimeMillis()+"", TEN_MINUTES);
            return token;
        } catch (ApiCallException | ApiResultFailedException e) {
            log.error("获取token失败", e);
            return "";
        }
    }

    private String getEncryptSecret(String publicKey, String secret) {
        // 如果为空,说明还未进行注册,调用注册接口进行注册认证与数据更新
        if (secret.isEmpty() || StrUtil.isEmpty(publicKey)) {
            log.info("密钥或公钥为空，进行注册");
            try {
                PublicKeyAndSecret registerResult = register(serverAddress);
                secret = registerResult.secret;
                publicKey = registerResult.publicKey;
                log.info("已获取 secret 和 spk");
            } catch (ApiCallException | ApiResultFailedException e) {
                log.error("注册许可证失败", e);
                return "";
            }
        }
        // 公钥加密
        RSA rsa = new RSA(null, publicKey);
        //对秘钥进行加密传输，防止篡改数据
        return rsa.encryptBase64(secret, CharsetUtil.CHARSET_UTF_8, KeyType.PublicKey);
    }

    /**
     * 判断token是否过期
     * @return 如果过期返回true
     */
    private boolean isTokenOverDue() {
        Object tokenCache = CacheUtil.getCache(TOKEN_CACHE_KEY);
        Object tokenTimeCache = CacheUtil.getCache(TOKEN_TIME_KEY);
        log.info("tokenTimeCache:"+Util.null2String(tokenTimeCache));
        boolean isRedis = CacheUtil.isRedis();
        log.info("是否为redis:"+isRedis);
        if (isRedis) {
            return tokenCache == null;
        }
        if (tokenCache == null || tokenTimeCache == null) {
            log.info("token缓存不存在或token时间缓存不存在");
            return true;
        }
        long tokenTime = Long.parseLong(Util.null2String(tokenTimeCache));
        long currentTime = System.currentTimeMillis();
        boolean isOverDue = currentTime - tokenTime > TEN_MINUTES * 1000;
        log.info("token是否超时："+isOverDue);
        return isOverDue;
    }

    /**
     * 调用ecology注册接口,根据appid进行注册,将返回服务端公钥和Secret信息
     */
    private PublicKeyAndSecret register(String address) {
        synchronized (EcologyTokenManager.class) {
            // 获取当前系统RSA加密的公钥
            RSA rsa = new RSA();
            String publicKey = rsa.getPublicKeyBase64();

            String result;
            try (HttpResponse response = HttpRequest.post(address + "/api/ec/dev/auth/regist")
                    .header("appid", appId)
                    .header("cpk", publicKey)
                    .timeout(10000)
                    .execute()) {
                result = response.body();
            } catch (Exception e) {
                throw new ApiCallException("许可证注册失败", e);
            }
            verifyRegisterResult(result);
            JSONObject resultJson = JSON.parseObject(result);
            //ECOLOGY返回的系统公钥
            String spk = resultJson.getString("spk");
            //ECOLOGY返回的系统密钥
            String secret = resultJson.getString("secrit");
            CacheUtil.putCache(PUBLIC_KEY_CACHE_KEY, StrUtil.nullToEmpty(spk),
                    SEVEN_DAY_SECOND);
            CacheUtil.putCache(SECRET_CACHE_KEY, StrUtil.nullToEmpty(secret),
                    SEVEN_DAY_SECOND);
            PublicKeyAndSecret publicKeyAndSecret = new PublicKeyAndSecret();
            publicKeyAndSecret.publicKey = spk;
            publicKeyAndSecret.secret = secret;
            return publicKeyAndSecret;
        }
    }

    private String encryptUserid(String userId, String publicKey) {
        //封装请求头参数
        RSA rsa = new RSA(null, publicKey);
        //对用户信息进行加密传输,暂仅支持传输OA用户ID
        return rsa.encryptBase64(userId, CharsetUtil.CHARSET_UTF_8, KeyType.PublicKey);
    }

    private String fetchToken(String encryptSecret) {
        String result;
        //调用ECOLOGY系统接口进行注册
        try (HttpResponse response = HttpRequest.post(serverAddress + "/api/ec/dev/auth/applytoken")
                .header("appid", appId)
                .header("secret", encryptSecret)
                .header("time", 60 * 30 + "")
                .execute()) {
            result = response.body();
        } catch (HttpException e) {
            throw new ApiCallException("获取token失败", e);
        }
        verifyTokenResult(result);
        JSONObject resultJson = JSON.parseObject(result);
        return resultJson.getString("token");
    }

    /**
     * 获取token，如果获取不到返回空字符串
     *
     * @param isNewToken 是否重新获取token，而不是使用缓存中的token
     * @return token，如果获取失败返回空字符串
     */
    public String getToken(boolean isNewToken) {
        String publicKey = Util.null2String(CacheUtil.getCache(PUBLIC_KEY_CACHE_KEY));
        return getToken(isNewToken, publicKey);
    }

    private void verifyTokenResult(String result) {
        if (StringUtils.isEmpty(result)) {
            throw new ApiCallException("获取token失败,返回结果为空");
        }
        JSONObject resultJson = JSON.parseObject(result);
        if (resultJson.getInteger("code") != 0) {
            throw new ApiResultFailedException("获取token失败，" + resultJson.getString("msg"));
        }
    }

    private void verifyRegisterResult(String result) {
        if (StringUtils.isEmpty(result)) {
            throw new ApiCallException("许可证注册失败,返回结果为空");
        }
        JSONObject resultJson = JSON.parseObject(result);
        if (resultJson.getInteger("code") != 0) {
            throw new ApiResultFailedException("许可证注册失败，" + resultJson.getString("errmsg"));
        }
    }

    private static class PublicKeyAndSecret {
        private String publicKey;
        private String secret;
    }
}

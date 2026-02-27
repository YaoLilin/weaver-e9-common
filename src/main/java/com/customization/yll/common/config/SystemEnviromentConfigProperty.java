package com.customization.yll.common.config;

import com.customization.yll.common.mode.util.ModeConfigUtil;
import lombok.Data;

/**
 * @author 姚礼林
 * @desc 系统环境配置，存储在[开发统一配置中心]建模中
 * @date 2026/2/2
 **/
@Data
public class SystemEnviromentConfigProperty {
    public static final String CONFIG_ID = "3f3b1d96-1aa6-47c9-b1be-9502e51ef5ce";
    private String appId;
    private String systemAddress;

    /**
     * 获取 OA 系统的许可证，可用于调用OA系统接口进行认证
     * @return OA 系统的许可证
     */
    public String getAppId() {
        if (this.appId == null) {
            this.appId = ModeConfigUtil.getPropValue(CONFIG_ID, "appId", false, true);
        }
        return appId;
    }

    /**
     * 获取系统访问地址
     */
    public String getSystemAddress() {
        if (this.systemAddress == null) {
            this.systemAddress = ModeConfigUtil.getPropValue(CONFIG_ID, "systemAddress",
                    false, true);
        }
        return systemAddress;
    }
}

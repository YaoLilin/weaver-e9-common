package com.customization.yll.common.mode;

import com.customization.yll.common.mode.handle.ModeActionExceptionHandler;
import com.customization.yll.common.mode.util.ModeConfigUtil;
import com.customization.yll.common.util.CacheUtil;
import weaver.conn.RecordSet;
import weaver.formmode.customjavacode.AbstractModeExpandJavaCodeNew;
import weaver.soa.workflow.request.RequestInfo;

import java.util.Collections;
import java.util.Map;

/**
 * @author 姚礼林
 * @desc 开发统一配置中心建模保存时，清除对应的配置缓存
 * @date 2025/4/8
 **/
public class CleanModeConfigCacheAction extends AbstractModeExpandJavaCodeNew {

    @Override
    public Map<String, String> doModeExpand(Map<String, Object> param) {
        try {
            RequestInfo requestInfo = (RequestInfo)param.get("RequestInfo");
            int billId = Integer.parseInt(requestInfo.getRequestid());
            RecordSet recordSet = new RecordSet();
            recordSet.executeQuery("select z.config_id,m.name from "+ ModeConfigUtil.TABLE_NAME + " z join "
                    + ModeConfigUtil.TABLE_NAME + "_dt1 m on m.mainid=z.id where m.mainid=?", billId);
            while (recordSet.next()) {
                String configId = recordSet.getString("config_id");
                String propName = recordSet.getString("name");
                CacheUtil.deleteCache(ModeConfigUtil.getCacheKey(configId,propName));
            }
        } catch (Exception e) {
            return ModeActionExceptionHandler.handle("清除缓存失败", e, this.getClass());
        }
        return Collections.emptyMap();
    }
}

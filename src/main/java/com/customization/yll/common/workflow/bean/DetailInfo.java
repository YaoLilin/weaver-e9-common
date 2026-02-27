package com.customization.yll.common.workflow.bean;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author 姚礼林
 * @desc 流程明细信息
 * @date 2025/7/7
 **/
public class DetailInfo implements Serializable {
    /**
     * 明细索引，不能为空，比如明细1索引就是0，明细2索引就是1
     */
    private Integer index;
    private List<Map<String ,String >> detailData;

    /**
     *
     * @param index 明细索引，不能为空，比如明细1索引就是0，明细2索引就是1
     * @param detailData 明细数据，一个元素标识一行，map key为字段名，value为字段值
     */
    public DetailInfo(@NotNull  Integer index, List<Map<String, String>> detailData) {
        this.index = index;
        this.detailData = detailData;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public List<Map<String, String>> getDetailData() {
        return detailData;
    }

    public void setDetailData(List<Map<String, String>> detailData) {
        this.detailData = detailData;
    }
}

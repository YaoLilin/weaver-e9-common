package com.customization.yll.common.bean;

/**
 * @author yaolilin
 * @desc 建模查询字段信息
 * @date 2024/9/14
 **/
public class SearchPageFieldInfo {
    private String showName;
    private String fieldName;
    private Integer fieldId;
    private boolean isShow;
    private String detailTable;

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Integer getFieldId() {
        return fieldId;
    }

    public void setFieldId(Integer fieldId) {
        this.fieldId = fieldId;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public String getDetailTable() {
        return detailTable;
    }

    public void setDetailTable(String detailTable) {
        this.detailTable = detailTable;
    }
}

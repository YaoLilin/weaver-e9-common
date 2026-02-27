package com.customization.yll.common.yao.base;

import lombok.Getter;
import org.junit.Before;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import weaver.soa.workflow.request.*;
import weaver.systeminfo.SystemEnv;
import weaver.workflow.request.RequestManager;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

/**
 * @author 姚礼林
 * @desc 流程测试类的初始化
 * @date 2024/5/23
 */
@PrepareForTest({RequestManager.class, SystemEnv.class})
@Getter
public class ActionTestSetUp {
    protected RequestInfo requestInfo;
    protected MainTableInfo mainTableInfo;
    protected DetailTableInfo detailTableInfo;
    protected DetailTable detailTable;
    protected RequestManager requestManager;

    @Before
    public void init() {
        requestInfo = Mockito.mock(RequestInfo.class);
        mainTableInfo = new MainTableInfo();
        detailTable = new DetailTable();
        detailTableInfo = Mockito.mock(DetailTableInfo.class);
        PowerMockito.mockStatic(SystemEnv.class);
        when(SystemEnv.getHtmlLabelName(anyInt(),anyInt())).thenReturn("a");
        requestManager = Mockito.mock(RequestManager.class);
        when(requestInfo.getRequestManager()).thenReturn(requestManager);
        when(requestInfo.getMainTableInfo()).thenReturn(mainTableInfo);
        when(requestInfo.getDetailTableInfo()).thenReturn(detailTableInfo);
        when(detailTableInfo.getDetailTable(anyInt())).thenReturn(detailTable);
        mainTableInfo.setProperty(new Property[]{new Property()});
        Row row = new Row();
        Cell cell = new Cell();
        row.setCell(new Cell[]{cell});
        detailTable.setRow(new Row[]{});
    }

    public Property createMainField(String fieldName, String fieldValue) {
        Property property = new Property();
        property.setName(fieldName);
        property.setValue(fieldValue);
        return property;
    }

    public Cell createDetailField(String fieldName, String fieldValue) {
        Cell cell = new Cell();
        cell.setName(fieldName);
        cell.setValue(fieldValue);
        return cell;
    }

    public void setMainField(Property[] fields) {
        mainTableInfo.setProperty(fields);
    }


}

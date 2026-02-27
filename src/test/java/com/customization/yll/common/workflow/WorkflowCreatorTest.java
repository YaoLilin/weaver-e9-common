package com.customization.yll.common.workflow;

import com.customization.yll.common.workflow.bean.CreateWorkflowResult;
import com.customization.yll.common.workflow.bean.DetailInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import weaver.general.GCONST;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author 姚礼林
 * @desc 创建流程测试
 * @date 2025/7/7
 **/
public class WorkflowCreatorTest {

    @Before
    public void setUp() throws Exception {
        GCONST.setRootPath("/Users/yaolilin/weaver/ecology/");
        GCONST.setServerName("ecology");
    }

    @Test
    public void create() {
        WorkflowCreator creator = new WorkflowCreator();
        Map<String, String> mainFields = new HashMap<>(5);
        mainFields.put("contract_number", "123");
        mainFields.put("xlk", "1");
        Map<String, String> row1 = new HashMap<>();
        row1.put("material_code", "1");
        row1.put("bh", "2");
        Map<String, String> row2 = new HashMap<>();
        row2.put("material_code", "3");
        row2.put("bh", "4");
        DetailInfo detail1 = new DetailInfo(0, Stream.of(row1,row2).collect(Collectors.toList()));
        Map<String, String> dt2Row1 = new HashMap<>();
        dt2Row1.put("bm", "1");
        DetailInfo detail2 = new DetailInfo(1, Stream.of(dt2Row1).collect(Collectors.toList()));

        CreateWorkflowResult result = creator.create(mainFields, Stream.of(detail1,detail2).collect(Collectors.toList()),
                55, null, 1, false);
        System.out.println(result);
        Assert.assertTrue(result.isSuccess());
    }

    @Test
    public void create_submit() {
        WorkflowCreator creator = new WorkflowCreator();
        Map<String, String> mainFields = new HashMap<>(5);
        mainFields.put("contract_number", "123");
        mainFields.put("xlk", "1");
        Map<String, String> row1 = new HashMap<>();
        row1.put("material_code", "1");
        row1.put("bh", "2");
        Map<String, String> row2 = new HashMap<>();
        row2.put("material_code", "3");
        row2.put("bh", "4");
        DetailInfo detailInfo = new DetailInfo(0, Stream.of(row1,row2).collect(Collectors.toList()));

        CreateWorkflowResult result = creator.create(mainFields, Stream.of(detailInfo).collect(Collectors.toList()),
                55, "测试", 1, true);
        System.out.println(result);
        Assert.assertTrue(result.isSuccess());
    }
}

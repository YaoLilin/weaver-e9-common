package com.customization.yll.common.manager;

import com.customization.yll.common.bean.FieldParamMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import weaver.conn.RecordSet;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 姚礼林
 * @desc TableFieldApiParamMapManager 测试
 * @date 2025/8/20
 **/
@ExtendWith(MockitoExtension.class)
class TableFieldApiParamMapManagerTest {
    private static final String CONTRACT_MODE_TABLE_NAME = "formtable_main_223";
    public static final String RECORD_TABLE_NAME = "uf_contract_ps_record";
    @Mock
    private RecordSet recordSet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Tag("测试生成查询语句是否正确")
    void buildQuerySql() {
        List<FieldParamMap> paramConfigs = getTestData();
        String querySql = getQurySql(paramConfigs, new TableFieldApiParamMapManager(recordSet));
        System.out.println(querySql);
    }

    private List<FieldParamMap> getTestData() {
        List<FieldParamMap> paramConfigs = new ArrayList<>();
        paramConfigs.add(new FieldParamMap("id", "id", "数据id"));
        paramConfigs.add(new FieldParamMap("htflx", "htflx", "合同分类-新"));
        paramConfigs.add(new FieldParamMap("yzdwlzryddr", "yzdwlzryddr", "业主单位流转人员单人"));
        paramConfigs.add(new FieldParamMap("htztsfbhlggfhqxsgsgxgt", "htztsfbhlggfhqxsgsgxgt", "合同主体是否包含柳钢股份或其下属公司(广西钢铁)"));
        paramConfigs.add(new FieldParamMap("slxs", "slxs", "税率（小数）"));
        paramConfigs.add(new FieldParamMap("xmyzdwwb", "xmyzdwwb", "项目业主单位文本"));
        paramConfigs.add(new FieldParamMap("xmzgdwwb", "xmzgdwwb", "项目主管单位文本"));
        paramConfigs.add(new FieldParamMap("xmzgdwzgry", "xmzgdwzgry", "项目主管单位主管人员"));
        paramConfigs.add(new FieldParamMap("xmzgdwzgrylxdh", "xmzgdwzgrylxdh", "项目主管单位主管人员联系电话"));
        paramConfigs.add(new FieldParamMap("htsplb", "htsplb", "合同审批类别"));
        paramConfigs.add(new FieldParamMap("htqx", "htqx", "合同期限"));
        paramConfigs.add(new FieldParamMap("xmzydw", "xmzydw", "项目专业单位"));
        paramConfigs.add(new FieldParamMap("bghje", "bghje", "变更后金额"));
        paramConfigs.add(new FieldParamMap("htjd", "htjd", "合同进度"));
        paramConfigs.add(new FieldParamMap("yl3", "yl3", "预留3"));
        paramConfigs.add(new FieldParamMap("sgysdj1", "sgysdj1", "施工用水单价1"));
        paramConfigs.add(new FieldParamMap("sgyddj1", "sgyddj1", "施工用电单价1"));
        paramConfigs.add(new FieldParamMap("zjdswl", "zjdswl", "工程总进度（实物量）"));
        paramConfigs.add(new FieldParamMap("xmyzdwllk", "xmyzdwllk", "项目业主单位（浏览框）"));
        paramConfigs.add(new FieldParamMap("jsxyfj", "jsxyfj", "技术协议附件"));
        paramConfigs.add(new FieldParamMap("zjqdfj", "zjqdfj", "造价清单附件"));
        paramConfigs.add(new FieldParamMap("qcrssbm", "qcrssbm", "起草人所属科室"));
        paramConfigs.add(new FieldParamMap("xmzgbmgzhjx", "xmzgbmgzhjx", "项目主管科室（固资或检修）"));
        paramConfigs.add(new FieldParamMap("ljykclpjehs", "ljykclpjehs", "累计已开材料票金额（含税）"));
        paramConfigs.add(new FieldParamMap("htjbrrlzy", "htjbrrlzy", "合同经办人（人力资源）"));
        paramConfigs.add(new FieldParamMap("htglsx", "htglsx", "合同管理属性"));
        paramConfigs.add(new FieldParamMap("htqdzt", "htqdzt", "合同状态（选项）"));
        paramConfigs.add(new FieldParamMap("htwd", "htwd", "合同文档"));
        paramConfigs.add(new FieldParamMap("gcgfxmfzr", "gcgfxmfzr", "工程供方项目负责人"));
        paramConfigs.add(new FieldParamMap("gcgfxmfzrdh", "gcgfxmfzrdh", "工程供方项目负责人电话"));
        paramConfigs.add(new FieldParamMap("sfcwjs", "sfcwjs", "是否财务结算"));
        paramConfigs.add(new FieldParamMap("sfjg", "sfjg", "是否交工"));
        paramConfigs.add(new FieldParamMap("sfspxtscht", "sfspxtscht", "是否审批系统生成合同"));
        paramConfigs.add(new FieldParamMap("fksm", "fksm", "付款说明"));
        paramConfigs.add(new FieldParamMap("kgrq", "kgrq", "开工日期"));
        paramConfigs.add(new FieldParamMap("jgys", "jgys", "交工验收日期"));
        paramConfigs.add(new FieldParamMap("yzdwid", "yzdwid", "项目业主单位"));
        paramConfigs.add(new FieldParamMap("zgbmid", "zgbmid", "主管部门id"));
        paramConfigs.add(new FieldParamMap("ssfl", "ssfl", "所属分类"));
        paramConfigs.add(new FieldParamMap("htjkcwhsks", "htjkcwhsks", "合同价款财务核算科室"));
        paramConfigs.add(new FieldParamMap("htfl", "htfl", "合同分类"));
        paramConfigs.add(new FieldParamMap("sfgsht", "sfgsht", "是否格式合同"));
        paramConfigs.add(new FieldParamMap("fkfs", "fkfs", "付款方式"));
        paramConfigs.add(new FieldParamMap("gclxll", "gclxll", "工程类型（浏览）"));
        paramConfigs.add(new FieldParamMap("sfhsxz", "sfhsxz", "是否含税（选择）"));
        paramConfigs.add(new FieldParamMap("htqcr", "htqcr", "合同起草人"));
        paramConfigs.add(new FieldParamMap("htqcrlxdh", "htqcrlxdh", "合同起草人联系电话"));
        paramConfigs.add(new FieldParamMap("ndll", "ndll", "年度（浏览）"));
        paramConfigs.add(new FieldParamMap("fddbr", "fddbr", "法定代表人（供方）"));
        paramConfigs.add(new FieldParamMap("ljsf", "ljsf", "实际支付金额合计"));
        paramConfigs.add(new FieldParamMap("zbbh", "zbbh", "招标编号"));
        paramConfigs.add(new FieldParamMap("bchtjehtjk", "bchtjehtjk", "本次实际合同金额（不含税）"));
        paramConfigs.add(new FieldParamMap("jsrq", "jsrq", "结算日期"));
        paramConfigs.add(new FieldParamMap("cwjsrq", "cwjsrq", "财务结算日期"));
        paramConfigs.add(new FieldParamMap("gcdz", "gcdz", "工程地址"));
        paramConfigs.add(new FieldParamMap("xjzzzfrlbl", "xjzzzfrlbl", "现金转账支付让利比例"));
        paramConfigs.add(new FieldParamMap("htfj", "htfj", "生效合同附件"));
        paramConfigs.add(new FieldParamMap("xmbm", "xmbm", "项目编码（SAP，如没有填无）"));
        paramConfigs.add(new FieldParamMap("BUSINESSID", "BUSINESSID", "中标编号"));
        paramConfigs.add(new FieldParamMap("STATUS", "STATUS", "合同状态"));
        paramConfigs.add(new FieldParamMap("SUPPLIERNAME", "SUPPLIERNAME", "供应商名称"));
        paramConfigs.add(new FieldParamMap("SUPPLIERCODE", "SUPPLIERCODE", "供应商编号"));
        paramConfigs.add(new FieldParamMap("CONTRACTID", "CONTRACTID", "合同编号"));
        paramConfigs.add(new FieldParamMap("SIGNADDRESS", "SIGNADDRESS", "签约地点"));
        paramConfigs.add(new FieldParamMap("SIGNDATE", "SIGNDATE", "签约时间"));
        paramConfigs.add(new FieldParamMap("STARTTIME", "STARTTIME", "合同起始日期"));
        paramConfigs.add(new FieldParamMap("ENDTIME", "ENDTIME", "合同截止日期"));
        paramConfigs.add(new FieldParamMap("CONTRACTTYPE", "CONTRACTTYPE", "合同类型"));
        paramConfigs.add(new FieldParamMap("CONTRACTNAME", "CONTRACTNAME", "合同名称"));
        paramConfigs.add(new FieldParamMap("UNITATTRIBUTE", "UNITATTRIBUTE", "公司代码"));
        paramConfigs.add(new FieldParamMap("COMPANYNAME", "COMPANYNAME", "公司名称"));
        paramConfigs.add(new FieldParamMap("TICKETRATE", "TICKETRATE", "税率"));
        paramConfigs.add(new FieldParamMap("PAYPLAN", "PAYPLAN", "结算方式"));
        paramConfigs.add(new FieldParamMap("HANDLEPERSON", "HANDLEPERSON", "经办人"));
        paramConfigs.add(new FieldParamMap("HANDLEPHONE", "HANDLEPHONE", "经办人联系电话"));
        paramConfigs.add(new FieldParamMap("ENGINETYPE", "ENGINETYPE", "工程类型"));
        paramConfigs.add(new FieldParamMap("COSTUNIT", "COSTUNIT", "成本单位"));
        paramConfigs.add(new FieldParamMap("OWNDEPT", "OWNDEPT", "项目业主单位（文本）"));
        paramConfigs.add(new FieldParamMap("MAINDEPT", "MAINDEPT", "合同承办单位/项目主管部门"));
        paramConfigs.add(new FieldParamMap("PRODEPT", "PRODEPT", "项目专业部门"));
        paramConfigs.add(new FieldParamMap("SUBDEPT", "SUBDEPT", "项目主管科室"));
        paramConfigs.add(new FieldParamMap("TOTAL", "TOTAL", "合同价款总金额（元）（不含税）"));
        paramConfigs.add(new FieldParamMap("YEAR", "YEAR", "年度"));
        paramConfigs.add(new FieldParamMap("PROJECTCODE", "PROJECTCODE", "项目编号*"));
        paramConfigs.add(new FieldParamMap("PROJECTNAME", "PROJECTNAME", "项目名称"));
        paramConfigs.add(new FieldParamMap("FREECONTRACTCODE", "FREECONTRACTCODE", "外部合同编号"));
        paramConfigs.add(new FieldParamMap("ACCOUNTBANK", "ACCOUNTBANK", "开户行"));
        paramConfigs.add(new FieldParamMap("ACCOUNTBANKNAME", "ACCOUNTBANKNAME", "开户行名称"));
        paramConfigs.add(new FieldParamMap("ACCOUNTBANKNO", "ACCOUNTBANKNO", "开户行号"));
        paramConfigs.add(new FieldParamMap("ELACCOUNTBANK", "ELACCOUNTBANK", "电子开户行"));
        paramConfigs.add(new FieldParamMap("ELACCOUNTBANKNAME", "ELACCOUNTBANKNAME", "电子开户行名称"));
        paramConfigs.add(new FieldParamMap("ELACCOUNTBANKNO", "ELACCOUNTBANKNO", "电子开户行号"));
        paramConfigs.add(new FieldParamMap("NMACCOUNTBANK", "NMACCOUNTBANK", "农民工开户行"));
        paramConfigs.add(new FieldParamMap("NMACCOUNTBANKNAME", "NMACCOUNTBANKNAME", "农民工开户行名称"));
        paramConfigs.add(new FieldParamMap("NMACCOUNTBANKNO", "NMACCOUNTBANKNO", "农民工开户行号"));
        paramConfigs.add(new FieldParamMap("NMPERSON", "NMPERSON", "工程供方委托代理人"));
        paramConfigs.add(new FieldParamMap("NMPERSONPHONE", "NMPERSONPHONE", "工程供方委托代理人电话"));
        paramConfigs.add(new FieldParamMap("DEADLINE", "DEADLINE", "工期"));
        paramConfigs.add(new FieldParamMap("zhmc", "zhmc", "账户名称"));
        paramConfigs.add(new FieldParamMap("zh", "zh", "账号"));
        paramConfigs.add(new FieldParamMap("dzzhmc", "dzzhmc", "电子账户名称"));
        paramConfigs.add(new FieldParamMap("dzzhh", "dzzhh", "电子账户号"));
        paramConfigs.add(new FieldParamMap("nmgzhmc", "nmgzhmc", "农民工账户名称"));
        paramConfigs.add(new FieldParamMap("nmgzh", "nmgzh", "农民工账号"));
        paramConfigs.add(new FieldParamMap("khyx", "khyx", "开户银行"));
        paramConfigs.add(new FieldParamMap("nmgkhyx", "nmgkhyx", "农民工开户银行"));
        paramConfigs.add(new FieldParamMap("dzkhyx", "dzkhyx", "电子开户银行"));
        paramConfigs.add(new FieldParamMap("bchtjehs", "bchtjehs", "本次实际合同金额（含税）"));
        paramConfigs.add(new FieldParamMap("gsmcjfx", "gsmcjfx", "公司名称(甲方)x"));
        paramConfigs.add(new FieldParamMap("gsmcjfwbx", "gsmcjfwbx", "公司名称(甲方文本)x"));
        paramConfigs.add(new FieldParamMap("dwdzjfx", "dwdzjfx", "单位地址(甲方)x"));
        paramConfigs.add(new FieldParamMap("tyshxydmjfx", "tyshxydmjfx", "统一社会信用代码(甲方)x"));
        paramConfigs.add(new FieldParamMap("fddbrjfx", "fddbrjfx", "法定代表人(甲方)x"));
        paramConfigs.add(new FieldParamMap("frlxfsjfx", "frlxfsjfx", "法人联系方式(甲方)x"));
        paramConfigs.add(new FieldParamMap("wtdlrjfx", "wtdlrjfx", "委托代理人(甲方)x"));
        paramConfigs.add(new FieldParamMap("dlrlxfsjfx", "dlrlxfsjfx", "代理人联系方式(甲方)x"));
        paramConfigs.add(new FieldParamMap("yxzhmcjfx", "yxzhmcjfx", "银行账户名称(甲方)x"));
        paramConfigs.add(new FieldParamMap("lxhjfx", "lxhjfx", "联行号(甲方)x"));
        paramConfigs.add(new FieldParamMap("khxmcjfx", "khxmcjfx", "开户行名称(甲方)x"));
        paramConfigs.add(new FieldParamMap("khxzhjfx", "khxzhjfx", "开户行账号(甲方)x"));
        paramConfigs.add(new FieldParamMap("gsmcyfx", "gsmcyfx", "公司名称(乙方)x"));
        paramConfigs.add(new FieldParamMap("gsmcyfwbx", "gsmcyfwbx", "公司名称(乙方文本)x"));
        paramConfigs.add(new FieldParamMap("dwdzyfx", "dwdzyfx", "单位地址(乙方)x"));
        paramConfigs.add(new FieldParamMap("tyshxydmyfx", "tyshxydmyfx", "统一社会信用代码(乙方)x"));
        paramConfigs.add(new FieldParamMap("fddbryfx", "fddbryfx", "法定代表人(乙方)x"));
        paramConfigs.add(new FieldParamMap("frlxfsyfx", "frlxfsyfx", "法人联系方式(乙方)x"));
        paramConfigs.add(new FieldParamMap("wtdlryfx", "wtdlryfx", "委托代理人(乙方)x"));
        paramConfigs.add(new FieldParamMap("dlrlxfsyfx", "dlrlxfsyfx", "代理人联系方式(乙方)x"));
        paramConfigs.add(new FieldParamMap("yxzhmcyfx", "yxzhmcyfx", "银行账户名称(乙方)x"));
        paramConfigs.add(new FieldParamMap("lxhyfx", "lxhyfx", "联行号(乙方)x"));
        paramConfigs.add(new FieldParamMap("khxmcyfx", "khxmcyfx", "开户行名称(乙方)x"));
        paramConfigs.add(new FieldParamMap("khxzhyfx", "khxzhyfx", "开户行账号(乙方)x"));
        paramConfigs.add(new FieldParamMap("gsmcbfx", "gsmcbfx", "公司名称(丙方)x"));
        paramConfigs.add(new FieldParamMap("dwdzbfx", "dwdzbfx", "单位地址(丙方)x"));
        paramConfigs.add(new FieldParamMap("tyshxydmbfx", "tyshxydmbfx", "统一社会信用代码(丙方)x"));
        paramConfigs.add(new FieldParamMap("fddbrbfx", "fddbrbfx", "法定代表人(丙方)x"));
        paramConfigs.add(new FieldParamMap("yxzhmcbfx", "yxzhmcbfx", "银行账户名称(丙方)x"));
        paramConfigs.add(new FieldParamMap("lxhbfx", "lxhbfx", "联行号(丙方)x"));
        paramConfigs.add(new FieldParamMap("khxmcbfx", "khxmcbfx", "开户行名称(丙方)x"));
        paramConfigs.add(new FieldParamMap("khxzhbfx", "khxzhbfx", "开户行账户(丙方)x"));
        paramConfigs.add(new FieldParamMap("gsmcbfwbx", "gsmcbfwbx", "公司名称(丙方文本)x"));
        paramConfigs.add(new FieldParamMap("sqr", "sqr", "申请人"));
        paramConfigs.add(new FieldParamMap("sqbm", "sqbm", "申请部门"));
        paramConfigs.add(new FieldParamMap("sqfb", "sqfb", "申请分部"));
        paramConfigs.add(new FieldParamMap("dwsx", "dwsx", "单位属性"));
        paramConfigs.add(new FieldParamMap("cgfs", "cgfs", "采购方式"));
        paramConfigs.add(new FieldParamMap("sfgsht1", "sfgsht1", "是否格式合同"));
        paramConfigs.add(new FieldParamMap("dhjfx", "dhjfx", "电话(甲方)x"));
        paramConfigs.add(new FieldParamMap("czjfx", "czjfx", "传真(甲方)x"));
        paramConfigs.add(new FieldParamMap("dzyxjfx", "dzyxjfx", "电子邮箱(甲方)x"));
        paramConfigs.add(new FieldParamMap("dhyfx", "dhyfx", "电话(乙方)x"));
        paramConfigs.add(new FieldParamMap("czyfx", "czyfx", "传真(乙方)x"));
        paramConfigs.add(new FieldParamMap("dzyxyfx", "dzyxyfx", "电子邮箱(乙方)x"));
        paramConfigs.add(new FieldParamMap("zbje", "zbje", "中标金额"));
        paramConfigs.add(new FieldParamMap("xmbh", "xmbh", "项目编号"));
        paramConfigs.add(new FieldParamMap("bankAccountNameOfGyl", "bankAccountNameOfGyl", "银行账户名称（供应链）"));
        paramConfigs.add(new FieldParamMap("zhmcgyl", "zhmcgyl", "账户名称（供应链）"));
        paramConfigs.add(new FieldParamMap("bankNameOfGyl", "bankNameOfGyl", "开户行名称（供应链）"));
        paramConfigs.add(new FieldParamMap("InterBankNumberOfGyl", "InterBankNumberOfGyl", "联行号（供应链）"));
        paramConfigs.add(new FieldParamMap("bankAccountNumberOfGyl", "bankAccountNumberOfGyl", "开户行账号（供应链）"));
        paramConfigs.add(new FieldParamMap("zybmgz", "zybmgz", "专业科室（固资）"));
        paramConfigs.add(new FieldParamMap("htlx", "htlx", "合同类型代码选项（用于供方评价）"));
        paramConfigs.add(new FieldParamMap("htlxwb", "htlxwb", "合同类型文本（用于供方评价）"));
        paramConfigs.add(new FieldParamMap("htlxdm", "htlxdm", "合同类型代码（用于供方评价）"));
        paramConfigs.add(new FieldParamMap("zgbmbm", "zgbmbm", "主管科室编码（用于供方评价）"));
        paramConfigs.add(new FieldParamMap("qcrgh", "qcrgh", "起草人工号（用于供方评价）"));
        paramConfigs.add(new FieldParamMap("qcrxm", "qcrxm", "起草人姓名（用于供方评价）"));
        paramConfigs.add(new FieldParamMap("qcrdwbm", "qcrdwbm", "起草人单位编码（用于供方评价）"));
        paramConfigs.add(new FieldParamMap("qcrdwwb", "qcrdwwb", "起草人单位文本（用于供方评价）"));
        paramConfigs.add(new FieldParamMap("yzdwgg", "yzdwgg", "业主单位（广钢）"));
        paramConfigs.add(new FieldParamMap("htcbdwxmzgbmgg", "htcbdwxmzgbmgg", "合同承办单位/项目主管部门（广钢）"));
        paramConfigs.add(new FieldParamMap("zgbmwbyygfpj", "zgbmwbyygfpj", "主管科室文本（用于供方评价）"));
        paramConfigs.add(new FieldParamMap("supervisorName", "supervisorName", "主管人员姓名（用于供方评价）"));
        paramConfigs.add(new FieldParamMap("supervisorCode", "supervisorCode", "主管人员工号（用于供方评价）"));
        paramConfigs.add(new FieldParamMap("yzdwbmyygfpj", "yzdwbmyygfpj", "业主单位编码（用于供方评价）"));
        paramConfigs.add(new FieldParamMap("yzdwmcyygfpj", "yzdwmcyygfpj", "业主单位名称（用于供方评价）"));
        paramConfigs.add(new FieldParamMap("zgbmbmyygfpj", "zgbmbmyygfpj", "主管部门编码（用于供方评价）"));
        paramConfigs.add(new FieldParamMap("qcbmbmyygfpj", "qcbmbmyygfpj", "起草部门编码（用于供方评价）"));
        paramConfigs.add(new FieldParamMap("cjtzs", "cjtzs", "成交通知书"));
        paramConfigs.add(new FieldParamMap("htqdyj", "htqdyj", "合同签订依据"));
        paramConfigs.add(new FieldParamMap("htdgb", "htdgb", "合同定稿版"));
        paramConfigs.add(new FieldParamMap("sxht", "sxht", "生效合同"));
        paramConfigs.add(new FieldParamMap("sapyfkbs", "sapyfkbs", "SAP应付款笔数"));
        paramConfigs.add(new FieldParamMap("fkxxxz", "fkxxxz", "付款形象性质"));
        paramConfigs.add(new FieldParamMap("zxsapyfkhs", "zxsapyfkhs", "最新已支付合同金额（含税）"));
        paramConfigs.add(new FieldParamMap("sapyfkljhs", "sapyfkljhs", "累计已支付合同金额（含税）"));

        return paramConfigs;
    }

    private String  getQurySql(List<FieldParamMap> paramConfigs,
                                                    TableFieldApiParamMapManager paramMapManager) {
        // 查询合同建模主表，只查询没有推送过或者之前推送过后来数据有更新的，并且流程来源为10-1
        String querySql = paramMapManager.buildQuerySql(paramConfigs, CONTRACT_MODE_TABLE_NAME, "z");
        querySql += " WHERE EXISTS(SELECT r.id FROM " + RECORD_TABLE_NAME +
                " r WHERE r.contract_id = z.id AND r.pushed_time < z.modedatamodifydatetime) OR " +
                " NOT EXISTS(SELECT r.id FROM " + RECORD_TABLE_NAME +
                " r WHERE r.contract_id = z.id) AND z.lcly=5";
        return querySql;
    }
}

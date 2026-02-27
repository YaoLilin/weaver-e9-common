package com.customization.yll.common.util;

import com.customization.yll.common.enu.LanguageType;
import org.junit.Before;
import org.junit.Test;
import weaver.conn.RecordSet;
import weaver.general.GCONST;

import static org.junit.Assert.*;

public class MultiLanguageUtilTest {
    private RecordSet recordSet;

    @Before
    public void setUp() throws Exception {
        GCONST.setRootPath("/Users/yaolilin/weaver/ecology/");
        GCONST.setServerName("ecology");
        recordSet = new RecordSet();
    }

    @Test
    public void getMultiLanguage() {
        String multiLanguage = MultiLanguageUtil.getMultiLanguage("韦利东", "Lidong Wei", recordSet);
        System.out.println("多语言姓名："+multiLanguage);
        assert !multiLanguage.isEmpty();
        boolean  result = recordSet.executeUpdate("update hrmresource set lastname=? where id = 22", multiLanguage);
        assert result;
    }

    @Test
    public void getMultiLanguage_withNullEnName() {
        String multiLanguage = MultiLanguageUtil.getMultiLanguage("韦利东", null, recordSet);
        assert "韦利东".equals(multiLanguage);
        System.out.println("多语言姓名："+multiLanguage);
        boolean result = recordSet.executeUpdate("update hrmresource set lastname=? where id = 22", multiLanguage);
        assert result;
    }

    @Test
    public void getMultiLanguage_withEmptyEnName() {
        String multiLanguage = MultiLanguageUtil.getMultiLanguage("韦利东", "", recordSet);
        assert "韦利东".equals(multiLanguage);
        System.out.println("多语言姓名："+multiLanguage);
        boolean result = recordSet.executeUpdate("update hrmresource set lastname=? where id = 22", multiLanguage);
        assert result;
    }

    @Test
    public void getMultiLanguage_withEmptyCnName() {
        String multiLanguage = MultiLanguageUtil.getMultiLanguage("", "Lidong", recordSet);
        assert multiLanguage.isEmpty();
        multiLanguage = MultiLanguageUtil.getMultiLanguage(null, "Lidong", recordSet);
        assert multiLanguage.isEmpty();
    }

    @Test
    public void analyzeMultiLanguageTextTest() {
        String result = MultiLanguageUtil.analyzeMultiLanguageText("~`~`7 韦利东`~`8 wld`~`9 韋利東`~`~", LanguageType.CN,
                recordSet);
        assertEquals("韦利东",result);
    }

    @Test
    public void analyzeMultiLanguageTextTest_withAnyChar() {
        String result = MultiLanguageUtil.analyzeMultiLanguageText("~`~`7  882jj.._`~`8 wld`~`9 韋利東`~`~", LanguageType.CN,
                recordSet);
        assertEquals(" 882jj.._",result);
    }

    @Test
    public void analyzeMultiLanguageTextTest_withNotMultiLanguage() {
        String result = MultiLanguageUtil.analyzeMultiLanguageText("韦利东", LanguageType.CN,
                recordSet);
        assertEquals("韦利东",result);
    }
}
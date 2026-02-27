package com.customization.yll.common.util;

import com.engine.kq.entity.TimeScopeEntity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import weaver.general.GCONST;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * @author yaolilin
 * @desc 考勤时间工具类测试
 * @date 2025/2/13
 **/
public class WorkTimeUtilTest {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Before
    public void setUp() throws Exception {
        GCONST.setRootPath("/Users/yaolilin/weaver/ecology/");
        GCONST.setServerName("ecology");
    }

    /**
     * 测试是否能获取下个工作日日期，请配置好考勤时间
     */
    @Test
    public void getNextWorkDate() {
        String nextWorkDate = WorkTimeUtil.getNextWorkDate(LocalDate.now(), "89");
        System.out.println("nextWorkDate:"+nextWorkDate);
        Assert.assertNotNull(nextWorkDate);
    }

    @Test
    public void getNextWorkDate_withSysAdmin() {
        String nextWorkDate = WorkTimeUtil.getNextWorkDate(LocalDate.now(), "1");
        System.out.println("nextWorkDate:"+nextWorkDate);
        Assert.assertNull(nextWorkDate);
    }

    @Test
    public void isTodayWork() {
        boolean todayWork = WorkTimeUtil.isTodayWork("2025-02-13", "89");
        Assert.assertTrue(todayWork);
    }

    @Test
    public void getWorkTimeList() {
        List<TimeScopeEntity> workTimeList = WorkTimeUtil.getWorkTimeList("2025-02-13", "89");
        System.out.println(workTimeList);
        Assert.assertFalse(workTimeList.isEmpty());
    }

    @Test
    public void getBeginWorkTime() {
        Optional<LocalDateTime> beginWorkTime = WorkTimeUtil.getBeginWorkTime("2025-02-26", "89");
        Assert.assertTrue(beginWorkTime.isPresent());
        System.out.println("上班时间："+beginWorkTime.get().format(DATE_TIME_FORMATTER));
    }

    @Test
    public void getOffWorkTime() {
        Optional<LocalDateTime> offWorkTime = WorkTimeUtil.getOffWorkTime("2025-02-13", "89");
        Assert.assertTrue(offWorkTime.isPresent());
        System.out.println("下班时间："+offWorkTime.get().format(DATE_TIME_FORMATTER));
    }

    @Test
    public void isInWorkTime() {
        boolean inWorkTime = WorkTimeUtil.isInWorkTime(
                LocalDateTime.parse("2025-02-13 13:00:00", DATE_TIME_FORMATTER), "89");
        Assert.assertTrue(inWorkTime);
        inWorkTime = WorkTimeUtil.isInWorkTime(
                LocalDateTime.parse("2025-02-13 19:00:00", DATE_TIME_FORMATTER), "89");
        Assert.assertFalse(inWorkTime);
    }

}

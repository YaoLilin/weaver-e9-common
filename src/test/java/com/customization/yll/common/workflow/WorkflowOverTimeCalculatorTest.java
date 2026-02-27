package com.customization.yll.common.workflow;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import weaver.general.GCONST;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author 姚礼林
 * @desc 流程耗时计算测试
 * @date 2025/4/14
 **/
public class WorkflowOverTimeCalculatorTest {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Before
    public void setUp() throws Exception {
        GCONST.setRootPath("/Users/yaolilin/weaver/ecology/");
        GCONST.setServerName("ecology");
    }

    /**
     * 测试流程耗时，从流程接收时间到现在时间
     */
    @Test
    public void getOverTimeSeconds() {
        WorkflowOverTimeCalculator timeCalculator = new WorkflowOverTimeCalculator();
        int overTimeSeconds = timeCalculator.getOverTimeSeconds("2025-04-11", "09:00:00", 89);
        System.out.println("overTimeSeconds:"+overTimeSeconds);
        Assert.assertTrue(overTimeSeconds > 39600);
    }

    /**
     * 测试流程耗时，从流程接收时间到指定时间，跨1天
     */
    @Test
    public void testGetOverTimeSeconds() {
        WorkflowOverTimeCalculator timeCalculator = new WorkflowOverTimeCalculator();
        LocalDateTime now = LocalDateTime.parse("2025-04-14 10:30:00", DATE_TIME_FORMATTER);
        int overTimeSeconds = timeCalculator.getOverTimeSeconds("2025-04-11", "09:00:00",
                now,89);
        System.out.println("overTimeSeconds:"+overTimeSeconds);
        Assert.assertEquals(39600, overTimeSeconds);
    }

    /**
     * 测试流程耗时，从流程接收时间到指定时间，跨2天
     */
    @Test
    public void testGetOverTimeSeconds_twoDays() {
        WorkflowOverTimeCalculator timeCalculator = new WorkflowOverTimeCalculator();
        LocalDateTime now = LocalDateTime.parse("2025-04-14 10:30:00", DATE_TIME_FORMATTER);
        int overTimeSeconds = timeCalculator.getOverTimeSeconds("2025-04-10", "09:00:00",
                now,89);
        System.out.println("overTimeSeconds:"+overTimeSeconds);
        Assert.assertEquals(73800, overTimeSeconds);
    }

    /**
     * 测试流程耗时，接收时间是昨天下午，截止时间是今天早上
     */
    @Test
    public void testGetOverTimeSeconds2() {
        WorkflowOverTimeCalculator timeCalculator = new WorkflowOverTimeCalculator();
        LocalDateTime endTime = LocalDateTime.parse("2025-04-16 09:52:00", DATE_TIME_FORMATTER);
        int overTimeSeconds = timeCalculator.getOverTimeSeconds("2025-04-15", "17:36:00",
                endTime,90);
        System.out.println("overTimeSeconds:"+overTimeSeconds);
        Assert.assertEquals(6360, overTimeSeconds);
    }

    /**
     * 测试流程耗时，从流程接收时间到指定时间，流程接收时间为下班后,将不计算当天时间
     */
    @Test
    public void testGetOverTimeSeconds_withOffWorkTime() {
        WorkflowOverTimeCalculator timeCalculator = new WorkflowOverTimeCalculator();
        LocalDateTime now = LocalDateTime.parse("2025-04-14 10:30:00", DATE_TIME_FORMATTER);
        int overTimeSeconds = timeCalculator.getOverTimeSeconds("2025-04-11", "19:00:00",
                now,89);
        System.out.println("overTimeSeconds:"+overTimeSeconds);
        Assert.assertEquals(7200, overTimeSeconds);
    }

    /**
     * 测试流程耗时，从流程接收时间到指定时间，流程接收时间当天不上班，将从下个工作日开始计算
     */
    @Test
    public void testGetOverTimeSeconds_withReceiveTimeNotWork() {
        WorkflowOverTimeCalculator timeCalculator = new WorkflowOverTimeCalculator();
        LocalDateTime now = LocalDateTime.parse("2025-04-14 10:30:00", DATE_TIME_FORMATTER);
        int overTimeSeconds = timeCalculator.getOverTimeSeconds("2025-04-12", "10:00:00",
                now,89);
        System.out.println("overTimeSeconds:"+overTimeSeconds);
        Assert.assertEquals(7200, overTimeSeconds);
    }

    /**
     * 测试流程耗时，流程接收时间和截止时间都为当天
     */
    @Test
    public void testGetOverTimeSeconds_withToday() {
        WorkflowOverTimeCalculator timeCalculator = new WorkflowOverTimeCalculator();
        LocalDateTime endTime = LocalDateTime.parse("2025-04-11 12:00:00", DATE_TIME_FORMATTER);
        int overTimeSeconds = timeCalculator.getOverTimeSeconds("2025-04-11", "09:00:00",
                endTime,89);
        System.out.println("overTimeSeconds:"+overTimeSeconds);
        Assert.assertEquals(10800, overTimeSeconds);
    }

    /**
     * 测试流程耗时，流程接收时间和截止时间都为当天，流程接收时间在上班前
     */
    @Test
    public void testGetOverTimeSeconds_withToday_beforeWorkTime() {
        WorkflowOverTimeCalculator timeCalculator = new WorkflowOverTimeCalculator();
        LocalDateTime endTime = LocalDateTime.parse("2025-04-11 12:00:00", DATE_TIME_FORMATTER);
        int overTimeSeconds = timeCalculator.getOverTimeSeconds("2025-04-11", "07:00:00",
                endTime,89);
        System.out.println("overTimeSeconds:"+overTimeSeconds);
        Assert.assertEquals(12600, overTimeSeconds);
    }

    /**
     * 测试流程耗时，流程接收时间和截止时间都为当天，截止时间在下班后
     */
    @Test
    public void testGetOverTimeSeconds_withToday_endTimeAfterOffWorkTime() {
        WorkflowOverTimeCalculator timeCalculator = new WorkflowOverTimeCalculator();
        LocalDateTime endTime = LocalDateTime.parse("2025-04-11 19:00:00", DATE_TIME_FORMATTER);
        int overTimeSeconds = timeCalculator.getOverTimeSeconds("2025-04-11", "15:00:00",
                endTime,89);
        System.out.println("overTimeSeconds:"+overTimeSeconds);
        Assert.assertEquals(10800, overTimeSeconds);
    }

    /**
     * 测试流程耗时，流程接收时间和截止时间都为当天，流程接收时间和截止时间都在下班后，将不计算耗时
     */
    @Test
    public void testGetOverTimeSeconds_withToday_afterOffWorkTime() {
        WorkflowOverTimeCalculator timeCalculator = new WorkflowOverTimeCalculator();
        LocalDateTime endTime = LocalDateTime.parse("2025-04-11 20:00:00", DATE_TIME_FORMATTER);
        int overTimeSeconds = timeCalculator.getOverTimeSeconds("2025-04-11", "19:00:00",
                endTime,89);
        System.out.println("overTimeSeconds:"+overTimeSeconds);
        Assert.assertEquals(0, overTimeSeconds);
    }

    /**
     * 测试流程耗时，流程接收时间和截止时间都为当天，截止时间在流程接收时间之前，不计算耗时
     */
    @Test
    public void testGetOverTimeSeconds_withToday_endTimeBeforeStartTime() {
        WorkflowOverTimeCalculator timeCalculator = new WorkflowOverTimeCalculator();
        LocalDateTime endTime = LocalDateTime.parse("2025-04-11 10:00:00", DATE_TIME_FORMATTER);
        int overTimeSeconds = timeCalculator.getOverTimeSeconds("2025-04-11", "11:00:00",
                endTime,89);
        System.out.println("overTimeSeconds:"+overTimeSeconds);
        Assert.assertEquals(0, overTimeSeconds);
    }

}

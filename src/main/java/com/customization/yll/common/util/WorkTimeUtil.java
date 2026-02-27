package com.customization.yll.common.util;

import cn.hutool.core.collection.CollUtil;
import com.engine.kq.biz.KQWorkTime;
import com.engine.kq.entity.TimeScopeEntity;
import com.engine.kq.entity.WorkTimeEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author yaolilin
 * @desc 考勤工具类
 * @date 2024/12/31
 **/
public class WorkTimeUtil {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final KQWorkTime KQ_WORK_TIME = new KQWorkTime();

    private WorkTimeUtil() {

    }

    /**
     * 当天是否上班
     *
     * @param date   日期
     * @param userId 用户id
     * @return 如果上班返回true
     */
    public static boolean isTodayWork(String date, String userId) {
        WorkTimeEntity workTimeEntity = KQ_WORK_TIME.getWorkTime(userId, date);
        if (workTimeEntity != null) {
            List<TimeScopeEntity> signTime = workTimeEntity.getSignTime();
            return CollUtil.isNotEmpty(signTime);
        }
        return false;
    }

    /**
     * 获取当天的班次信息，如果没有班次则返回空列表,列表已根据时间排序
     *
     * @param date   日期
     * @param userId 用户id
     * @return 当天的班次信息
     */
    public static List<TimeScopeEntity> getWorkTimeList(String date, String userId) {
        WorkTimeEntity workTimeEntity = KQ_WORK_TIME.getWorkTime(userId, date);
        if (workTimeEntity == null) {
            return new ArrayList<>();
        }
        List<TimeScopeEntity> signTime = workTimeEntity.getWorkTime();
        if (CollUtil.isEmpty(signTime)) {
            return new ArrayList<>();
        }
        sortWorkTimeEntity(signTime);
        return signTime;
    }

    /**
     * 获取指定日期的上班时间
     *
     * @param workDate 日期
     * @param userId   用户id
     * @return 上班时间
     */
    public static Optional<LocalDateTime> getBeginWorkTime(String workDate, String userId) {
        if (!isTodayWork(workDate, userId)) {
            return Optional.empty();
        }
        List<TimeScopeEntity> workTimeList = WorkTimeUtil.getWorkTimeList(workDate, userId);
        TimeScopeEntity workTime = workTimeList.get(0);
        LocalDate dateObj = LocalDate.parse(workDate, DATE_FORMATTER);
        LocalDateTime dateTimeObj = setTime(workTime.getBeginTime(), dateObj);
        return Optional.of(dateTimeObj);
    }

    /**
     * 获取指定日期的下班时间
     * @param workDate 日期
     * @param userId 用户id
     * @return 下班时间
     */
    public static Optional<LocalDateTime> getOffWorkTime(String workDate, String userId) {
        if (!isTodayWork(workDate, userId)) {
            return Optional.empty();
        }
        List<TimeScopeEntity> workTimeList = WorkTimeUtil.getWorkTimeList(workDate, userId);
        TimeScopeEntity workTimeScope = workTimeList.get(workTimeList.size() - 1);
        LocalDate dateObj = LocalDate.parse(workDate, DATE_FORMATTER);
        LocalDateTime dateTimeObj = setTime(workTimeScope.getEndTime(), dateObj);
        return Optional.of(dateTimeObj);
    }

    /**
     * 从指定日期开始，获取下一个工作日的日期，如果指定日期为工作日则返回该日期，查询超过30天没有获取到工作日则返回null
     *
     * @param startDate 指定日期，将从此日期往后查询到第一个工作日
     * @param userId 用户id
     * @return 工作日日期
     */
    @Nullable
    public static String getNextWorkDate(LocalDate startDate, String userId) {
        LocalDate date = LocalDate.from(startDate);
        int days = 30;
        int count = 0;
        while (count++ <= days) {
            if (isTodayWork(DATE_FORMATTER.format(date), userId)) {
                return DATE_FORMATTER.format(date);
            }
            date = date.plusDays(1);
        }
        return null;
    }

    /**
     * 判断当前时间是否处于上班时间
     * @param nowTime 当前时间
     * @param userId 用户id
     * @return 是否处于上班时间
     */
    public static boolean isInWorkTime(LocalDateTime nowTime, String userId) {
        List<TimeScopeEntity> workTimeList = getWorkTimeList(nowTime.toLocalDate().format(DATE_FORMATTER),
                userId);
        if (CollUtil.isEmpty(workTimeList)) {
            return false;
        }
        String beginWorkTime = workTimeList.get(0).getBeginTime();
        String endWorkTime = workTimeList.get(workTimeList.size() - 1).getEndTime();
        LocalTime beginWorkTimeObj = LocalTime.parse(beginWorkTime, TIME_FORMATTER);
        LocalTime endWorkTimeObj = LocalTime.parse(endWorkTime, TIME_FORMATTER);
        return !nowTime.toLocalTime().isBefore(beginWorkTimeObj) && !nowTime.toLocalTime().isAfter(endWorkTimeObj);
    }

    private static void sortWorkTimeEntity(List<TimeScopeEntity> signTime) {
        signTime.sort((o1, o2) -> {
            if (o1.getBeginTime().equals(o2.getBeginTime())) {
                return 0;
            }
            return LocalTime.parse(o1.getBeginTime(), TIME_FORMATTER)
                    .isBefore(LocalTime.parse(o2.getBeginTime(), TIME_FORMATTER)) ? -1 : 1;
        });
    }

    @NotNull
    private static LocalDateTime setTime(String time, LocalDate date) {
        LocalTime localTime = LocalTime.parse(time, TIME_FORMATTER);
        return LocalDateTime.of(date, localTime);
    }
}

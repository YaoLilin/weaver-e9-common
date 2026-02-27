package com.customization.yll.common.workflow;

import com.customization.yll.common.exception.ConfigurationException;
import com.customization.yll.common.util.WorkTimeUtil;
import com.engine.kq.entity.TimeScopeEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import weaver.integration.logging.Logger;
import weaver.integration.logging.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * @author yaolilin
 * @desc 流程耗时计算，只计算工作时间，非工作时间不纳入超时计算中
 * @date 2024/12/30
 **/
public class WorkflowOverTimeCalculator {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 获取流程接收时间到当前时间的未处理时间秒数
     *
     * @param receiveDate 流程接收日期
     * @param receiveTime 流程接收时间，需要到秒，比如 10:00:00
     * @param userId      用户id
     * @return 流程未处理时间秒数
     */
    public int getOverTimeSeconds(String receiveDate, String receiveTime, int userId) {
        return getOverTimeSeconds(receiveDate, receiveTime, null, userId);
    }

    /**
     * 获取流程接收时间到指定时间的未处理时间秒数
     *
     * @param receiveDate 流程接收日期
     * @param receiveTime 流程接收时间，需要到秒，比如 10:00:00
     * @param endTime     指定的时间，计算的截止时间，为空时为当前时间
     * @param userId      用户id
     * @return 流程未处理时间秒数
     */
    public int getOverTimeSeconds(String receiveDate, String receiveTime, @Nullable LocalDateTime endTime,
                                  int userId) {
        LocalDateTime beginCalculateTime = getBeginCalculateTime(receiveDate, receiveTime, userId);
        LocalDate beginCalculateDate = beginCalculateTime.toLocalDate();
        LocalDateTime endTimeL = endTime == null ? LocalDateTime.now() : endTime;
        LocalDate endDate = endTimeL.toLocalDate();
        LocalTime endTimeObj = endTimeL.toLocalTime();
        log.info("receiveDate:"+receiveDate+",receiveTime:+"+receiveTime+
                ",endTime:"+DATE_TIME_FORMATTER.format(endTimeL)+",userId:"+userId);
        if (beginCalculateTime.isAfter(endTimeL)) {
            return 0;
        }
        // 开始计算日期等于截止日期，则返回当天耗时
        if (beginCalculateDate.isEqual(endDate)) {
            return calculateTime(beginCalculateTime.format(DATE_FORMATTER),beginCalculateTime.toLocalTime(),
                    endTimeObj, userId);
        }
        int result = getStartDayLeftWorkTime(userId, beginCalculateTime);
        LocalDate date = beginCalculateDate.plusDays(1);
        while (date.isBefore(endDate) || date.isEqual(endDate)) {
            String dateStr = date.format(DATE_FORMATTER);
            if (!WorkTimeUtil.isTodayWork(dateStr, userId + "")) {
                date = date.plusDays(1);
                continue;
            }
            // 如果date没到截止日期，则累加当天的全部工作时间，否则累加当天已工作时间
            if (date.isBefore(endDate)) {
                result += getFullWorkTimeSeconds(dateStr, userId);
            } else {
                result += getDayPassWorkTime(dateStr, endTimeObj, userId);
            }
            date = date.plusDays(1);
        }
        return result;
    }

    private int calculateTime(String date, LocalTime startTime, LocalTime endTime, int userId) {
        List<TimeScopeEntity> workTimeList =
                WorkTimeUtil.getWorkTimeList(date, userId + "");
        int result = 0;
        for (TimeScopeEntity scope : workTimeList) {
            LocalTime startWorkTime = parseToLocalTime(scope.getBeginTime());
            LocalTime offWorkTime = parseToLocalTime(scope.getEndTime());
            if (startTime.isAfter(offWorkTime)) {
                continue;
            }
            LocalTime startCalculateTime = startTime.isAfter(startWorkTime) ? startTime : startWorkTime;
            LocalTime endCalculateTime = endTime.isBefore(offWorkTime) ? endTime : offWorkTime;
            result += endCalculateTime.toSecondOfDay() - startCalculateTime.toSecondOfDay();
            if (endTime.isBefore(offWorkTime) || endTime.equals(offWorkTime)) {
                break;
            }
        }
        return result;
    }

    /**
     * 获取当天已工作秒数
     */
    private static int getDayPassWorkTime(String date, LocalTime endTime, int userId) {
        List<TimeScopeEntity> workTimeList =
                WorkTimeUtil.getWorkTimeList(date, userId + "");
        int seconds = 0;
        for (TimeScopeEntity scope : workTimeList) {
            LocalTime startWorkTime = parseToLocalTime(scope.getBeginTime());
            LocalTime offWorkTime = parseToLocalTime(scope.getEndTime());
            boolean inRange = (endTime.isAfter(startWorkTime) || endTime.equals(startWorkTime))
                    && (endTime.isBefore(offWorkTime) || endTime.equals(offWorkTime));
            if (offWorkTime.isBefore(endTime) || offWorkTime.equals(endTime)) {
                seconds += offWorkTime.toSecondOfDay() - startWorkTime.toSecondOfDay();
            } else if (inRange) {
                seconds += endTime.toSecondOfDay() - startWorkTime.toSecondOfDay();
            }
        }
        return seconds;
    }

    private static int getFullWorkTimeSeconds(String date, int userId) {
        List<TimeScopeEntity> workTimeList =
                WorkTimeUtil.getWorkTimeList(date, userId + "");
        int calculateTime = 0;
        for (TimeScopeEntity timeScope : workTimeList) {
            LocalTime beginTime = parseToLocalTime(timeScope.getBeginTime());
            LocalTime endTime = parseToLocalTime(timeScope.getEndTime());
            // 计算 endTime 和 beginTime相差的秒数
            calculateTime += endTime.toSecondOfDay() - beginTime.toSecondOfDay();
        }
        return calculateTime;
    }

    private static boolean timeBefore(String time1, String time2) {
        LocalTime time1Obj = parseToLocalTime(time1);
        LocalTime time2Obj = parseToLocalTime(time2);
        return time1Obj.isBefore(time2Obj);
    }

    @NotNull
    private static LocalDateTime setTime(String time, LocalDateTime dateTime) {
        LocalTime workTimeObj = parseToLocalTime(time);
        LocalDateTime result = LocalDateTime.from(dateTime);
        result = result.withHour(workTimeObj.getHour())
                .withMinute(workTimeObj.getMinute())
                .withSecond(workTimeObj.getSecond());
        return result;
    }

    /**
     * 计算开始计算时间当天剩余的工作时间秒数
     */
    private int getStartDayLeftWorkTime(int userId, LocalDateTime dateTime) {
        int result = 0;
        List<TimeScopeEntity> workTimeScopes = WorkTimeUtil.getWorkTimeList(dateTime.format(DATE_FORMATTER),
                userId + "");
        for (TimeScopeEntity scope : workTimeScopes) {
            LocalTime calculateStartTime = dateTime.toLocalTime();
            LocalTime startWorkTime = parseToLocalTime(scope.getBeginTime());
            LocalTime offWorkTime = parseToLocalTime(scope.getEndTime());
            boolean inRange = (calculateStartTime.isAfter(startWorkTime) || calculateStartTime.equals(startWorkTime))
                    && (calculateStartTime.isBefore(offWorkTime) || calculateStartTime.equals(offWorkTime));
            if (inRange) {
                result += offWorkTime.toSecondOfDay() - calculateStartTime.toSecondOfDay();
            } else {
                result += offWorkTime.toSecondOfDay() - startWorkTime.toSecondOfDay();
            }
        }
        return result;
    }

    /**
     * 获取计算流程未处理天数的开始计算时间，如果接收日期是在非工作日，则开始计算时间就为下个工作日的上班时间,如果接收时间早于当天上班
     * 时间，则将开始计算时间设置为上班时间，如果接收时间晚于当天下班时间，则将开始计算时间设置为下个工作日的上班时间
     *
     * @param receiveDate 流程接收日期
     * @param receiveTime 流程接收时间，需要到秒，比如 10:00:00
     * @param userId      用户id
     * @return 流程超时开始计算时间
     */
    private LocalDateTime getBeginCalculateTime(String receiveDate, String receiveTime, int userId) {
        LocalDateTime beginCalculateTime = LocalDateTime.parse(receiveDate + " " + receiveTime, DATE_TIME_FORMATTER);
        List<TimeScopeEntity> workTimeScopes = WorkTimeUtil.getWorkTimeList(receiveDate, userId + "");
        boolean isTodayWork = !workTimeScopes.isEmpty();
        if (isTodayWork) {
            // 上班时间
            TimeScopeEntity beginWorkTime = workTimeScopes.get(0);
            // 下班时间
            TimeScopeEntity offWorkTime = workTimeScopes.get(workTimeScopes.size() - 1);
            if (timeBefore(receiveTime, beginWorkTime.getBeginTime())) {
                // 流程接收时间早于上班开始时间，将计算开始时间调整为上班时间
                beginCalculateTime = setTime(beginWorkTime.getBeginTime(), beginCalculateTime);
            } else if (timeBefore(offWorkTime.getEndTime(), receiveTime)) {
                // 流程接收时间晚于下班时间，将计算开始时间调整为下个工作日的上班时间
                beginCalculateTime = getNextWorkDayBeginTime(userId, beginCalculateTime.toLocalDate());
            }
        } else {
            // 流程接收时间的当天不上班，将计算开始时间调整为下个工作日的上班时间
            beginCalculateTime = getNextWorkDayBeginTime(userId, beginCalculateTime.toLocalDate());
        }
        return beginCalculateTime;
    }

    private LocalDateTime getNextWorkDayBeginTime(int userId, LocalDate nowDate) {
        String nextWorkDate = WorkTimeUtil.getNextWorkDate(nowDate.plusDays(1), userId + "");
        if (nextWorkDate == null) {
            throw new ConfigurationException("获取下一个工作日的排班信息，请确认是否开启考勤并设置考勤班次" +
                    "当前日期：" + DATE_FORMATTER.format(nowDate) + ",用户id：" + userId);
        }
        // 将计算开始时间调整为下个工作日的上班开始时间
        Optional<LocalDateTime> beginWorkTime = WorkTimeUtil.getBeginWorkTime(nextWorkDate, userId + "");
        return beginWorkTime.orElseThrow(() ->
                new ConfigurationException("获取不到指定日期的上班时间，请确认是否正确设置考勤排班，日期："
                        + nextWorkDate + ",用户id：" + userId));
    }

    /**
     * 将时间字符串解析为LocalTime对象，如果传入的时间格式为 HH:mm ,则添加 “:00”，变为 HH:mm:ss 格式
     */
    @NotNull
    private static LocalTime parseToLocalTime(String time) {
        if (time.split(":").length == 2) {
            time += ":00";
        }
        return LocalTime.parse(time, TIME_FORMATTER);
    }

}

package com.customization.yll.common.manager;

import com.customization.yll.common.exception.QueueTaskHandleException;

/**
 * @author 姚礼林
 * @desc TODO
 * @date 2024/7/5
 */
public interface QueueTask {
    boolean handleEvent() throws QueueTaskHandleException;
}

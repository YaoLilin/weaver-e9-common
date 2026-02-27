package com.customization.yll.common.web.modal.vo;

import lombok.Data;

import java.util.List;

/**
 * @author 姚礼林
 * @desc 通用分页结果
 * @date 2026/1/23
 **/
@Data
public class PageResult<T> {
    private List<T> list;
    private Integer total;
    private Integer pageNo;
    private Integer pageSize;
}

package com.customization.yll.common.exception;

/**
 * @author 姚礼林
 * @desc sql 执行失败异常
 * @date 2024/6/7
 */
public class SqlExecuteException extends RuntimeException {
    private String sql;

    public SqlExecuteException(String message) {
        super(message);
    }

    public SqlExecuteException() {
    }

    public SqlExecuteException(String message, String sql) {
        super(message);
        this.sql = sql;
    }

    public SqlExecuteException(String message, Throwable cause) {
        super(message, cause);
    }
}

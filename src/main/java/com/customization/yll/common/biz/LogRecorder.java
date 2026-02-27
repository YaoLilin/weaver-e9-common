package com.customization.yll.common.biz;

import com.customization.yll.common.util.ModeUtil;
import weaver.conn.RecordSet;
import weaver.integration.logging.Logger;
import weaver.integration.logging.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 姚礼林
 * @desc 将日志存放到建模中
 * @date 2024/2/1
 */
public class LogRecorder {
    private static final int INFO = 0;
    private static final int ERROR = 1;
    private static final String LOG_TABLE_NAME = "uf_logrecord";
    private final SimpleDateFormat format;
    private final RecordSet recordSet;
    private final int modeId;
    private final Logger logger;

    public LogRecorder(String className){
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        recordSet = new RecordSet();
        modeId = ModeUtil.getModeIdByTableName(LOG_TABLE_NAME,recordSet);
        logger = LoggerFactory.getLogger(className);
    }

    public LogRecorder(Class<?> cls){
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        recordSet = new RecordSet();
        modeId = ModeUtil.getModeIdByTableName(LOG_TABLE_NAME,recordSet);
        logger = LoggerFactory.getLogger(cls);
    }

    /**
     * 插入信息日志
     * @param content 日志内容
     */
    public void info(String content) {
        String position = getPosition();
        logger.info(position+":"+content);
        insertLog(content,position,INFO);
    }

    /**
     * 插入错误日志
     * @param content 日志内容
     */
    public  void error(String content) {
        String position = getPosition();
        logger.error(position+":"+content);
        insertLog(content,position,ERROR);
    }

    /**
     * 插入错误日志
     * @param content 日志内容
     * @param e 异常信息
     */
    public  void error(String content, Throwable e) {
        String position = getPosition();
        logger.error(position+":"+content,e);
        insertLog(content,position,ERROR);
    }

    private  void insertLog(String content,String position,int type){
        Map<String, Object> fieldData = new HashMap<>(2);
        fieldData.put("type",type);
        fieldData.put("time", format.format(System.currentTimeMillis()));
        fieldData.put("position", position);
        fieldData.put("content", content);
        ModeUtil.insertToMode(fieldData, LOG_TABLE_NAME, modeId, recordSet);
    }

    private String getPosition(){
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        return stackTrace[3].getClassName() + "-" + stackTrace[3].getMethodName() + "():" + stackTrace[3].getLineNumber();
    }

}

package com.customization.yll.common.util;

import cn.hutool.core.convert.Convert;
import com.customization.yll.common.bean.SearchPageFieldInfo;
import com.customization.yll.common.exception.SqlExecuteException;
import lombok.experimental.UtilityClass;
import weaver.conn.RecordSet;
import weaver.conn.RecordSetExecutionInterface;
import weaver.formmode.setup.ModeRightInfo;
import weaver.integration.logging.Logger;
import weaver.integration.logging.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author 姚礼林
 * @desc 建模工具类
 * @date 2023/5/12
 */
@UtilityClass
public class ModeUtil {
    private static final Logger logger = LoggerFactory.getLogger(ModeUtil.class);

    /**
     * 根据建模表名获取到对应的建模id
     *
     * @param tableName 建模表名
     * @return 建模id
     */
    public static int getModeIdByTableName(String tableName) {
        return getModeIdByTableName(tableName, new RecordSet());
    }

    /**
     * 根据建模表名获取到对应的建模id
     *
     * @param tableName 建模表名
     * @param recordSet recordSet
     * @return 建模id，如果获取不到则返回-1
     */
    public static int getModeIdByTableName(String tableName, RecordSet recordSet) {
        String getModeIdSql = "select b.id from workflow_bill a,modeinfo b where a.tablename = ? and b.formid = a.id";
        recordSet.executeQuery(getModeIdSql, tableName);
        recordSet.next();
        return recordSet.getInt("id");
    }

    public static String getTableNameByModeId(int modeId, RecordSet recordSet) {
        recordSet.executeQuery("select a.tablename from workflow_bill a,modeinfo b " +
                "where b.id=? and b.formid = a.id", modeId);
        recordSet.next();
        return recordSet.getString("tablename");
    }

    public static String getTableNameBySearchId(int searchId, RecordSet recordSet) {
        recordSet.executeQuery("SELECT modeid from mode_customsearch where id=?", searchId);
        recordSet.next();
        int modeId = recordSet.getInt("modeid");
        return getTableNameByModeId(modeId, recordSet);
    }

    /**
     * 获取表单id
     *
     * @param modeId    建模id
     * @param recordSet RecordSet
     * @return 表单id
     */
    public static Optional<Integer> getFormId(int modeId, RecordSet recordSet) {
        if (!recordSet.executeQuery("SELECT formid FROM modeinfo WHERE id=?", modeId)) {
            throw new SqlExecuteException("执行sql失败，modeid=" + modeId);
        }
        if (!recordSet.next()) {
            return Optional.empty();
        }
        Integer formId = Convert.toInt(recordSet.getString("formid"));
        if (formId == null) {
            return Optional.empty();
        }
        return Optional.of(formId);
    }

    /**
     * 获取建模查询的字段信息，仅获取查询列表勾选的字段
     *
     * @param searchId  查询id
     * @param recordSet recordSet
     * @return 建模查询的字段信息
     */
    public static List<SearchPageFieldInfo> getSearchPageFields(int searchId, RecordSet recordSet) {
        List<SearchPageFieldInfo> fieldInfoList = new ArrayList<>();
        recordSet.executeQuery("select b.LABELNAME,f.fieldname,f.detailtable,a.isshow,a.FIELDID from mode_customdspfield a " +
                "left join htmllabelinfo b on a.SHOWNAMELABEL = b.indexid AND b.LANGUAGEID = 7 " +
                "join workflow_billfield f on a.fieldid = f.id " +
                "where a.customid=?", searchId);
        while (recordSet.next()) {
            SearchPageFieldInfo fieldInfo = new SearchPageFieldInfo();
            fieldInfo.setFieldName(recordSet.getString("fieldname"));
            fieldInfo.setFieldId(recordSet.getInt("FIELDID"));
            fieldInfo.setShow("1".equals(recordSet.getString("isshow")));
            fieldInfo.setShowName(recordSet.getString("LABELNAME"));
            fieldInfo.setDetailTable(recordSet.getString("detailtable"));
            fieldInfoList.add(fieldInfo);
        }
        return fieldInfoList;
    }

    /**
     * 插入建模数据
     *
     * @param fieldData 字段数据，key为字段名称，value为字段值
     * @param tableName 建模表名称
     * @param recordSet recordSet
     * @return 是否插入成功
     */
    public static boolean insertToMode(Map<String, Object> fieldData, String tableName, RecordSet recordSet) {
        int modeId = getModeIdByTableName(tableName, recordSet);
        return insertToMode(fieldData, tableName, modeId, new RecordSet());
    }

    /**
     * 插入建模数据
     *
     * @param fieldData 字段数据，key为字段名称，value为字段值
     * @param tableName 建模表名称
     * @param modeId    建模id
     * @param recordSet recordSet
     * @return 是否插入成功
     */
    public static boolean insertToMode(Map<String, Object> fieldData, String tableName, int modeId, RecordSet recordSet) {
        try {
            return insertToModeByRsInterface(fieldData, tableName, modeId, recordSet);
        } catch (Exception e) {
            logger.error("插入建模数据错误，tableName=" + tableName, e);
            return false;
        }
    }

    /**
     * 插入建模数据并获取新增的数据id
     *
     * @param fieldData 字段数据，key为字段名称，value为字段值
     * @param tableName 建模表名称
     * @param modeId    建模id
     * @param recordSet recordSet
     * @return 新增数据的数据id
     */
    public static Optional<Integer> insertToModeAndGetId(Map<String, Object> fieldData, String tableName, int modeId,
                                                         RecordSet recordSet) {
        try {
            return insertToModeAndGetIdByRsInterface(fieldData, tableName, modeId, recordSet);
        } catch (Exception e) {
            logger.error("插入建模数据错误，tableName=" + tableName, e);
            return Optional.empty();
        }
    }

    /**
     * 插入建模数据，可用于执行事务
     *
     * @param fieldData 字段数据，key为字段名称，value为字段值
     * @param tableName 建模表名称
     * @param modeId    建模id
     * @param recordSet recordSet
     * @return 是否插入成功
     */
    public static boolean insertToModeByRsInterface(Map<String, Object> fieldData, String tableName, int modeId,
                                                    RecordSetExecutionInterface recordSet) throws Exception {
        return insertToModeAndGetIdByRsInterface(fieldData, tableName, modeId, recordSet).isPresent();
    }

    /**
     * 插入建模数据，可用于执行事务，并获取到新增数据的数据id
     *
     * @param fieldData 字段数据，key为字段名称，value为字段值
     * @param tableName 建模表名称
     * @param modeId    建模id
     * @param recordSet recordSet
     * @return 新增数据的数据id
     */
    public static Optional<Integer> insertToModeAndGetIdByRsInterface(Map<String, Object> fieldData,
                                                                      String tableName, int modeId,
                                                                      RecordSetExecutionInterface recordSet) throws Exception {
        Map<String, Object> data = new HashMap<>(fieldData);
        String uuid = UUID.randomUUID().toString();
        addStanderFieldValue(data, modeId, uuid);
        if (!DbUtil.insertByRsInterface(tableName, data, recordSet)) {
            return Optional.empty();
        }
        // 执行权限重构
        recordSet.executeSql("select id from " + tableName + " where modeuuid=?", true,
                "", false, uuid);
        recordSet.next();
        int dataId = recordSet.getInt("id");
        reconstructionJC(dataId, modeId, 1);
        return Optional.of(dataId);
    }

    /**
     * 建模台账批量插入数据
     *
     * @param data               批量数据
     * @param tableName          建模表名称
     * @param modeId             建模id
     * @param isReconstructionJC 是否执行权限重构
     * @param recordSet          recordSet
     * @return 是否成功
     */
    public static boolean batchInsertToMode(List<Map<String, Object>> data, String tableName, int modeId,
                                            boolean isReconstructionJC, RecordSet recordSet) {
        try {
            return batchInsertToModeByRsInterface(data, tableName, modeId, isReconstructionJC, recordSet);
        } catch (Exception e) {
            logger.error("批量插入建模数据错误，tableName=" + tableName, e);
            return false;
        }
    }

    /**
     * 建模台账批量插入数据,可用于执行事务
     *
     * @param data               批量数据
     * @param tableName          建模表名称
     * @param modeId             建模id
     * @param isReconstructionJC 是否执行权限重构
     * @param recordSet          recordSet
     * @return 是否成功
     */
    public static boolean batchInsertToModeByRsInterface(List<Map<String, Object>> data, String tableName, int modeId,
                                                         boolean isReconstructionJC,
                                                         RecordSetExecutionInterface recordSet) throws Exception {
        List<String> uuids = new ArrayList<>();
        List<Map<String, Object>> insertData = new ArrayList<>(data);
        insertData.forEach(i -> {
            String uuid = UUID.randomUUID().toString();
            uuids.add(uuid);
            addStanderFieldValue(i, modeId, uuid);
        });
        if (!DbUtil.batchInsert(insertData, tableName, recordSet)) {
            return false;
        }

        if (isReconstructionJC) {
            List<Integer> ids = new ArrayList<>();
            String sql = "select id from " + tableName + " where modeuuid=?";
            for (String uuid : uuids) {
                if (!recordSet.executeSql(sql, true, "", false, uuid)) {
                    logger.error("根据uuid查询数据id失败");
                    continue;
                }
                if (!recordSet.next()) {
                    logger.warn("无法查询到数据id，uuid:" + uuid);
                    continue;
                }
                ids.add(recordSet.getInt("id"));
            }
            if (ids.isEmpty()) {
                logger.error("ids 集合为空");
            }
            ids.forEach(id -> reconstructionJC(id, modeId, 1));
        }
        return true;
    }

    /**
     * 更新建模数据
     *
     * @param data      更新数据
     * @param dataId    建模数据id
     * @param tableName 建模表名
     * @param recordSet recordSet
     * @return 是否成功
     */
    public static boolean updateMode(Map<String, Object> data, int dataId, String tableName, RecordSet recordSet) {
        Map<String, Object> condition = new HashMap<>(1);
        condition.put("id", dataId);
        return DbUtil.update(data, condition, tableName, recordSet);
    }

    private static void addStanderFieldValue(Map<String, Object> fieldData, int modeId, String uuid) {
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = format.format(currentTime);
        format.applyPattern("HH:mm:ss");
        String time = format.format(currentTime);
        fieldData.put("modeuuid", uuid);
        fieldData.put("formmodeid", modeId);
        fieldData.put("modedatacreater", 1);
        fieldData.put("modedatacreatertype", 0);
        fieldData.put("modedatacreatedate", dateStr);
        fieldData.put("modedatacreatetime", time);
    }

    /**
     * 权限重构
     *
     * @param id      数据id
     * @param modelId 模块id
     * @param userId  用户id
     */
    private static void reconstructionJC(int id, int modelId, int userId) {
        ModeRightInfo modeRightInfo = new ModeRightInfo();
        modeRightInfo.setNewRight(true);
        modeRightInfo.editModeDataShare(userId, modelId, id);
    }

}

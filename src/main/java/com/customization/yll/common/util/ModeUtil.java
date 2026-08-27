package com.customization.yll.common.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.customization.yll.common.IntegrationLog;
import com.customization.yll.common.bean.SearchPageFieldInfo;
import com.customization.yll.common.exception.SqlExecuteException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import weaver.conn.RecordSet;
import weaver.conn.RecordSetExecutionInterface;
import weaver.formmode.setup.ModeRightInfo;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * @author 姚礼林
 * @desc 建模工具类
 * @date 2023/5/12
 */
@UtilityClass
public class ModeUtil {
    private static final IntegrationLog logger = new IntegrationLog(ModeUtil.class);
    private static final String UPDATE_TIME_FIELD = "modedatamodifydatetime";
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 根据建模表名获取到对应的建模id
     *
     * @param tableName 建模表名
     * @return 建模id，如果不存在则返回 -1
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
        if (!recordSet.next()) {
            return -1;
        }
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
     * 插入建模数据，可用于执行事务
     *
     * @param fieldData               字段数据，key为字段名称，value为字段值
     * @param tableName               建模表名称
     * @param modeId                  建模id
     * @param reconstructionJCOoption 执行权限重构选项，如果不需要执行权限重构，传入 null 即可
     * @param recordSet               recordSet
     * @return 新插入数据中的uuid，可以根据uuid查询到数据id，如果插入失败则返回 Optional.empty()
     * @throws Exception 执行数据库发生异常
     */
    public static Optional<String> insertToMode(Map<String, Object> fieldData, String tableName, int modeId,
                                                @Nullable ReconstructionJCOoption reconstructionJCOoption,
                                                RecordSetExecutionInterface recordSet) throws Exception {
        return insert(fieldData, tableName, modeId, reconstructionJCOoption, recordSet);
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
     * 插入建模数据并获取新增的数据id
     *
     * @param fieldData               字段数据，key为字段名称，value为字段值
     * @param tableName               建模表名称
     * @param modeId                  建模id
     * @param reconstructionJCOoption 执行权限重构选项，如果不需要执行权限重构，传入 null 即可
     * @param recordSet               recordSet
     * @return 新增数据的数据id
     * @throws Exception 执行数据库发生异常
     */
    public static Optional<Integer> insertToModeAndGetId(Map<String, Object> fieldData, String tableName, int modeId,
                                                         @Nullable ReconstructionJCOoption reconstructionJCOoption,
                                                         RecordSetExecutionInterface recordSet) throws Exception {
        Optional<String> uuidOp = insert(fieldData, tableName, modeId,
                reconstructionJCOoption, recordSet);
        if (!uuidOp.isPresent()) {
            logger.error("插入建模数据失败，tableName=" + tableName);
            return Optional.empty();
        }
        return queryIdByUuid(tableName, uuidOp.get(), recordSet);
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
        return insert(fieldData, tableName, modeId, new ReconstructionJCOoption(true, true),
                recordSet).isPresent();
    }

    /**
     * 插入建模数据，可用于执行事务，并获取到新增数据的数据id
     *
     * @param fieldData 字段数据，key为字段名称，value为字段值
     * @param tableName 建模表名称
     * @param modeId    建模id
     * @param recordSet recordSet
     * @return 新增数据的数据id
     * @throws Exception 执行数据库发生异常
     */
    public static Optional<Integer> insertToModeAndGetIdByRsInterface(Map<String, Object> fieldData,
                                                                      String tableName, int modeId,
                                                                      RecordSetExecutionInterface recordSet) throws Exception {
        return insertToModeAndGetId(fieldData, tableName, modeId,
                new ReconstructionJCOoption(true, true), recordSet);
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
        if (CollUtil.isEmpty(data)) {
            logger.info("无插入数据");
            return true;
        }
        verifyBatchInsertParams(tableName, modeId, recordSet);

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
        if (CollUtil.isEmpty(data)) {
            logger.info("无插入数据");
            return true;
        }
        verifyBatchInsertParams(tableName, modeId, recordSet);

        List<String> uuids = getUuids(data.size());

        if (!batchInsert(data, uuids, tableName, modeId, recordSet)) {
            return false;
        }

        if (isReconstructionJC) {
            List<Integer> ids = getIdsByUuid(tableName, recordSet, uuids);
            batchReconstructionJC(modeId, ids);
        }
        return true;
    }

    /**
     * 执行批量插入操作并获取插入数据的ID
     *
     * @param data               待插入的数据列表，每个元素是一个Map，表示一行数据
     * @param tableName          目标表名
     * @param modeId             模块id
     * @param isReconstructionJC 是否执行权限重构
     * @param recordSet          RecordSetExecutionInterface 对象
     * @return 返回一个BatchInsertResult对象，包含插入是否成功以及成功的插入记录ID列表。如果插入失败或获取ID过程中发生异常，则返回空的ID列表。
     */
    public static BatchInsertResult batchInsertAndGetIds(List<Map<String, Object>> data, String tableName, int modeId,
                                                         boolean isReconstructionJC,
                                                         RecordSetExecutionInterface recordSet) {
        if (CollUtil.isEmpty(data)) {
            logger.info("无插入数据");
            return new BatchInsertResult(true, Collections.emptyList());
        }
        verifyBatchInsertParams(tableName, modeId, recordSet);

        List<String> uuids = getUuids(data.size());

        if (!batchInsert(data, uuids, tableName, modeId, recordSet)) {
            return new BatchInsertResult(false, null);
        }

        List<Integer> ids;
        try {
            ids = getIdsByUuid(tableName, recordSet, uuids);
        } catch (Exception e) {
            logger.error("根据uuid查询id发生异常", e);
            return new BatchInsertResult(true, Collections.emptyList());
        }

        if (isReconstructionJC) {
            batchReconstructionJC(modeId, ids);
        }
        return new BatchInsertResult(true, ids);
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
        return updateMode(data, condition, tableName, recordSet);
    }

    /**
     * 根据条件更新建模数据，更新条件可为多个
     *
     * @param data      更新数据
     * @param condition 更新条件，多个 AND 条件，如：{"code":"A01","name":"one"}
     * @param tableName 建模表名
     * @param recordSet recordSet
     * @return 是否成功
     */
    public static boolean updateMode(Map<String, Object> data, Map<String, Object> condition,
                                     String tableName, RecordSet recordSet) {
        if (!data.containsKey(UPDATE_TIME_FIELD)) {
            String updateTime = DateUtil.format(new Date(), DATE_TIME_FORMAT);
            data.put(UPDATE_TIME_FIELD, updateTime);
        }
        return DbUtil.update(data, condition, tableName, recordSet);
    }

    private static void verifyBatchInsertParams(String tableName, int modeId, RecordSetExecutionInterface recordSet) {
        if (StrUtil.isBlank(tableName)) {
            throw new IllegalArgumentException("表名为空，请传入正确表名");
        }
        if (modeId < 1) {
            throw new IllegalArgumentException("modeId 不正确，必需大于0，modeId:" + modeId);
        }
        Objects.requireNonNull(recordSet, "RecordSet 对象不能为空");
    }

    /**
     * 插入建模数据
     *
     * @return 新增数据的uuid
     */
    private static Optional<String> insert(Map<String, Object> fieldData, String tableName, int modeId,
                                           ReconstructionJCOoption reconstructionJCOoption,
                                           RecordSetExecutionInterface recordSet) throws Exception {
        logger.debug("表名：{}, modeId: {}, 插入数据：{}", tableName, modeId, logger.toJsonStr(fieldData));
        Map<String, Object> data = new HashMap<>(fieldData);
        String uuid = UUID.randomUUID().toString();
        addStanderFieldValue(data, modeId, uuid);
        if (!DbUtil.insertByRsInterface(tableName, data, recordSet)) {
            logger.error("插入失败");
            return Optional.empty();
        }
        if (reconstructionJCOoption != null && reconstructionJCOoption.isReconstructionJC()) {
            // 执行权限重构
            Optional<Integer> id = queryIdByUuid(tableName, uuid, recordSet);
            if (id.isPresent()) {
                if (reconstructionJCOoption.async) {
                    CompletableFuture.runAsync(() -> reconstructionJC(id.get(), modeId, 1));
                } else {
                    reconstructionJC(id.get(), modeId, 1);
                }
            } else {
                logger.error("获取数据id失败，无法执行权限重构，uuid：" + uuid);
            }

        }
        return Optional.of(uuid);
    }

    private Optional<Integer> queryIdByUuid(String tableName, String uuid, RecordSetExecutionInterface recordSet) {
        try {
            if (!recordSet.executeSql("select id from " + tableName + " where modeuuid=?", true,
                    "", false, uuid)) {
                logger.error("获取数据id失败，请到 ecology 日志查看详细错误信息，uuid：" + uuid);
                return Optional.empty();
            }
            recordSet.next();
            int dataId = recordSet.getInt("id");
            return Optional.of(dataId);
        } catch (Exception e) {
            logger.error("根据uuid查询数据id发生异常，uuid：" + uuid, e);
            return Optional.empty();
        }
    }

    /**
     * 批量权限重构
     */
    private static void batchReconstructionJC(int modeId, List<Integer> ids) {
        try {
            if (CollUtil.isNotEmpty(ids)) {
                for (Integer id : ids) {
                    reconstructionJC(id, modeId, 1);
                }
            } else {
                logger.error("ids 集合为空");
            }
        } catch (Exception e) {
            logger.error("执行权限重构发生异常", e);
        }
    }

    @NotNull
    private static List<String> getUuids(int num) {
        List<String> uuids = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            uuids.add(UUID.randomUUID().toString());
        }
        return uuids;
    }

    private static boolean batchInsert(List<Map<String, Object>> data, List<String> uuidList, String tableName, int modeId,
                                       RecordSetExecutionInterface recordSet) {
        if (data.size() != uuidList.size()) {
            logger.error("插入失败，待插入数据数量不等于uuid数量");
            return false;
        }
        List<Map<String, Object>> insertData = new ArrayList<>(data);
        for (int i = 0; i < insertData.size(); i++) {
            Map<String, Object> item = insertData.get(i);
            String uuid = uuidList.get(i);

            addStanderFieldValue(item, modeId, uuid);
        }

        try {
            return DbUtil.batchInsert(insertData, tableName, recordSet);
        } catch (Exception e) {
            logger.error("批量插入发生异常", e);
            return false;
        }
    }

    @NotNull
    private static List<Integer> getIdsByUuid(String tableName, RecordSetExecutionInterface recordSet,
                                              List<String> uuids) throws Exception {
        List<Integer> ids = new ArrayList<>();
        String sql = "select id from " + tableName + " where modeuuid=?";
        for (String uuid : uuids) {
            if (StrUtil.isBlank(uuid)) {
                continue;
            }
            if (!recordSet.executeSql(sql, true, "", false, uuid)) {
                logger.error("根据uuid查询数据id失败,uuid:" + uuid + ",表名：" + tableName);
                break;
            }
            if (!recordSet.next()) {
                logger.warn("无法查询到数据id，uuid:" + uuid);
                continue;
            }
            ids.add(recordSet.getInt("id"));
        }
        return ids;
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

    @Data
    @AllArgsConstructor
    public static class BatchInsertResult {
        private boolean success;
        private List<Integer> ids;
    }

    @AllArgsConstructor
    @Data
    public static class ReconstructionJCOoption {
        /**
         * 是否执行权限重构
         */
        private boolean isReconstructionJC;
        /**
         * 是否异步执行权限重构，权限重构可能会耗时比较久，可使用异步执行，无需等待权限重构完成
         */
        private boolean async;
    }

}

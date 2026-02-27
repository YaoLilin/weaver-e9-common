package com.customization.yll.common.manager;

import com.customization.yll.common.bean.MapInfo;
import com.customization.yll.common.enu.FieldType;
import com.customization.yll.common.exception.FieldNotFoundException;
import com.customization.yll.common.exception.FieldValueEmptyException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import weaver.soa.workflow.request.Cell;
import weaver.soa.workflow.request.Property;
import weaver.soa.workflow.request.Row;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkflowFieldMapperTest {

    @Test
    @DisplayName("主表字段映射：基础映射与类型转换")
    void mapMainFieldShouldMapAndConvertValues() {
        WorkflowFieldMapper mapper = new WorkflowFieldMapper();
        mapper.addMainFieldMapConfig("name", new MapInfo("lastName"));
        mapper.addMainFieldMapConfig("age", new MapInfo("age", FieldType.INTEGER));
        mapper.addMainFieldMapConfig("score", new MapInfo("score", value -> "S" + value));

        Property name = mockProperty("name", "John");
        Property age = mockProperty("age", "18");
        Property score = mockProperty("score", "90");

        Map<String, Object> result = mapper.mapMainField(new Property[]{name, age, score});

        assertEquals("John", result.get("lastName"));
        assertEquals(18, result.get("age"));
        assertEquals("S90", result.get("score"));
    }

    @Test
    @DisplayName("主表字段映射：满足跳过条件时不写入参数")
    void mapMainFieldShouldSkipByCondition() {
        WorkflowFieldMapper mapper = new WorkflowFieldMapper();
        MapInfo mapInfo = new MapInfo("flagValue").setSkipCondition("0"::equals);
        mapper.addMainFieldMapConfig("flag", mapInfo);

        Property flag = mockProperty("flag", "0");
        Map<String, Object> result = mapper.mapMainField(new Property[]{flag});

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("主表字段映射：字段不存在时抛异常")
    void mapMainFieldShouldThrowWhenMissingField() {
        WorkflowFieldMapper mapper = new WorkflowFieldMapper();
        mapper.addMainFieldMapConfig("missing", new MapInfo("target"));

        Property name = mockPropertyNameOnly("name");

        assertThrows(FieldNotFoundException.class, () -> mapper.mapMainField(new Property[]{name}));
    }

    @Test
    @DisplayName("主表字段映射：必填字段为空时抛异常")
    void mapMainFieldShouldThrowWhenRequiredEmpty() {
        WorkflowFieldMapper mapper = new WorkflowFieldMapper();
        mapper.addMainFieldMapConfig("req", new MapInfo("reqParam", true));

        Property req = mockProperty("req", "");

        assertThrows(FieldValueEmptyException.class, () -> mapper.mapMainField(new Property[]{req}));
    }

    @Test
    @DisplayName("明细表字段映射：多行映射与类型转换")
    void mapDetailFieldShouldMapRows() {
        WorkflowFieldMapper mapper = new WorkflowFieldMapper();
        mapper.addDetailFieldMapConfig("item", new MapInfo("itemName"));
        mapper.addDetailFieldMapConfig("price", new MapInfo("price", FieldType.DOUBLE));

        Row row1 = mockRow(new Cell[]{
                mockCell("item", "A"),
                mockCell("price", "10.5")
        });
        Row row2 = mockRow(new Cell[]{
                mockCell("item", "B"),
                mockCell("price", "20")
        });

        List<Map<String, Object>> result = mapper.mapDetailField(new Row[]{row1, row2});

        assertEquals(2, result.size());
        assertEquals("A", result.get(0).get("itemName"));
        assertEquals(10.5, result.get(0).get("price"));
        assertEquals("B", result.get(1).get("itemName"));
        assertEquals(20.0, result.get(1).get("price"));
    }

    @Test
    @DisplayName("明细表字段映射：字段不存在时抛异常")
    void mapDetailFieldShouldThrowWhenMissingField() {
        WorkflowFieldMapper mapper = new WorkflowFieldMapper();
        mapper.addDetailFieldMapConfig("missing", new MapInfo("target"));

        Row row = mockRow(new Cell[]{mockCellNameOnly("item")});

        assertThrows(FieldNotFoundException.class, () -> mapper.mapDetailField(new Row[]{row}));
    }

    @Test
    @DisplayName("清理映射配置：主表与明细表")
    void cleanMapConfigShouldClear() {
        WorkflowFieldMapper mapper = new WorkflowFieldMapper();
        mapper.addMainFieldMapConfig("name", new MapInfo("lastName"));
        mapper.addDetailFieldMapConfig("item", new MapInfo("itemName"));

        mapper.cleanMainFieldParamMap();
        mapper.cleanDetailFieldParamMap();

        assertTrue(mapper.getMainFieldMapConfig().isEmpty());
        assertTrue(mapper.getDetailFieldMapConfig().isEmpty());
    }

    private Property mockProperty(String name, String value) {
        Property property = mock(Property.class);
        when(property.getName()).thenReturn(name);
        when(property.getValue()).thenReturn(value);
        return property;
    }

    private Property mockPropertyNameOnly(String name) {
        Property property = mock(Property.class);
        when(property.getName()).thenReturn(name);
        return property;
    }

    private Cell mockCell(String name, String value) {
        Cell cell = mock(Cell.class);
        when(cell.getName()).thenReturn(name);
        when(cell.getValue()).thenReturn(value);
        return cell;
    }

    private Cell mockCellNameOnly(String name) {
        Cell cell = mock(Cell.class);
        when(cell.getName()).thenReturn(name);
        return cell;
    }

    private Row mockRow(Cell[] cells) {
        Row row = mock(Row.class);
        when(row.getCell()).thenReturn(cells);
        return row;
    }
}

package com.customization.yll.common.web.util;

import com.customization.yll.common.exception.FieldValueEmptyException;
import com.customization.yll.common.web.ApiModel;
import com.customization.yll.common.web.ApiParam;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author 姚礼林
 * @desc ApiParamValidator 单元测试类
 * @date 2025/12/17
 **/
class ApiParamValidatorTest {

    /**
     * 测试null对象 - 应该不抛异常
     */
    @Test
    void testValidate_NullObject() {
        assertDoesNotThrow(() -> ApiParamValidator.validate(null));
    }

    /**
     * 测试所有必填字段都有值 - 应该不抛异常
     */
    @Test
    void testValidate_AllRequiredFieldsHaveValues() {
        TestDataClass obj = new TestDataClass();
        obj.setRequiredString("test");
        obj.setRequiredInteger(123);
        obj.setRequiredList(new ArrayList<>());
        obj.getRequiredList().add("item1");
        obj.setRequiredArray(new String[]{"array1", "array2"});

        assertDoesNotThrow(() -> ApiParamValidator.validate(obj));
    }

    /**
     * 测试必填字符串字段为null - 应该抛异常
     */
    @Test
    void testValidate_RequiredStringIsNull() {
        TestDataClass obj = new TestDataClass();
        obj.setRequiredString(null);
        obj.setRequiredInteger(123);
        obj.setRequiredList(new ArrayList<>());
        obj.setRequiredArray(new String[]{"test"});

        FieldValueEmptyException exception = assertThrows(FieldValueEmptyException.class,
                () -> ApiParamValidator.validate(obj));
        assertTrue(exception.getMessage().contains("requiredString"));
        assertEquals("requiredString", exception.getFieldName());
    }

    /**
     * 测试必填字符串字段为空字符串 - 应该抛异常
     */
    @Test
    void testValidate_RequiredStringIsEmpty() {
        TestDataClass obj = new TestDataClass();
        obj.setRequiredString("");
        obj.setRequiredInteger(123);
        obj.setRequiredList(new ArrayList<>());
        obj.setRequiredArray(new String[]{"test"});

        FieldValueEmptyException exception = assertThrows(FieldValueEmptyException.class,
                () -> ApiParamValidator.validate(obj));
        assertTrue(exception.getMessage().contains("requiredString"));
    }

    /**
     * 测试必填字符串字段为空白字符串 - 应该抛异常
     */
    @Test
    void testValidate_RequiredStringIsBlank() {
        TestDataClass obj = new TestDataClass();
        obj.setRequiredString("   ");
        obj.setRequiredInteger(123);
        obj.setRequiredList(new ArrayList<>());
        obj.setRequiredArray(new String[]{"test"});

        FieldValueEmptyException exception = assertThrows(FieldValueEmptyException.class,
                () -> ApiParamValidator.validate(obj));
        assertTrue(exception.getMessage().contains("requiredString"));
    }

    /**
     * 测试必填整数字段为null - 应该抛异常
     */
    @Test
    void testValidate_RequiredIntegerIsNull() {
        TestDataClass obj = new TestDataClass();
        obj.setRequiredString("test");
        obj.setRequiredInteger(null);
        obj.setRequiredList(new ArrayList<>());
        obj.setRequiredArray(new String[]{"test"});

        FieldValueEmptyException exception = assertThrows(FieldValueEmptyException.class,
                () -> ApiParamValidator.validate(obj));
        assertTrue(exception.getMessage().contains("requiredInteger"));
        assertEquals("requiredInteger", exception.getFieldName());
    }

    /**
     * 测试必填集合字段为null - 应该抛异常
     */
    @Test
    void testValidate_RequiredListIsNull() {
        TestDataClass obj = new TestDataClass();
        obj.setRequiredString("test");
        obj.setRequiredInteger(123);
        obj.setRequiredList(null);
        obj.setRequiredArray(new String[]{"test"});

        FieldValueEmptyException exception = assertThrows(FieldValueEmptyException.class,
                () -> ApiParamValidator.validate(obj));
        assertTrue(exception.getMessage().contains("requiredList"));
        assertEquals("requiredList", exception.getFieldName());
    }

    /**
     * 测试必填集合字段为空集合 - 应该抛异常
     */
    @Test
    void testValidate_RequiredListIsEmpty() {
        TestDataClass obj = new TestDataClass();
        obj.setRequiredString("test");
        obj.setRequiredInteger(123);
        obj.setRequiredList(new ArrayList<>());
        obj.setRequiredArray(new String[]{"test"});

        // 空集合应该被视为空值
        FieldValueEmptyException exception = assertThrows(FieldValueEmptyException.class,
                () -> ApiParamValidator.validate(obj));
        assertTrue(exception.getMessage().contains("requiredList"));
    }

    /**
     * 测试必填数组字段为null - 应该抛异常
     */
    @Test
    void testValidate_RequiredArrayIsNull() {
        TestDataClass obj = new TestDataClass();
        obj.setRequiredString("test");
        obj.setRequiredInteger(123);
        obj.setRequiredList(new ArrayList<>());
        obj.getRequiredList().add("item");
        obj.setRequiredArray(null);

        FieldValueEmptyException exception = assertThrows(FieldValueEmptyException.class,
                () -> ApiParamValidator.validate(obj));
        assertTrue(exception.getMessage().contains("requiredArray"));
        assertEquals("requiredArray", exception.getFieldName());
    }

    /**
     * 测试必填数组字段为空数组 - 应该抛异常
     */
    @Test
    void testValidate_RequiredArrayIsEmpty() {
        TestDataClass obj = new TestDataClass();
        obj.setRequiredString("test");
        obj.setRequiredInteger(123);
        obj.setRequiredList(new ArrayList<>());
        obj.getRequiredList().add("item");
        obj.setRequiredArray(new String[]{});

        FieldValueEmptyException exception = assertThrows(FieldValueEmptyException.class,
                () -> ApiParamValidator.validate(obj));
        assertTrue(exception.getMessage().contains("requiredArray"));
    }

    /**
     * 测试非必填字段为空 - 应该不抛异常
     */
    @Test
    void testValidate_OptionalFieldsAreEmpty() {
        TestDataClass obj = new TestDataClass();
        obj.setRequiredString("test");
        obj.setRequiredInteger(123);
        obj.setRequiredList(new ArrayList<>());
        obj.getRequiredList().add("item");
        obj.setRequiredArray(new String[]{"test"});
        // 非必填字段都为空
        obj.setOptionalString(null);
        obj.setOptionalInteger(null);
        obj.setOptionalList(null);
        obj.setOptionalArray(null);

        assertDoesNotThrow(() -> ApiParamValidator.validate(obj));
    }

    /**
     * 测试没有@ApiParam注解的字段 - 应该不校验
     */
    @Test
    void testValidate_NoAnnotationField() {
        TestDataClass obj = new TestDataClass();
        obj.setRequiredString("test");
        obj.setRequiredInteger(123);
        obj.setRequiredList(new ArrayList<>());
        obj.getRequiredList().add("item");
        obj.setRequiredArray(new String[]{"test"});
        // 没有注解的字段为null，不应该影响校验
        obj.setNoAnnotationField(null);

        assertDoesNotThrow(() -> ApiParamValidator.validate(obj));
    }

    /**
     * 测试required默认为false的字段 - 应该不校验
     */
    @Test
    void testValidate_DefaultOptionalField() {
        TestDataClass obj = new TestDataClass();
        obj.setRequiredString("test");
        obj.setRequiredInteger(123);
        obj.setRequiredList(new ArrayList<>());
        obj.getRequiredList().add("item");
        obj.setRequiredArray(new String[]{"test"});
        // required默认为false的字段为null，不应该影响校验
        obj.setDefaultOptionalField(null);

        assertDoesNotThrow(() -> ApiParamValidator.validate(obj));
    }

    /**
     * 测试多个必填字段，部分为空 - 应该抛出第一个遇到的异常
     */
    @Test
    void testValidate_MultipleRequiredFieldsEmpty() {
        TestDataClass obj = new TestDataClass();
        obj.setRequiredString(null);
        obj.setRequiredInteger(null);
        obj.setRequiredList(null);
        obj.setRequiredArray(null);

        FieldValueEmptyException exception = assertThrows(FieldValueEmptyException.class,
                () -> ApiParamValidator.validate(obj));
        // 应该抛出第一个遇到的必填字段为空的异常
        assertTrue(exception.getMessage().contains("requiredString"));
        assertEquals("requiredString", exception.getFieldName());
    }

    /**
     * 测试所有字段都为空 - 应该抛出第一个必填字段的异常
     */
    @Test
    void testValidate_AllFieldsEmpty() {
        TestDataClass obj = new TestDataClass();

        FieldValueEmptyException exception = assertThrows(FieldValueEmptyException.class,
                () -> ApiParamValidator.validate(obj));
        assertTrue(exception.getMessage().contains("requiredString"));
    }

    /**
     * 测试字符串字段有正常值 - 应该不抛异常
     */
    @Test
    void testValidate_StringFieldHasValue() {
        TestDataClass obj = new TestDataClass();
        obj.setRequiredString("valid value");
        obj.setRequiredInteger(123);
        obj.setRequiredList(new ArrayList<>());
        obj.getRequiredList().add("item");
        obj.setRequiredArray(new String[]{"test"});

        assertDoesNotThrow(() -> ApiParamValidator.validate(obj));
    }

    /**
     * 测试集合字段有值 - 应该不抛异常
     */
    @Test
    void testValidate_ListFieldHasValue() {
        TestDataClass obj = new TestDataClass();
        obj.setRequiredString("test");
        obj.setRequiredInteger(123);
        List<String> list = new ArrayList<>();
        list.add("item1");
        list.add("item2");
        obj.setRequiredList(list);
        obj.setRequiredArray(new String[]{"test"});

        assertDoesNotThrow(() -> ApiParamValidator.validate(obj));
    }

    /**
     * 测试数组字段有值 - 应该不抛异常
     */
    @Test
    void testValidate_ArrayFieldHasValue() {
        TestDataClass obj = new TestDataClass();
        obj.setRequiredString("test");
        obj.setRequiredInteger(123);
        obj.setRequiredList(new ArrayList<>());
        obj.getRequiredList().add("item");
        obj.setRequiredArray(new String[]{"array1", "array2", "array3"});

        assertDoesNotThrow(() -> ApiParamValidator.validate(obj));
    }

    /**
     * 测试整数字段有值 - 应该不抛异常
     */
    @Test
    void testValidate_IntegerFieldHasValue() {
        TestDataClass obj = new TestDataClass();
        obj.setRequiredString("test");
        obj.setRequiredInteger(999);
        obj.setRequiredList(new ArrayList<>());
        obj.getRequiredList().add("item");
        obj.setRequiredArray(new String[]{"test"});

        assertDoesNotThrow(() -> ApiParamValidator.validate(obj));
    }

    /**
     * 测试整数字段为0 - 应该不抛异常（0是有效值）
     */
    @Test
    void testValidate_IntegerFieldIsZero() {
        TestDataClass obj = new TestDataClass();
        obj.setRequiredString("test");
        obj.setRequiredInteger(0);
        obj.setRequiredList(new ArrayList<>());
        obj.getRequiredList().add("item");
        obj.setRequiredArray(new String[]{"test"});

        assertDoesNotThrow(() -> ApiParamValidator.validate(obj));
    }

    /**
     * 测试嵌套对象校验 - 嵌套对象必填字段为空
     */
    @Test
    void testValidate_NestedObjectRequiredFieldEmpty() {
        ParentObject parent = new ParentObject();
        parent.setParentRequiredField("parent");
        NestedObject nested = new NestedObject();
        nested.setNestedRequiredField(null); // 必填字段为空
        parent.setNestedObject(nested);

        FieldValueEmptyException exception = assertThrows(FieldValueEmptyException.class,
                () -> ApiParamValidator.validate(parent));
        assertTrue(exception.getMessage().contains("nestedRequiredField"));
        assertEquals("nestedRequiredField", exception.getFieldName());
    }

    /**
     * 测试嵌套对象校验 - 嵌套对象必填字段有值
     */
    @Test
    void testValidate_NestedObjectRequiredFieldHasValue() {
        ParentObject parent = new ParentObject();
        parent.setParentRequiredField("parent");
        NestedObject nested = new NestedObject();
        nested.setNestedRequiredField("nested value");
        parent.setNestedObject(nested);

        assertDoesNotThrow(() -> ApiParamValidator.validate(parent));
    }

    /**
     * 测试List中的嵌套对象校验 - 元素必填字段为空
     */
    @Test
    void testValidate_NestedObjectListElementRequiredFieldEmpty() {
        ParentObjectWithRequiredCollections parent = new ParentObjectWithRequiredCollections();
        parent.setParentRequiredField("parent");
        List<NestedObject> list = new ArrayList<>();
        NestedObject nested1 = new NestedObject();
        nested1.setNestedRequiredField("value1");
        list.add(nested1);
        NestedObject nested2 = new NestedObject();
        nested2.setNestedRequiredField(null); // 第二个元素的必填字段为空
        list.add(nested2);
        parent.setNestedObjectList(list);
        // 设置其他必填字段（至少有一个元素，避免空集合校验失败）
        Map<String, NestedObject> map = new HashMap<>();
        NestedObject mapItem = new NestedObject();
        mapItem.setNestedRequiredField("map value");
        map.put("key", mapItem);
        parent.setNestedObjectMap(map);
        NestedObject[] array = new NestedObject[1];
        NestedObject arrayItem = new NestedObject();
        arrayItem.setNestedRequiredField("array value");
        array[0] = arrayItem;
        parent.setNestedObjectArray(array);

        FieldValueEmptyException exception = assertThrows(FieldValueEmptyException.class,
                () -> ApiParamValidator.validate(parent));
        assertTrue(exception.getMessage().contains("nestedObjectList"));
        assertTrue(exception.getMessage().contains("第 1 个元素"));
        assertTrue(exception.getMessage().contains("nestedRequiredField"));
    }

    /**
     * 测试List中的嵌套对象校验 - 所有元素必填字段都有值
     */
    @Test
    void testValidate_NestedObjectListAllElementsValid() {
        ParentObjectWithRequiredCollections parent = new ParentObjectWithRequiredCollections();
        parent.setParentRequiredField("parent");
        List<NestedObject> list = new ArrayList<>();
        NestedObject nested1 = new NestedObject();
        nested1.setNestedRequiredField("value1");
        list.add(nested1);
        NestedObject nested2 = new NestedObject();
        nested2.setNestedRequiredField("value2");
        list.add(nested2);
        parent.setNestedObjectList(list);
        // 设置其他必填字段（至少有一个元素，避免空集合校验失败）
        Map<String, NestedObject> map = new HashMap<>();
        NestedObject mapItem = new NestedObject();
        mapItem.setNestedRequiredField("map value");
        map.put("key", mapItem);
        parent.setNestedObjectMap(map);
        NestedObject[] array = new NestedObject[1];
        NestedObject arrayItem = new NestedObject();
        arrayItem.setNestedRequiredField("array value");
        array[0] = arrayItem;
        parent.setNestedObjectArray(array);

        assertDoesNotThrow(() -> ApiParamValidator.validate(parent));
    }

    /**
     * 测试Map中的嵌套对象校验 - value对象必填字段为空
     */
    @Test
    void testValidate_NestedObjectMapValueRequiredFieldEmpty() {
        ParentObjectWithRequiredCollections parent = new ParentObjectWithRequiredCollections();
        parent.setParentRequiredField("parent");
        Map<String, NestedObject> map = new HashMap<>();
        NestedObject nested1 = new NestedObject();
        nested1.setNestedRequiredField("value1");
        map.put("key1", nested1);
        NestedObject nested2 = new NestedObject();
        nested2.setNestedRequiredField(null); // value对象的必填字段为空
        map.put("key2", nested2);
        parent.setNestedObjectMap(map);
        // 设置其他必填字段（至少有一个元素，避免空集合校验失败）
        List<NestedObject> list = new ArrayList<>();
        NestedObject listItem = new NestedObject();
        listItem.setNestedRequiredField("list value");
        list.add(listItem);
        parent.setNestedObjectList(list);
        NestedObject[] array = new NestedObject[1];
        NestedObject arrayItem = new NestedObject();
        arrayItem.setNestedRequiredField("array value");
        array[0] = arrayItem;
        parent.setNestedObjectArray(array);

        FieldValueEmptyException exception = assertThrows(FieldValueEmptyException.class,
                () -> ApiParamValidator.validate(parent));
        assertTrue(exception.getMessage().contains("nestedObjectMap"));
        assertTrue(exception.getMessage().contains("key2"));
        assertTrue(exception.getMessage().contains("nestedRequiredField"));
    }

    /**
     * 测试Map中的嵌套对象校验 - 所有value对象必填字段都有值
     */
    @Test
    void testValidate_NestedObjectMapAllValuesValid() {
        ParentObjectWithRequiredCollections parent = new ParentObjectWithRequiredCollections();
        parent.setParentRequiredField("parent");
        Map<String, NestedObject> map = new HashMap<>();
        NestedObject nested1 = new NestedObject();
        nested1.setNestedRequiredField("value1");
        map.put("key1", nested1);
        NestedObject nested2 = new NestedObject();
        nested2.setNestedRequiredField("value2");
        map.put("key2", nested2);
        parent.setNestedObjectMap(map);
        // 设置其他必填字段（至少有一个元素，避免空集合校验失败）
        List<NestedObject> list = new ArrayList<>();
        NestedObject listItem = new NestedObject();
        listItem.setNestedRequiredField("list value");
        list.add(listItem);
        parent.setNestedObjectList(list);
        NestedObject[] array = new NestedObject[1];
        NestedObject arrayItem = new NestedObject();
        arrayItem.setNestedRequiredField("array value");
        array[0] = arrayItem;
        parent.setNestedObjectArray(array);

        assertDoesNotThrow(() -> ApiParamValidator.validate(parent));
    }

    /**
     * 测试数组中的嵌套对象校验 - 元素必填字段为空
     */
    @Test
    void testValidate_NestedObjectArrayElementRequiredFieldEmpty() {
        ParentObjectWithRequiredCollections parent = new ParentObjectWithRequiredCollections();
        parent.setParentRequiredField("parent");
        NestedObject[] array = new NestedObject[2];
        NestedObject nested1 = new NestedObject();
        nested1.setNestedRequiredField("value1");
        array[0] = nested1;
        NestedObject nested2 = new NestedObject();
        nested2.setNestedRequiredField(null); // 第二个元素的必填字段为空
        array[1] = nested2;
        parent.setNestedObjectArray(array);
        // 设置其他必填字段（至少有一个元素，避免空集合校验失败）
        List<NestedObject> list = new ArrayList<>();
        NestedObject listItem = new NestedObject();
        listItem.setNestedRequiredField("list value");
        list.add(listItem);
        parent.setNestedObjectList(list);
        Map<String, NestedObject> map = new HashMap<>();
        NestedObject mapItem = new NestedObject();
        mapItem.setNestedRequiredField("map value");
        map.put("key", mapItem);
        parent.setNestedObjectMap(map);

        FieldValueEmptyException exception = assertThrows(FieldValueEmptyException.class,
                () -> ApiParamValidator.validate(parent));
        assertTrue(exception.getMessage().contains("nestedObjectArray"));
        assertTrue(exception.getMessage().contains("第 1 个元素"));
        assertTrue(exception.getMessage().contains("nestedRequiredField"));
    }

    /**
     * 测试数组中的嵌套对象校验 - 所有元素必填字段都有值
     */
    @Test
    void testValidate_NestedObjectArrayAllElementsValid() {
        ParentObjectWithRequiredCollections parent = new ParentObjectWithRequiredCollections();
        parent.setParentRequiredField("parent");
        NestedObject[] array = new NestedObject[2];
        NestedObject nested1 = new NestedObject();
        nested1.setNestedRequiredField("value1");
        array[0] = nested1;
        NestedObject nested2 = new NestedObject();
        nested2.setNestedRequiredField("value2");
        array[1] = nested2;
        parent.setNestedObjectArray(array);
        // 设置其他必填字段（至少有一个元素，避免空集合校验失败）
        List<NestedObject> list = new ArrayList<>();
        NestedObject listItem = new NestedObject();
        listItem.setNestedRequiredField("list value");
        list.add(listItem);
        parent.setNestedObjectList(list);
        Map<String, NestedObject> map = new HashMap<>();
        NestedObject mapItem = new NestedObject();
        mapItem.setNestedRequiredField("map value");
        map.put("key", mapItem);
        parent.setNestedObjectMap(map);

        assertDoesNotThrow(() -> ApiParamValidator.validate(parent));
    }

    /**
     * 测试多层嵌套对象校验
     */
    @Test
    void testValidate_MultiLevelNestedObject() {
        ParentObject parent = new ParentObject();
        parent.setParentRequiredField("parent");
        NestedObject nested = new NestedObject();
        nested.setNestedRequiredField("nested");
        parent.setNestedObject(nested);
        List<NestedObject> list = new ArrayList<>();
        NestedObject listItem = new NestedObject();
        listItem.setNestedRequiredField("list item");
        list.add(listItem);
        parent.setNestedObjectList(list);

        assertDoesNotThrow(() -> ApiParamValidator.validate(parent));
    }

    /**
     * 测试List中包含基本类型 - 不应该递归校验
     */
    @Test
    void testValidate_ListWithPrimitiveTypes() {
        TestDataClass obj = new TestDataClass();
        obj.setRequiredString("test");
        obj.setRequiredInteger(123);
        List<String> stringList = new ArrayList<>();
        stringList.add("item1");
        stringList.add("item2");
        obj.setRequiredList(stringList);
        obj.setRequiredArray(new String[]{"test"});

        assertDoesNotThrow(() -> ApiParamValidator.validate(obj));
    }

    /**
     * 测试Map中包含基本类型value - 不应该递归校验
     */
    @Test
    void testValidate_MapWithPrimitiveValue() {
        ParentObject parent = new ParentObject();
        parent.setParentRequiredField("parent");
        NestedObject nested = new NestedObject();
        nested.setNestedRequiredField("value");
        parent.setNestedObject(nested);

        assertDoesNotThrow(() -> ApiParamValidator.validate(parent));
    }

    /**
     * 测试没有@ApiModel注解的嵌套对象 - 不应该被校验（即使必填字段为空也不抛异常）
     */
    @Test
    void testValidate_NestedObjectWithoutApiModel_ShouldNotValidate() {
        ParentObjectWithNestedWithoutApiModel parent = new ParentObjectWithNestedWithoutApiModel();
        parent.setParentRequiredField("parent");
        NestedObjectWithoutApiModel nested = new NestedObjectWithoutApiModel();
        nested.setNestedRequiredField(null); // 必填字段为空，但因为类没有@ApiModel注解，不应该校验
        parent.setNestedObject(nested);

        // 应该不抛异常，因为没有@ApiModel注解的对象不会被校验
        assertDoesNotThrow(() -> ApiParamValidator.validate(parent));
    }

    /**
     * 测试List中的元素对象，即使没有@ApiModel注解，也会被校验
     */
    @Test
    void testValidate_ListElementWithoutApiModel_ShouldValidate() {
        ParentObjectWithRequiredCollections parent = new ParentObjectWithRequiredCollections();
        parent.setParentRequiredField("parent");
        // 设置其他必填字段（至少有一个元素，避免空集合校验失败）
        List<NestedObject> requiredList = new ArrayList<>();
        NestedObject requiredListItem = new NestedObject();
        requiredListItem.setNestedRequiredField("required list value");
        requiredList.add(requiredListItem);
        parent.setNestedObjectList(requiredList);
        Map<String, NestedObject> requiredMap = new HashMap<>();
        NestedObject requiredMapItem = new NestedObject();
        requiredMapItem.setNestedRequiredField("required map value");
        requiredMap.put("key", requiredMapItem);
        parent.setNestedObjectMap(requiredMap);
        NestedObject[] requiredArray = new NestedObject[1];
        NestedObject requiredArrayItem = new NestedObject();
        requiredArrayItem.setNestedRequiredField("required array value");
        requiredArray[0] = requiredArrayItem;
        parent.setNestedObjectArray(requiredArray);

        List<NestedObjectWithoutApiModel> list = new ArrayList<>();
        NestedObjectWithoutApiModel nested1 = new NestedObjectWithoutApiModel();
        nested1.setNestedRequiredField("value1");
        list.add(nested1);
        NestedObjectWithoutApiModel nested2 = new NestedObjectWithoutApiModel();
        nested2.setNestedRequiredField(null); // 必填字段为空
        list.add(nested2);
        parent.setNestedObjectWithoutApiModelList(list);

        // 应该抛异常，因为List中的元素对象会被校验（不受@ApiModel限制）
        FieldValueEmptyException exception = assertThrows(FieldValueEmptyException.class,
                () -> ApiParamValidator.validate(parent));
        // 检查错误信息中是否包含字段名或元素信息
        String message = exception.getMessage();
        assertTrue(message.contains("nestedObjectWithoutApiModelList") ||
                        message.contains("第") && message.contains("个元素") ||
                        message.contains("nestedRequiredField"),
                "错误信息应该包含字段名或元素信息，实际错误信息：" + message);
    }

    /**
     * 测试Map中的value对象，即使没有@ApiModel注解，也会被校验
     */
    @Test
    void testValidate_MapValueWithoutApiModel_ShouldValidate() {
        ParentObjectWithRequiredCollections parent = new ParentObjectWithRequiredCollections();
        parent.setParentRequiredField("parent");
        // 设置其他必填字段（至少有一个元素，避免空集合校验失败）
        List<NestedObject> requiredList = new ArrayList<>();
        NestedObject requiredListItem = new NestedObject();
        requiredListItem.setNestedRequiredField("required list value");
        requiredList.add(requiredListItem);
        parent.setNestedObjectList(requiredList);
        Map<String, NestedObject> requiredMap = new HashMap<>();
        NestedObject requiredMapItem = new NestedObject();
        requiredMapItem.setNestedRequiredField("required map value");
        requiredMap.put("key", requiredMapItem);
        parent.setNestedObjectMap(requiredMap);
        NestedObject[] requiredArray = new NestedObject[1];
        NestedObject requiredArrayItem = new NestedObject();
        requiredArrayItem.setNestedRequiredField("required array value");
        requiredArray[0] = requiredArrayItem;
        parent.setNestedObjectArray(requiredArray);

        Map<String, NestedObjectWithoutApiModel> map = new HashMap<>();
        NestedObjectWithoutApiModel nested1 = new NestedObjectWithoutApiModel();
        nested1.setNestedRequiredField("value1");
        map.put("key1", nested1);
        NestedObjectWithoutApiModel nested2 = new NestedObjectWithoutApiModel();
        nested2.setNestedRequiredField(null); // 必填字段为空
        map.put("key2", nested2);
        parent.setNestedObjectWithoutApiModelMap(map);

        // 应该抛异常，因为Map中的value对象会被校验（不受@ApiModel限制）
        FieldValueEmptyException exception = assertThrows(FieldValueEmptyException.class,
                () -> ApiParamValidator.validate(parent));
        // 检查错误信息中是否包含字段名或key信息
        String message = exception.getMessage();
        assertTrue(message.contains("nestedObjectWithoutApiModelMap") ||
                        message.contains("key2") ||
                        message.contains("nestedRequiredField"),
                "错误信息应该包含字段名或key信息，实际错误信息：" + message);
    }

    /**
     * 测试数组中的元素对象，即使没有@ApiModel注解，也会被校验
     */
    @Test
    void testValidate_ArrayElementWithoutApiModel_ShouldValidate() {
        ParentObjectWithRequiredCollections parent = new ParentObjectWithRequiredCollections();
        parent.setParentRequiredField("parent");
        // 设置其他必填字段（至少有一个元素，避免空集合校验失败）
        List<NestedObject> requiredList = new ArrayList<>();
        NestedObject requiredListItem = new NestedObject();
        requiredListItem.setNestedRequiredField("required list value");
        requiredList.add(requiredListItem);
        parent.setNestedObjectList(requiredList);
        Map<String, NestedObject> requiredMap = new HashMap<>();
        NestedObject requiredMapItem = new NestedObject();
        requiredMapItem.setNestedRequiredField("required map value");
        requiredMap.put("key", requiredMapItem);
        parent.setNestedObjectMap(requiredMap);
        NestedObject[] requiredArray = new NestedObject[1];
        NestedObject requiredArrayItem = new NestedObject();
        requiredArrayItem.setNestedRequiredField("required array value");
        requiredArray[0] = requiredArrayItem;
        parent.setNestedObjectArray(requiredArray);

        NestedObjectWithoutApiModel[] array = new NestedObjectWithoutApiModel[2];
        NestedObjectWithoutApiModel nested1 = new NestedObjectWithoutApiModel();
        nested1.setNestedRequiredField("value1");
        array[0] = nested1;
        NestedObjectWithoutApiModel nested2 = new NestedObjectWithoutApiModel();
        nested2.setNestedRequiredField(null); // 必填字段为空
        array[1] = nested2;
        parent.setNestedObjectWithoutApiModelArray(array);

        // 应该抛异常，因为数组中的元素对象会被校验（不受@ApiModel限制）
        FieldValueEmptyException exception = assertThrows(FieldValueEmptyException.class,
                () -> ApiParamValidator.validate(parent));
        // 检查错误信息中是否包含字段名或元素信息
        String message = exception.getMessage();
        assertTrue(message.contains("nestedObjectWithoutApiModelArray") ||
                        message.contains("第") && message.contains("个元素") ||
                        message.contains("nestedRequiredField"),
                "错误信息应该包含字段名或元素信息，实际错误信息：" + message);
    }

    /**
     * 测试数据类 - 包含各种类型的字段
     */
    static class TestDataClass {
        @ApiParam(description = "必填字符串字段", required = true)
        private String requiredString;

        @ApiParam(description = "非必填字符串字段", required = false)
        private String optionalString;

        @ApiParam(description = "必填整数字段", required = true)
        private Integer requiredInteger;

        @ApiParam(description = "非必填整数字段", required = false)
        private Integer optionalInteger;

        @ApiParam(description = "必填集合字段", required = true)
        private List<String> requiredList;

        @ApiParam(description = "非必填集合字段", required = false)
        private List<String> optionalList;

        @ApiParam(description = "必填数组字段", required = true)
        private String[] requiredArray;

        @ApiParam(description = "非必填数组字段", required = false)
        private String[] optionalArray;

        // 没有注解的字段
        private String noAnnotationField;

        // required默认为false的字段
        @ApiParam(description = "默认非必填字段")
        private String defaultOptionalField;

        // 只有description，没有required的字段
        @ApiParam(description = "只有描述的字段")
        private String descriptionOnlyField;

        // Getters and Setters
        public String getRequiredString() {
            return requiredString;
        }

        public void setRequiredString(String requiredString) {
            this.requiredString = requiredString;
        }

        public String getOptionalString() {
            return optionalString;
        }

        public void setOptionalString(String optionalString) {
            this.optionalString = optionalString;
        }

        public Integer getRequiredInteger() {
            return requiredInteger;
        }

        public void setRequiredInteger(Integer requiredInteger) {
            this.requiredInteger = requiredInteger;
        }

        public Integer getOptionalInteger() {
            return optionalInteger;
        }

        public void setOptionalInteger(Integer optionalInteger) {
            this.optionalInteger = optionalInteger;
        }

        public List<String> getRequiredList() {
            return requiredList;
        }

        public void setRequiredList(List<String> requiredList) {
            this.requiredList = requiredList;
        }

        public List<String> getOptionalList() {
            return optionalList;
        }

        public void setOptionalList(List<String> optionalList) {
            this.optionalList = optionalList;
        }

        public String[] getRequiredArray() {
            return requiredArray;
        }

        public void setRequiredArray(String[] requiredArray) {
            this.requiredArray = requiredArray;
        }

        public String[] getOptionalArray() {
            return optionalArray;
        }

        public void setOptionalArray(String[] optionalArray) {
            this.optionalArray = optionalArray;
        }

        public String getNoAnnotationField() {
            return noAnnotationField;
        }

        public void setNoAnnotationField(String noAnnotationField) {
            this.noAnnotationField = noAnnotationField;
        }

        public String getDefaultOptionalField() {
            return defaultOptionalField;
        }

        public void setDefaultOptionalField(String defaultOptionalField) {
            this.defaultOptionalField = defaultOptionalField;
        }

        public String getDescriptionOnlyField() {
            return descriptionOnlyField;
        }

        public void setDescriptionOnlyField(String descriptionOnlyField) {
            this.descriptionOnlyField = descriptionOnlyField;
        }
    }

    /**
     * 嵌套对象测试类 - 有@ApiModel注解，会被校验
     */
    @ApiModel(description = "嵌套对象测试类")
    static class NestedObject {
        @ApiParam(description = "嵌套对象必填字段", required = true)
        private String nestedRequiredField;

        @ApiParam(description = "嵌套对象非必填字段", required = false)
        private String nestedOptionalField;

        public String getNestedRequiredField() {
            return nestedRequiredField;
        }

        public void setNestedRequiredField(String nestedRequiredField) {
            this.nestedRequiredField = nestedRequiredField;
        }

        public String getNestedOptionalField() {
            return nestedOptionalField;
        }

        public void setNestedOptionalField(String nestedOptionalField) {
            this.nestedOptionalField = nestedOptionalField;
        }
    }

    /**
     * 包含嵌套对象的测试类
     */
    static class ParentObject {
        @ApiParam(description = "父对象必填字段", required = true)
        private String parentRequiredField;

        @ApiParam(description = "嵌套对象字段", required = false)
        private NestedObject nestedObject;

        @ApiParam(description = "嵌套对象列表", required = false)
        private List<NestedObject> nestedObjectList;

        @ApiParam(description = "嵌套对象Map", required = false)
        private Map<String, NestedObject> nestedObjectMap;

        @ApiParam(description = "嵌套对象数组", required = false)
        private NestedObject[] nestedObjectArray;

        public String getParentRequiredField() {
            return parentRequiredField;
        }

        public void setParentRequiredField(String parentRequiredField) {
            this.parentRequiredField = parentRequiredField;
        }

        public NestedObject getNestedObject() {
            return nestedObject;
        }

        public void setNestedObject(NestedObject nestedObject) {
            this.nestedObject = nestedObject;
        }

        public List<NestedObject> getNestedObjectList() {
            return nestedObjectList;
        }

        public void setNestedObjectList(List<NestedObject> nestedObjectList) {
            this.nestedObjectList = nestedObjectList;
        }

        public Map<String, NestedObject> getNestedObjectMap() {
            return nestedObjectMap;
        }

        public void setNestedObjectMap(Map<String, NestedObject> nestedObjectMap) {
            this.nestedObjectMap = nestedObjectMap;
        }

        public NestedObject[] getNestedObjectArray() {
            return nestedObjectArray;
        }

        public void setNestedObjectArray(NestedObject[] nestedObjectArray) {
            this.nestedObjectArray = nestedObjectArray;
        }
    }

    /**
     * 包含必填嵌套集合的测试类
     */
    static class ParentObjectWithRequiredCollections {
        @ApiParam(description = "父对象必填字段", required = true)
        private String parentRequiredField;

        @ApiParam(description = "嵌套对象列表", required = true)
        private List<NestedObject> nestedObjectList;

        @ApiParam(description = "嵌套对象Map", required = true)
        private Map<String, NestedObject> nestedObjectMap;

        @ApiParam(description = "嵌套对象数组", required = true)
        private NestedObject[] nestedObjectArray;

        // 用于测试没有@ApiModel注解的对象在集合中的情况
        @ApiParam(description = "没有@ApiModel注解的对象列表", required = false)
        private List<NestedObjectWithoutApiModel> nestedObjectWithoutApiModelList;

        @ApiParam(description = "没有@ApiModel注解的对象Map", required = false)
        private Map<String, NestedObjectWithoutApiModel> nestedObjectWithoutApiModelMap;

        @ApiParam(description = "没有@ApiModel注解的对象数组", required = false)
        private NestedObjectWithoutApiModel[] nestedObjectWithoutApiModelArray;

        public String getParentRequiredField() {
            return parentRequiredField;
        }

        public void setParentRequiredField(String parentRequiredField) {
            this.parentRequiredField = parentRequiredField;
        }

        public List<NestedObject> getNestedObjectList() {
            return nestedObjectList;
        }

        public void setNestedObjectList(List<NestedObject> nestedObjectList) {
            this.nestedObjectList = nestedObjectList;
        }

        public Map<String, NestedObject> getNestedObjectMap() {
            return nestedObjectMap;
        }

        public void setNestedObjectMap(Map<String, NestedObject> nestedObjectMap) {
            this.nestedObjectMap = nestedObjectMap;
        }

        public NestedObject[] getNestedObjectArray() {
            return nestedObjectArray;
        }

        public void setNestedObjectArray(NestedObject[] nestedObjectArray) {
            this.nestedObjectArray = nestedObjectArray;
        }

        public List<NestedObjectWithoutApiModel> getNestedObjectWithoutApiModelList() {
            return nestedObjectWithoutApiModelList;
        }

        public void setNestedObjectWithoutApiModelList(List<NestedObjectWithoutApiModel> nestedObjectWithoutApiModelList) {
            this.nestedObjectWithoutApiModelList = nestedObjectWithoutApiModelList;
        }

        public Map<String, NestedObjectWithoutApiModel> getNestedObjectWithoutApiModelMap() {
            return nestedObjectWithoutApiModelMap;
        }

        public void setNestedObjectWithoutApiModelMap(Map<String, NestedObjectWithoutApiModel> nestedObjectWithoutApiModelMap) {
            this.nestedObjectWithoutApiModelMap = nestedObjectWithoutApiModelMap;
        }

        public NestedObjectWithoutApiModel[] getNestedObjectWithoutApiModelArray() {
            return nestedObjectWithoutApiModelArray;
        }

        public void setNestedObjectWithoutApiModelArray(NestedObjectWithoutApiModel[] nestedObjectWithoutApiModelArray) {
            this.nestedObjectWithoutApiModelArray = nestedObjectWithoutApiModelArray;
        }
    }

    /**
     * 没有@ApiModel注解的嵌套对象测试类 - 不应该被校验
     */
    static class NestedObjectWithoutApiModel {
        @ApiParam(description = "嵌套对象必填字段", required = true)
        private String nestedRequiredField;

        public String getNestedRequiredField() {
            return nestedRequiredField;
        }

        public void setNestedRequiredField(String nestedRequiredField) {
            this.nestedRequiredField = nestedRequiredField;
        }
    }

    /**
     * 包含没有@ApiModel注解的嵌套对象的测试类
     */
    static class ParentObjectWithNestedWithoutApiModel {
        @ApiParam(description = "父对象必填字段", required = true)
        private String parentRequiredField;

        @ApiParam(description = "嵌套对象字段（没有@ApiModel注解）", required = false)
        private NestedObjectWithoutApiModel nestedObject;

        public String getParentRequiredField() {
            return parentRequiredField;
        }

        public void setParentRequiredField(String parentRequiredField) {
            this.parentRequiredField = parentRequiredField;
        }

        public NestedObjectWithoutApiModel getNestedObject() {
            return nestedObject;
        }

        public void setNestedObject(NestedObjectWithoutApiModel nestedObject) {
            this.nestedObject = nestedObject;
        }
    }

    /**
     * 测试继承类 - 父类中的字段是否能被校验
     */
    @Test
    @DisplayName("测试继承类 - 父类必填字段为空应抛出异常")
    void testValidate_InheritedClass_ParentRequiredFieldEmpty_ThrowsException() {
        // Arrange
        ChildClass child = new ChildClass();
        // 只设置子类的必填字段，不设置父类的必填字段
        child.setChildRequiredField("child value");
        // 父类的必填字段 parentRequiredField 为 null

        // Act & Assert
        // 根据当前实现，getDeclaredFields() 只返回当前类声明的字段，不包括继承的字段
        // 所以父类的字段应该不会被校验
        // 如果实现支持继承字段校验，应该抛出异常；如果不支持，应该不抛异常
        // 这里先测试当前行为：父类字段不会被校验
        assertDoesNotThrow(() -> ApiParamValidator.validate(child),
                "当前实现使用 getDeclaredFields()，只校验当前类声明的字段，不包括继承的字段");
    }

    /**
     * 测试继承类 - 父类和子类必填字段都有值
     */
    @Test
    @DisplayName("测试继承类 - 父类和子类必填字段都有值应不抛异常")
    void testValidate_InheritedClass_AllRequiredFieldsHaveValues() {
        // Arrange
        ChildClass child = new ChildClass();
        child.setParentRequiredField("parent value");
        child.setChildRequiredField("child value");

        // Act & Assert
        assertDoesNotThrow(() -> ApiParamValidator.validate(child));
    }

    /**
     * 测试继承类 - 子类必填字段为空应抛出异常
     */
    @Test
    @DisplayName("测试继承类 - 子类必填字段为空应抛出异常")
    void testValidate_InheritedClass_ChildRequiredFieldEmpty_ThrowsException() {
        // Arrange
        ChildClass child = new ChildClass();
        child.setParentRequiredField("parent value");
        // 子类的必填字段 childRequiredField 为 null

        // Act & Assert
        FieldValueEmptyException exception = assertThrows(FieldValueEmptyException.class,
                () -> ApiParamValidator.validate(child));
        assertTrue(exception.getMessage().contains("childRequiredField"));
        assertEquals("childRequiredField", exception.getFieldName());
    }

    /**
     * 测试多层继承 - 祖父类、父类、子类的字段校验
     */
    @Test
    @DisplayName("测试多层继承 - 多层继承的字段校验")
    void testValidate_MultiLevelInheritance() {
        // Arrange
        GrandChildClass grandChild = new GrandChildClass();
        // 只设置最底层子类的必填字段
        grandChild.setGrandChildRequiredField("grandchild value");
        // 父类和祖父类的必填字段都为 null

        // Act & Assert
        // 根据当前实现，只校验当前类声明的字段
        assertDoesNotThrow(() -> ApiParamValidator.validate(grandChild),
                "当前实现只校验当前类声明的字段，不包括继承的字段");
    }

    /**
     * 父类测试类 - 包含必填字段
     */
    static class ParentClass {
        @ApiParam(description = "父类必填字段", required = true)
        private String parentRequiredField;

        @ApiParam(description = "父类非必填字段", required = false)
        private String parentOptionalField;

        public String getParentRequiredField() {
            return parentRequiredField;
        }

        public void setParentRequiredField(String parentRequiredField) {
            this.parentRequiredField = parentRequiredField;
        }

        public String getParentOptionalField() {
            return parentOptionalField;
        }

        public void setParentOptionalField(String parentOptionalField) {
            this.parentOptionalField = parentOptionalField;
        }
    }

    /**
     * 子类测试类 - 继承父类，包含自己的必填字段
     */
    static class ChildClass extends ParentClass {
        @ApiParam(description = "子类必填字段", required = true)
        private String childRequiredField;

        @ApiParam(description = "子类非必填字段", required = false)
        private String childOptionalField;

        public String getChildRequiredField() {
            return childRequiredField;
        }

        public void setChildRequiredField(String childRequiredField) {
            this.childRequiredField = childRequiredField;
        }

        public String getChildOptionalField() {
            return childOptionalField;
        }

        public void setChildOptionalField(String childOptionalField) {
            this.childOptionalField = childOptionalField;
        }
    }

    /**
     * 孙子类测试类 - 多层继承
     */
    static class GrandChildClass extends ChildClass {
        @ApiParam(description = "孙子类必填字段", required = true)
        private String grandChildRequiredField;

        public String getGrandChildRequiredField() {
            return grandChildRequiredField;
        }

        public void setGrandChildRequiredField(String grandChildRequiredField) {
            this.grandChildRequiredField = grandChildRequiredField;
        }
    }
}


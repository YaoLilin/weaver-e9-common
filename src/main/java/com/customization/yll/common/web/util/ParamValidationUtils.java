package com.customization.yll.common.web.util;

import com.customization.yll.common.web.exception.WebParamException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Api 参数校验工具类
 *
 * @author 姚礼林
 */
@UtilityClass
public class ParamValidationUtils {
    /**
     * 验证 API 参数，如果校验不通过则抛出异常
     *
     * @param object API BODY 参数对象
     * @throws WebParamException.BodyParamException 校验不通过时的异常
     */
    public static <T> void webParamValidate(T object) throws WebParamException.BodyParamException {
        List<String> result = validate(object);
        if (!result.isEmpty()) {
            throw new WebParamException.BodyParamException(String.join("; ", result));
        }
    }

    /**
     * 验证对象并返回所有错误信息
     */
    public static <T> List<String> validate(T object) {
        Set<ConstraintViolation<T>> violations = ValidatorInstance.INSTANCE.validator.validate(object);
        return violations.stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.toList());
    }

    /**
     * 验证对象，如果失败抛出异常
     */
    public static <T> void validateAndThrow(T object) {
        Set<ConstraintViolation<T>> violations = ValidatorInstance.INSTANCE.validator.validate(object);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new IllegalArgumentException("验证失败: " + message);
        }
    }

    /**
     * 验证对象的特定属性
     */
    public static <T> List<String> validateProperty(T object, String propertyName) {
        Set<ConstraintViolation<T>> violations =
                ValidatorInstance.INSTANCE.validator.validateProperty(object, propertyName);
        return violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
    }

    private enum ValidatorInstance {
        /**
         * 枚举实例
         */
        INSTANCE();
        private final Validator validator;

        ValidatorInstance() {
            validator = Validation.buildDefaultValidatorFactory().getValidator();
        }
    }
}

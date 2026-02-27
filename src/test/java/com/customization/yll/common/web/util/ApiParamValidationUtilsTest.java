package com.customization.yll.common.web.util;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author 姚礼林
 * @desc ApiParamValidationUtils 测试类
 * @date 2025/12/19
 **/
class ApiParamValidationUtilsTest {

    @Test
    void validate() {
        TestBean testBean = new TestBean();
        testBean.setName("");
        testBean.setAge(null);

        InnerBean innerBean = new InnerBean();
        innerBean.setAccountId("");
        innerBean.setEmail("gggg");
        innerBean.setAccountName("222");

        testBean.setInnerBean(innerBean);

        List<String> result = ParamValidationUtils.validate(testBean);
        System.out.println(result);
        assertFalse(result.isEmpty());
        assertTrue(result.contains("innerBean.accountId: 账号ID不能为空"));
    }

    static class TestBean {
        @NotBlank(message = "姓名不能为空")
        private String name;
        @NotNull(message = "年龄不能为空")
        private Integer age;
        @Valid
        private InnerBean innerBean;

        public String getName() {
            return name;
        }

        public void setName(@NotBlank(message = "姓名不能为空") String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(@NotNull(message = "年龄不能为空") Integer age) {
            this.age = age;
        }

        public InnerBean getInnerBean() {
            return innerBean;
        }

        public void setInnerBean(InnerBean innerBean) {
            this.innerBean = innerBean;
        }
    }

    static class InnerBean {
        @NotBlank(message = "账号ID不能为空")
        private String accountId;
        @NotBlank(message = "账号名称不能为空")
        private String accountName;
        @Email(message = "邮箱格式错误")
        private String email;

        public @NotBlank(message = "账号ID不能为空") String getAccountId() {
            return accountId;
        }

        public void setAccountId(@NotBlank(message = "账号ID不能为空") String accountId) {
            this.accountId = accountId;
        }

        public @NotBlank(message = "账号名称不能为空") String getAccountName() {
            return accountName;
        }

        public void setAccountName(@NotBlank(message = "账号名称不能为空") String accountName) {
            this.accountName = accountName;
        }

        public @Email(message = "邮箱格式错误") String getEmail() {
            return email;
        }

        public void setEmail(@Email(message = "邮箱格式错误") String email) {
            this.email = email;
        }
    }
}

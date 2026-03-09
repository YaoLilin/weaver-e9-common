# E9 公共类库说明
此项目为本人自行开发的泛微二次开发公共类库，提供泛微 E9 系统便捷的工具类以及实用组件，可在任意 E9 项目中使用，为开发提供便利。  
欢迎各位泛微二次开发人员提交代码，完善此类库，我希望它能被广泛使用，任何人都可以把工具类和通用的功能代码提交到此类库，
它可以帮助到许多人。

## 为什么我要创建此项目
我的目的是想要泛微二次开发者上传自己的通用功能代码，这样使用此类库的人都能用到

## 提交代码说明
任何人都可以提交代码，代码需符合规范，例如阿里巴巴编码规约，谷歌规范。对于现有的代码不要修改方法的签名，防止破坏正在使用的项目。

## 使用方法
本项目仅包含源码，下载到本地后需自行添加泛微二开依赖。out-dep 目录中为本项目引入的外部依赖，需将这些依赖引入到本地项目中。  

推荐的使用方法：将本项目在你的二次开发项目中作为一个模块，或独立的项目，打包成 jar 包部署到 E9 系统中。
或者你可以直接使用本项目下的 `weaver-liuzhou-seconddev-common.jar` 文件，将它部署到 E9 系统中，将此文件放入
ecology/WEB-INF/lib 目录下即可。

## 主要功能

### 流程

#### AbstractWorkflowAction - Action 抽象类

**路径**：`com.customization.yll.common.workflow.AbstractWorkflowAction`  
**说明**：实现此抽象类可让流程 Action 开发更方便，它提供了更加明确的 Action 执行结果，以及可以方便的获取到流程字段等功能。

**功能**：

- 完善的返回结果，包含是否成功和执行结果信息。  
  在标准的 `Action` 接口的 `execute()` 方法中，返回结果类型是字符串，使用者不知道需要返回什么
  字符串，而在 `AbstractWorkflowAction` 的 `doExecute()` 方法的返回结果是一个明确的 `ActionResult` 对象，
  它包含了布尔型的是否执行成功标识，以及执行结果信息，如果 `Action` 执行失败可通过执行结果信息显示在前端，显示给
  用户。
- 更方便获取流程字段值：通过类中的成员 `WorkflowActionHelper` 可方便获取流程字段值，而不是繁琐的通过
  `RequestInfo` 获取
- 自带日志记录：类中已经创建了集成日志类，当 `Action` 开始执行和执行完成后都会记录到日志，你也可以使用此日志类
- 全局异常处理：当 `Action` 发生异常时会进行捕获，而不是抛出到上层标准类导致无法找到异常信息，捕获异常后会记录到
  集成日志，并且在前端提示错误，显示是哪个 `Action` 出现错误
- 支持异步执行：只需向 `Action` 参数 `async` 传入 1 ，`Action` 就能异步执行
- 参数校验：配合 `@ActionParam` 注解可对 `Action` 参数进行校验，可实现参数必填校验，如果必填的 `Action` 参数
  没有填写值则会提示错误

**使用说明**：  
继承此类，并实现方法，可直接配在流程 Action 中，示例：

```java
public class TestAction3 extends AbstractWorkflowAction {

    /**
     * 传入参数，使用 @ActionParam 进行必填校验
     */
    @ActionParam(required = true, displayName = "参数1", desc = "测试参数")
    private String param1;

    @NotNull
    @Override
    protected ActionResult doExecute(RequestInfo requestInfo) {
        // 记录日志
        this.log.info("开始执行");
        // 获取主表字段
        String number = this.actionHelper.getMainFieldValue("number");
        // 获取明细字段
        List<Map<String, String>> detailFieldValue =
                this.actionHelper.getDetailFieldValue(0, "name", "address");
        // return new ActionResult(false, "失败信息");
        return new ActionResult(true, "成功");
    }
}
```

#### WorkflowCreator - 流程创建

**路径**：`com.customization.yll.common.workflow.WorkflowCreator`  
**说明**：可通过后端创建流程，支持传入主表字段数据，明细表字段数据，创建后可自动提交到下个节点

#### WorkflowApprovalInfoManager - 流程审批意见日志获取

**路径**：`com.customization.yll.common.workflow.WorkflowApprovalInfoManager`  
**说明**：可获取流程所有审批意见日志（对应数据库表 workflow_requestlog），可获取指定节点的日志，
可添加自定义 sql 条件，获取的日志信息包括：logId、operator、nodeId、operateDate、operateTime、remark、destNodeId

#### WorkflowFieldValueManager - 流程字段值获取

**路径**：`com.customization.yll.common.workflow.WorkflowFieldValueManager`  
**说明**：可获取主表和明细表字段值，只需要传入流程请求id和字段id/字段名就能获取到字段值

#### WorkflowFormPdfCreator - 生成流程表单PDF

**路径**：`com.customization.yll.common.workflow.WorkflowFormPdfCreator`  
**说明**：用的是标准功能的流程存为文档功能，可生成流程表单页面的PDF文件，可获取指定节点的页面PDF

#### WorkflowOverTimeCalculator - 流程耗时计算

**路径**：`com.customization.yll.common.workflow.WorkflowOverTimeCalculator`  
**说明**：可计算流程操作耗时，非工作时间不纳入计算。  
**功能**：

- 获取流程接收时间到当前时间的未处理时间秒数
- 获取流程接收时间到指定时间的未处理时间秒数

#### NodeUtil - 流程节点工具类

**路径**：`com.customization.yll.common.workflow.util.NodeUtil`  
**说明**：流程节点工具类  
**功能**：

- 根据节点id获取节点名称
- 根据节点名称和工作流ID获取节点ID
- 根据节点名称获取所有流程版本的节点id

#### WorkflowOperateUtil - 流程操作工具类

#### WorkflowUtil - 流程工具类

### 建模

#### ModeUtil - 建模工具类

#### ModeActionUtil - 建模 Action 工具类

### 文档

#### DocConvertorByWpsApi - 使用 WPS 中台接口对文档进行转换

#### DocFileManager - 文档文件获取

#### DocInsertQrManager - 文档二维码工具

#### FileUploadService - 文件上传

#### WordTextEditService - 文档文字编辑

#### FileConvertUtil - 文件格式转换工具




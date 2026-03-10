# E9 公共类库说明

此项目为本人自行开发的泛微二次开发公共类库，提供泛微 E9 系统便捷的工具类以及实用组件，可在任意 E9 项目中使用，为开发提供便利。
欢迎各位泛微二次开发人员提交代码，完善此类库，我希望它能被广泛使用，任何人都可以把工具类和通用的功能代码提交到此类库，
它可以帮助到许多人。

## 为什么我要创建此项目

我的目的是想要泛微二次开发者上传自己的通用功能代码，这样使用此类库的人都能用到

## 提交代码说明

任何人都可以提交代码，代码需符合规范，例如阿里巴巴编码规约，谷歌规范。对于现有的代码不要修改方法的签名，防止破坏正在使用的项目。

## 使用方法

方法一：直接使用 jar 包，下载 `weaver-liuzhou-seconddev-common.jar` ，将此 jar 包部署到服务器的 `ecology/WEB-INF/lib`
目录  
方法二：源码编译，需要有泛微 E9 开发环境，下载源码并放入你的开发项目，编译后将 class 文件放入你的开发包并部署到服务器中

## E10 二开公共类库

我在 E10 也创建了公共类库，感兴趣可以看看，https://github.com/YaoLilin/weaver-e10-second-dev-common

## 主要功能 🎉

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

**路径**：`com.customization.yll.common.workflow.util.WorkflowOperateUtil`  
**说明**：提供流程的基本操作功能，包括流程提交和流程干预等操作

**功能**：

- 提交流程：通过指定请求id、操作者和意见来提交流程
- 流程干预：支持流程干预操作，可将流程跳转到指定节点

#### WorkflowUtil - 流程工具类

**路径**：`com.customization.yll.common.util.WorkflowUtil`  
**说明**：提供丰富的流程字段获取、流程状态判断、节点信息查询等流程相关工具方法

**功能**：

- 获取流程主表字段值：支持通过requestInfo、请求id等方式获取主表字段值
- 更新流程主表字段：更新指定流程的主表字段值
- 获取明细数据：获取流程明细表的字段数据
- 获取下拉框字段选项显示名称：根据字段值获取下拉框、公共选择框的显示名称
- 流程状态判断：判断流程是否已归档、是否为公文流程等
- 流程信息获取：获取流程名称、创建人、归档时间等流程基本信息
- 流程版本管理：获取流程的所有版本、判断版本关系等
- 节点信息管理：获取流程节点列表、当前节点、创建节点等信息
- 节点操作者管理：获取当前节点或已流转节点的各种操作者信息

#### WorkflowFieldMapper - 接口参数与流程字段映射

**路径**：`com.customization.yll.common.manager.WorkflowFieldMapper`  
**说明**：用于将流程字段映射到接口参数，比如将流程字段 `field1` 的值存入接口参数 `param1`
中，支持字段名称转换、值转换、类型转换等功能，简化接口参数构建过程，适合需要将流程字段值传入接口参数的使用场景

**功能**：

- 主表字段映射：将流程主表字段映射为接口所需的参数格式
- 明细表字段映射：将流程明细表字段映射为接口所需的参数格式
- 字段值转换：支持自定义转换函数对字段值进行转换
- 字段类型转换：支持将字符串转换为Integer、Double等类型
- 字段校验：支持必填字段校验，当字段为空时抛出异常
- 跳过条件：支持根据字段值决定是否跳过该字段的映射

**使用示例：**

主表字段值映射到接口参数：

```java
WorkflowFieldMapper fieldMapper = new WorkflowFieldMapper();
JSONObject body = new JSONObject();
fieldMapper.

addMainFieldMapConfig("applyNo",new MapInfo("applyNo", true));
        fieldMapper.

addMainFieldMapConfig(isProject() ?"sqygbh":"sqrbh",
        new

MapInfo("applicantName",true));
        fieldMapper.

addMainFieldMapConfig("sqrdh",new MapInfo("applicantPhone", true));
        fieldMapper.

addMainFieldMapConfig("sqrq",new MapInfo("applicationDate", true));
        fieldMapper.

addMainFieldMapConfig("tzlxxlk",new MapInfo("investmentType")
                .

setConvertFunction(v ->

getSelectorShowName(v, "tzlxxlk",tableName, recordSet)));
        fieldMapper.

addMainFieldMapConfig("xmmcn",new MapInfo("projectName", true));

// mapMainField() 方法会生成 Map 类型的接口参数
        body.

putAll(fieldMapper.mapMainField(requestInfo.getMainTableInfo().

getProperty()));
```

### 建模

#### ModeUtil - 建模工具类

**路径**：`com.customization.yll.common.util.ModeUtil`  
**说明**：提供建模数据的增删改查、批量操作、权限重构等功能

**功能**：

- 建模数据插入：支持单条和批量插入建模数据，自动添加标准字段和权限重构
- 建模数据更新：更新指定建模数据的字段值
- 建模信息查询：根据表名获取建模id、根据建模id获取表名等
- 查询字段获取：获取建模查询页面的字段信息
- 批量操作：支持批量插入数据并获取插入的id列表
- 权限重构：自动执行建模数据的权限重构，确保数据权限正确

#### ModeActionUtil - 建模 Action 工具类

**路径**：`com.customization.yll.common.mode.util.ModeActionUtil`  
**说明**：提供建模 Action 的返回结果工具方法，简化建模 Action 的开发

**功能**：

- 生成错误返回结果：为建模 Action 生成标准化的错误返回信息
- 生成成功返回结果：为建模 Action 生成成功返回结果

#### ModeConfigUtil - 建模配置属性工具类

**路径**：`com.customization.yll.common.mode.util.ModeConfigUtil`    
**说明**：用于获取建模统一配置中心的配置属性值，支持缓存和必填校验。该功能可实现在建模中创建配置属性，  
替换传统中使用配置文件的属性配置方法，需要在建模中创建模块与表结构才可以使用。

**功能**：

- 获取配置属性值：根据配置id和属性名获取配置中心的值
- 必填校验：支持对配置属性进行必填校验，当必需的属性不存在时抛出异常
- 缓存支持：支持将配置属性值缓存到内存中，提高查询性能
- 缓存过期：支持自定义缓存过期时间，默认3分钟过期

### 文档

#### DocUtil - 文档工具类

**路径**：`com.customization.yll.common.util.DocUtil`  
**说明**：提供文档文件获取、流程文档生成、文件下载等文档相关功能

**功能**：

- 流程文档获取：获取流程存为文档功能生成的文件，支持按时间筛选
- 归档文档获取：获取流程归档时生成的文档文件
- 文档文件获取：获取文档的正文文件和附件文件，支持分类获取
- 文件下载：获取文档附件的下载地址
- 文件流获取：根据文件ID获取文件输入流
- 文件名获取：根据文件ID获取文件名
- WPS转换：使用WPS服务将文档转换为PDF格式

#### DocConvertorByWpsApi - 使用 WPS 中台接口对文档进行转换

**路径**：`com.customization.yll.common.doc.DocConvertorByWpsApi`  
**说明**：通过WPS文档中台API进行文档格式转换，支持多种格式转换

**功能**：

- 文档转换：调用WPS接口进行同步文档格式转换
- 多种格式：支持PDF、图片等多种格式转换
- 签名认证：使用HMAC-SHA256进行API请求签名
- 文件下载：自动下载转换后的文件并保存到指定路径

#### DocFileManager - 文档文件获取

**路径**：`com.customization.yll.common.doc.DocFileManager`  
**说明**：获取文档中的所有文件，包括正文和附件，支持压缩包自动解压

**功能**：

- 文件获取：获取文档中的所有文件，包括正文和附件
- 压缩包解压：自动检测并解压ZIP文件，获取内部文件
- 去重处理：根据文件ID去重，避免重复文件
- 文件信息：返回详细的文件信息，包括文件类型、路径等

#### DocInsertQrManager - 文档二维码工具

**路径**：`com.customization.yll.common.doc.DocInsertQrManager`  
**说明**：用于在Word文档中插入二维码，支持设置二维码位置和样式

**功能**：

- 二维码插入：在Word文档顶部插入二维码，支持左对齐、右对齐、居中对齐
- 自动去重：如果文档已存在二维码，会自动删除旧的二维码再插入新的
- 格式支持：仅支持docx格式的Word文档
- 二维码生成：自动生成二维码图片并插入到文档中

#### FileUploadService - 文件上传

**路径**：`com.customization.yll.common.doc.FileUploadService`  
**说明**：通过标准API接口上传文件到E9系统中，支持文档目录分类

**功能**：

- 文件上传：将本地文件上传到E9系统的指定文档目录
- 参数校验：对上传参数进行校验，确保文件存在和文件名有效
- Token认证：自动获取用户token进行接口认证
- 结果解析：解析上传接口返回结果，获取上传成功后的文件ID
- 批量上传：支持批量上传多个文件

#### WordTextEditService - 文档文字编辑

**路径**：`com.customization.yll.common.doc.WordTextEditService`  
**说明**：基于Apache POI的Word文档文本编辑工具，支持文本插入、替换、格式控制等

**功能**：

- 文本插入：根据关键词模式在指定位置插入文本
- 文本替换：替换文档中已存在的指定文本
- 模式匹配：支持正则表达式匹配，支持首次匹配或全部匹配模式
- 格式控制：支持文本对齐、间距等格式设置
- 表格处理：支持在表格单元格中进行文本操作
- 批量处理：支持处理多个文档

#### FileConvertUtil - 文件格式转换工具

**路径**：`com.customization.yll.common.doc.util.FileConvertUtil`  
**说明**：提供文件格式转换功能，支持使用WPS集成和WPS API进行文档转换

**功能**：

- WPS集成转换：使用E9集成的WPS服务将文档转换为PDF
- WPS API转换：通过WPS文档中台API进行更丰富的格式转换
- 文件保存：支持将转换后的文件保存到指定路径
- 多种格式：支持多种文档格式的转换

### 接口

#### ParamUtil - 接口参数工具类

**路径**：`com.customization.yll.common.util.ParamUtil`  
**说明**：提供接口参数获取功能，支持从HttpServletRequest中解析请求参数和JSON数据

**功能**：

- 请求参数转换：将HttpServletRequest转换为Map对象
- JSON参数解析：将请求体中的JSON数据解析为Map对象
- 对象转换：使用Jackson将JSON输入流转换为指定类型的对象
- JSON序列化：将对象转换为JSON格式

#### ApiParamValidator - 接口参数校验工具

**路径**：`com.customization.yll.common.web.util.ApiParamValidator`  
**说明**：基于@ApiParam注解的参数校验工具，支持嵌套对象递归校验

**功能**：

- 必填字段校验：校验使用@ApiParam注解且required=true的字段是否为空
- 嵌套对象校验：支持递归校验嵌套对象和集合类型中的对象
- 循环引用防护：防止对象循环引用导致的无限递归
- 集合类型支持：支持List、Map、数组等集合类型的元素校验
- 错误信息增强：提供详细的错误信息，包括字段路径

#### ParamValidationUtils - 参数校验工具

**路径**：`com.customization.yll.common.web.util.ParamValidationUtils`  
**说明**：基于Jakarta Validation的参数校验工具，支持注解式参数校验

**功能**：

- 参数校验：使用JSR-303注解对对象进行参数校验
- 异常抛出：校验失败时自动抛出相应的异常
- 错误信息收集：收集所有校验错误信息并返回
- 属性校验：支持对对象的特定属性进行校验

#### ApiParam - 接口参数注解

**路径**：`com.customization.yll.common.web.ApiParam`  
**说明**：用于标记接口参数的注解，提供参数描述和校验信息

**功能**：

- 参数描述：定义参数的显示名称和描述信息
- 必需校验：标记参数是否为必需参数
- 示例值：提供参数的示例值

#### ApiModel - 接口参数对象注解

**路径**：`com.customization.yll.common.web.ApiModel`  
**说明**：用于标记接口参数对象的注解，提供对象级别的描述信息

**功能**：

- 对象描述：为接口参数对象提供描述信息

#### ApiCallManager - 接口请求工具

**路径**：`com.customization.yll.common.web.util.ApiCallManager`  
**说明**：基于OkHttp的接口请求工具，支持GET、POST、文件上传等HTTP请求

**功能**：

- HTTP请求：支持GET、POST、PUT、DELETE等HTTP方法
- 文件上传：支持multipart/form-data文件上传
- 请求头处理：自动处理Content-Type，支持自定义请求头
- 超时配置：支持自定义连接、读取、写入超时时间
- 结果返回：直接返回Response对象，支持流式处理

### 系统

#### EcologyTokenManager - OA token 获取

**路径**：`com.customization.yll.common.manager.EcologyTokenManager`  
**说明**：用于获取E9系统的认证token，支持RSA加密和缓存机制

**功能**：

- Token获取：通过RSA加密方式获取系统认证token
- 缓存机制：token和密钥信息自动缓存，提高性能
- 请求头生成：自动生成包含token的HTTP请求头
- 用户ID加密：对用户ID进行RSA加密保护
- 密钥管理：自动获取和管理RSA公钥和私钥

#### CacheUtil - 缓存工具

**路径**：`com.customization.yll.common.util.CacheUtil`  
**说明**：提供缓存功能，支持Redis和本地缓存，自动选择最佳缓存方式

**功能**：

- 自动缓存选择：优先使用Redis缓存，没有Redis时自动使用本地缓存
- 缓存过期：支持设置缓存过期时间，过期自动清除
- 数据类型支持：支持所有可序列化对象和集合类型
- 缓存操作：提供存取删缓存的基本操作
- 本地缓存增强：在本地缓存基础上增加了过期时间功能

#### PropertiesUtil - 配置文件属性工具

**路径**：`com.customization.yll.common.util.PropertiesUtil`  
**说明**：提供配置文件属性读取功能，支持中文处理和缓存机制

**功能**：

- 配置文件读取：读取properties配置文件，支持中文编码
- 属性值获取：获取配置文件中的属性值，支持必填校验
- 缓存机制：支持属性值的内存缓存，提高读取性能
- 配置转换：将配置字符串转换为Map对象

#### DbUtil - 数据库工具类

**路径**：`com.customization.yll.common.util.DbUtil`  
**说明**：提供数据库操作的工具方法，支持增删改查和批量操作

**功能**：

- 数据插入：支持单条和批量数据插入
- 数据更新：支持单条和批量数据更新
- 数据删除：支持按条件删除数据
- 批量操作：支持大量数据的批量插入和更新
- 事务支持：提供事务安全的数据库操作接口
- SQL构建：自动构建安全的SQL语句

#### SqlUtil - sql工具类

**路径**：`com.customization.yll.common.util.SqlUtil`  
**说明**：提供SQL语句构建功能，支持动态生成查询、更新、插入语句

**功能**：

- 查询SQL构建：根据字段名和表名生成SELECT语句
- 更新SQL构建：生成UPDATE语句的SET部分和完整语句
- 插入SQL构建：生成INSERT语句，支持批量插入
- 条件SQL构建：生成WHERE条件语句
- 表别名支持：支持在查询中指定表别名

#### IntegrationLog - 集成日志

**路径**：`com.customization.yll.common.IntegrationLog`  
**说明**：基于标准集成日志的扩展，增加了占位符支持，就像 Log4j 一样，创建也更简单，使用 new 语句创建即可

**功能**：

- 占位符日志：支持使用{}占位符进行日志格式化
- 多级别日志：支持debug、info、warn、error级别日志
- JSON转换：提供对象转JSON字符串的方法
- 异常记录：支持记录异常信息

### 其它

#### HrmInfoUtil - 人力资源工具类

**路径**：`com.customization.yll.common.util.HrmInfoUtil`  
**说明**：提供人力资源信息查询功能，包括人员信息和部门信息

**功能**：

- 人员信息查询：获取工号、姓名、岗位等人员基本信息
- 姓名多语言：支持获取中文姓名，支持多语言文本解析
- 部门信息查询：获取部门名称、部门全路径、上级部门等
- 组织结构：支持构建部门层级路径

#### FormUtil - 表单工具类

**路径**：`com.customization.yll.common.util.FormUtil`  
**说明**：提供表单相关的工具方法，包括字段显示名称、表名获取等

**功能**：

- 字段名称获取：根据字段ID或数据库名获取字段的显示名称
- 表单表名获取：根据表单ID获取对应的数据库表名
- 表单ID获取：根据表名获取表单ID

#### FieldUtil - 字段工具类

**路径**：`com.customization.yll.common.util.FieldUtil`  
**说明**：提供表单字段相关的工具方法，包括字段类型判断、字段名称获取等

**功能**：

- 字段类型判断：根据字段ID获取字段的具体类型（选择框、浏览框等）
- 字段ID获取：根据字段名称获取字段ID
- 选择框选项：获取下拉框、选择框字段的选项显示名称
- 公共选择框：获取公共选择框的选项显示名称
- 字段显示名称：获取流程字段的显示名称

## 更多功能

可以查看我的源码，如果你有好用的功能，欢迎贡献 👏👏。

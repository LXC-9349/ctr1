### 流程使用方式
```text
在com.ctr.crm.commons.configuration.ApprovalConfig配置需要拦截的beanNames
在service中添加@Approval拦截方法 Demo：合同审批
```
#### 

### 短信渠道添加
```text
实现com.superaicloud.crm.commons.sms.channel.SuperChannel类
短信渠道配置里面添加对应渠道
短信用户配置里面添加对应账号密码
``` 

### job说明
```text
com.ctr.crm.api.VipService                0:10执行合同过期处理
com.ctr.crm.api.AllotJobService           2:30执行自动分配，和初始化员工库容
com.ctr.crm.api.RecyclingMemberJobService 3:00执行机会表自动回收
com.ctr.crm.api.SmsBackJobService         每2分钟轮循查短信上行或状态
```

### sql脚本说明
```text
1、sql脚本文件命名规则：
  V+版本号+双下划线+文件描述+.sql 如V20190606.16.14__InitTest.sql
2、注意事项
  1）sql脚本升级按版本号顺序执行，所以版本号顺序切记准确无误
  2）DDL最好与DML分开存放，因为DDL无法回滚
  3）已经上环境的脚本文件名不得随意删除，里面的SQL最好也不再修改
  4）脚本文件统一放置在classpath下的db/script目录
```

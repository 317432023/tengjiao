
0、参数获取与更新
    
    前台API 查询配置参数值的时候 从redis缓存优先加载参数、若获取不到再从数据库加载然后更新缓存 TODO
    
1、自动生成代码模板优化

    实体模板遇到 createTime和 updateTime 添加 TableField()  OK
    页面模板遇到 自定义 id 值 添加操作时要输出编辑内容，其他情况不输出 编辑内容 OK
    页面模板遇到 createTime和 updateTime 不输出编辑内容 OK
    页面模板遇到 非必填 输入域 不添加 required 或使用 required = false TODO

2、仪表盘参数统计接口开发测试数据 TODO

3、操作日志查看 TODO

4、站点参数配置 TODO

5、添加码值表管理 TODO

6、springSecurity 用户认证代码规约扫描 TODO

7、UI 界面编辑框可拖拉 OK
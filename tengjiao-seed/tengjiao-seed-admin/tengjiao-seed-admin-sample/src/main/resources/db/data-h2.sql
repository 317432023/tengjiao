
INSERT INTO sys_station VALUES ('1', '总站', 'http://root.xxx.com', '199828', null, null, '0', '2021-02-02 01:01:59', '2020-11-12 21:01:32');
INSERT INTO sys_station VALUES ('2', '测试站', 'http://test.xxx.com', null, null, null, '0', '2021-02-10 00:00:00', '2020-11-12 21:06:32');

INSERT INTO sys_role VALUES ('1', 'admin', '高级管理者', null, '0', '1', '2020-11-12 21:02:49', null);
INSERT INTO sys_role VALUES ('2', 'editor', '普通编辑者', null, '0', '1', '2020-11-12 21:08:03', null);
INSERT INTO sys_role VALUES ('3', 'admin', '高级管理者', '测试分站管理者', '0', '2', '2021-02-08 10:00:00', '2021-02-08 10:00:00');

INSERT INTO sys_admin VALUES ('1', 'admin', '$2a$10$ocNxnlC/UYPvfDYprV8nieulUPEQQZB4/d4lYMaSmKShaOyve7IA2', '$2a$10$7M0pmOmKxtA6VwSqS7qlfOpF7x6J0EObxWLqMG8V4IxxeHHWspXHO', null, '1', '2020-11-12 21:04:23', null, '0', '刘老根', null, null, null, null, '0');
INSERT INTO sys_admin VALUES ('2', 'editor', '$2a$10$ocNxnlC/UYPvfDYprV8nieulUPEQQZB4/d4lYMaSmKShaOyve7IA2', '$2a$10$7M0pmOmKxtA6VwSqS7qlfOpF7x6J0EObxWLqMG8V4IxxeHHWspXHO', null, '1', '2020-11-12 21:16:47', '2021-02-19 00:09:29', '0', '老铁', null, null, 'postman@qq.com', '1234567890X', '0');
INSERT INTO sys_admin VALUES ('3', 'admin2', '$2a$10$ocNxnlC/UYPvfDYprV8nieulUPEQQZB4/d4lYMaSmKShaOyve7IA2', '$2a$10$7M0pmOmKxtA6VwSqS7qlfOpF7x6J0EObxWLqMG8V4IxxeHHWspXHO', null, '2', '2020-11-12 21:17:26', '2021-03-17 12:14:16', '0', '王五', null, null, null, null, '0');
INSERT INTO sys_admin VALUES ('4', 'editor2', '$2a$10$ocNxnlC/UYPvfDYprV8nieulUPEQQZB4/d4lYMaSmKShaOyve7IA2', '$2a$10$7M0pmOmKxtA6VwSqS7qlfOpF7x6J0EObxWLqMG8V4IxxeHHWspXHO', null, '2', '2020-11-12 21:17:46', '2021-02-19 00:08:32', '0', '孙六', null, null, null, null, '1');
INSERT INTO sys_admin VALUES ('1343965747830988800', 'rise', '$2a$10$ocNxnlC/UYPvfDYprV8nieulUPEQQZB4/d4lYMaSmKShaOyve7IA2', '$2a$10$7M0pmOmKxtA6VwSqS7qlfOpF7x6J0EObxWLqMG8V4IxxeHHWspXHO', null, '1', null, '2020-12-31 00:35:08', '1', '赵七', null, null, NULL, '15659230272', '1');
INSERT INTO sys_admin VALUES ('1358448711579078656', 'zhangsan', '$2a$10$ocNxnlC/UYPvfDYprV8nieulUPEQQZB4/d4lYMaSmKShaOyve7IA2', '$2a$10$7M0pmOmKxtA6VwSqS7qlfOpF7x6J0EObxWLqMG8V4IxxeHHWspXHO', null, '2', '2021-02-08 00:13:06', '2021-02-08 01:40:11', '0', '张三', null, '1984-10-09', null, null, '1');

INSERT INTO sys_admin_role VALUES ('2', '2');
INSERT INTO sys_admin_role VALUES ('3', '3');
INSERT INTO sys_admin_role VALUES ('4', '3');
INSERT INTO sys_admin_role VALUES ('1343965747830988800', '1');
INSERT INTO sys_admin_role VALUES ('1343965747830988800', '2');
INSERT INTO sys_admin_role VALUES ('1358448711579078656', '3');

INSERT INTO `sys_menu` VALUES ('1', '0', 'Dashboard', '0', '/dashboard', '/dashboard', '0', '0', '0', '0', '首页', 'dashboard', 'dashboard:view', '1', '1', '1', null, '2021-03-21 15:24:01', null);
INSERT INTO `sys_menu` VALUES ('2', '0', 'System', '2', '/system', null, '1', '0', '0', '1', '系统管理', 'tree', null, '1', '0', '0', null, '2021-03-21 15:24:01', null);

INSERT INTO `sys_menu` VALUES ('3', '2', 'Menu', '0', '/system/menu', null, '0', '0', '0', '0', '菜单列表', 'tree-table', null, '1', '0', '0', null, '2021-03-21 15:25:04', null);
INSERT INTO `sys_menu` VALUES ('4', '3', null, '1', null, '/system/menu/query/**', '0', '0', '0', '1', '查看', null, 'system:menu:view', '1', '0', '0', null, '2021-03-21 15:25:06', null);
INSERT INTO `sys_menu` VALUES ('5', '3', null, '1', null, '/system/menu/add', '0', '0', '0', '2', '添加菜单', null, 'system:menu:add', '1', '0', '0', null, '2021-03-21 15:25:08', null);
INSERT INTO `sys_menu` VALUES ('6', '3', null, '1', null, '/system/menu/mod', '0', '0', '0', '3', '修改菜单', null, 'system:menu:mod', '1', '0', '0', null, '2021-03-21 15:25:11', null);
INSERT INTO `sys_menu` VALUES ('7', '3', null, '1', null, '/system/menu/del/**', '0', '0', '0', '4', '删除菜单', null, 'system:menu:del', '1', '0', '0', null, '2021-03-21 15:25:13', null);

INSERT INTO `sys_menu` VALUES ('8', '2', 'Config', '0', '/system/config', null, '0', '0', '0', '1', '系统配置', 'icon', null, '1', '0', '0', null, '2021-03-21 15:24:01', null);
INSERT INTO `sys_menu` VALUES ('9', '8', null, '1', null, '/system/config/query/**', '0', '0', '0', '1', '查看', null, 'system:config:view', '1', '0', '0', null, '2021-03-21 15:24:01', null);
INSERT INTO `sys_menu` VALUES ('10', '8', null, '1', null, '/system/config/add', '0', '0', '0', '2', '添加配置', null, 'system:config:add', '1', '0', '0', null, '2021-03-21 15:24:01', null);
INSERT INTO `sys_menu` VALUES ('11', '8', null, '1', null, '/system/config/mod', '0', '0', '0', '3', '修改配置', null, 'system:config:mod', '1', '0', '0', null, '2021-03-21 15:24:01', null);
INSERT INTO `sys_menu` VALUES ('12', '8', null, '1', null, '/system/config/del/**', '0', '0', '0', '4', '删除配置', 'el-icon-delete', 'system:config:del', '1', '0', '0', null, '2021-03-21 15:24:01', null);

INSERT INTO `sys_menu` VALUES ('13', '2', 'Station', '0', '/system/station', null, '0', '0', '0', '2', '站点管理', 'tab', null, '1', '0', '0', null, '2021-03-21 15:24:01', null);
INSERT INTO `sys_menu` VALUES ('14', '13', null, '1', null, '/system/station/query/**', '0', '0', '0', '1', '查看', null, 'system:station:view', '1', '0', '0', null, '2021-03-21 15:24:01', null);
INSERT INTO `sys_menu` VALUES ('15', '13', null, '1', null, '/system/station/add', '0', '0', '0', '2', '添加站点', null, 'system:station:add', '1', '0', '0', null, '2021-03-21 15:24:01', null);
INSERT INTO `sys_menu` VALUES ('16', '13', null, '1', null, '/system/station/mod', '0', '0', '0', '3', '修改站点', null, 'system:station:mod', '1', '0', '0', null, '2021-03-21 15:24:01', null);
INSERT INTO `sys_menu` VALUES ('17', '13', null, '1', null, '/system/station/del/**', '0', '0', '0', '4', '删除站点', null, 'system:station:del', '1', '0', '0', null, '2021-03-21 15:24:01', null);

INSERT INTO `sys_menu` VALUES ('18', '2', 'Role', '0', '/system/role', null, '0', '0', '0', '3', '角色管理', 'peoples', null, '1', '0', '0', null, '2021-03-21 15:24:01', null);
INSERT INTO `sys_menu` VALUES ('19', '18', null, '1', null, '/system/role/query/**', '0', '0', '0', '1', '查看', null, 'system:role:view', '1', '0', '0', null, '2021-03-21 15:24:01', null);
INSERT INTO `sys_menu` VALUES ('20', '18', null, '1', null, '/system/role/add', '0', '0', '0', '2', '添加角色', null, 'system:role:add', '1', '0', '0', null, '2021-03-21 15:24:01', null);
INSERT INTO `sys_menu` VALUES ('21', '18', null, '1', null, '/system/role/mod', '0', '0', '0', '3', '修改角色', null, 'system:role:mod', '1', '0', '0', null, '2021-03-21 15:24:01', null);
INSERT INTO `sys_menu` VALUES ('22', '18', null, '1', null, '/system/role/del/**', '0', '0', '0', '4', '删除角色', null, 'system:role:del', '1', '0', '0', null, '2021-03-21 15:24:01', null);
INSERT INTO `sys_menu` VALUES ('23', '18', null, '1', null, '/system/role/mod/menu', '0', '0', '0', '5', '角色授权', null, 'system:role:grant', '1', '0', '0', null, '2021-03-21 15:25:13', null);

INSERT INTO `sys_menu` VALUES ('24', '2', 'Admin', '0', '/system/admin', null, '0', '0', '0', '4', '管理员列表', 'people', null, '1', '0', '0', null, '2021-03-21 15:24:50', null);
INSERT INTO `sys_menu` VALUES ('25', '24', null, '1', null, '/system/admin/query/**', '0', '0', '0', '1', '查看', null, 'system:admin:view', '1', '0', '0', null, '2021-03-21 15:24:53', null);
INSERT INTO `sys_menu` VALUES ('26', '24', null, '1', null, '/system/admin/add', '0', '0', '0', '2', '添加管理员', null, 'system:admin:add', '1', '0', '0', null, '2021-03-21 15:24:56', null);
INSERT INTO `sys_menu` VALUES ('27', '24', null, '1', null, '/system/admin/mod', '0', '0', '0', '3', '修改管理员', null, 'system:admin:mod', '1', '0', '0', null, '2021-03-21 15:24:59', null);
INSERT INTO `sys_menu` VALUES ('28', '24', null, '1', null, '/system/admin/del/**', '0', '0', '0', '4', '删除管理员', null, 'system:admin:del', '1', '0', '0', null, '2021-03-21 15:25:01', null);

INSERT INTO `sys_menu` VALUES ('29', '2', 'Job', '0', '/system/job', null, '0', '0', '0', '5', '定时任务', 'lock', null, '1', '0', '0', null, '2021-03-21 15:25:04', null);
INSERT INTO `sys_menu` VALUES ('30', '29', null, '1', null, '/system/job/query/**', '0', '0', '0', '1', '查看', null, 'system:job:view', '1', '0', '0', null, '2021-03-21 15:25:06', null);
INSERT INTO `sys_menu` VALUES ('31', '29', null, '1', null, '/system/job/add', '0', '0', '0', '2', '添加定时任务', null, 'system:job:add', '1', '0', '0', null, '2021-03-21 15:25:08', null);
INSERT INTO `sys_menu` VALUES ('32', '29', null, '1', null, '/system/job/mod', '0', '0', '0', '3', '修改定时任务', null, 'system:job:mod', '1', '0', '0', null, '2021-03-21 15:25:11', null);
INSERT INTO `sys_menu` VALUES ('33', '29', null, '1', null, '/system/job/del/**', '0', '0', '0', '4', '删除定时任务', null, 'system:job:del', '1', '0', '0', null, '2021-03-21 15:25:13', null);

INSERT INTO `sys_config` VALUES ('1', '0', '开启外部API签名验证', 'open_api_sign', '1', '', null, null);
INSERT INTO `sys_config` VALUES ('2', '0', '总控制台默认分页大小', 'admin_default_page_size', '20', '', null, null);

INSERT INTO `sys_job` VALUES ('1', 'demoTask', 'taskWithParams', '这是参数111', '0/5 * * * * ?', '0', '定时任务示例_带参', '2021-06-27 16:23:22', null);

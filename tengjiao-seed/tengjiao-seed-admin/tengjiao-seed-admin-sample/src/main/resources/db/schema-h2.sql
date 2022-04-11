DROP TABLE IF EXISTS sys_admin;
CREATE TABLE sys_admin (
  id bigint(20) unsigned NOT NULL COMMENT '主键',
  username varchar(50) DEFAULT NULL COMMENT '用户名',
  password varchar(64) DEFAULT NULL COMMENT '密码',
  salt varchar(255) DEFAULT NULL COMMENT '盐',
  dept_id bigint(20) DEFAULT NULL COMMENT '部门ID',
  station_id int(11) DEFAULT NULL COMMENT '站点ID',
  create_time datetime DEFAULT NULL COMMENT '创建时间',
  update_time datetime DEFAULT NULL COMMENT '更新时间',
  gender tinyint(4) DEFAULT '0' COMMENT '性别（1-男，2-女，0-保密）',
  nickname varchar(50) DEFAULT NULL COMMENT '昵称',
  avatar varchar(255) DEFAULT NULL COMMENT '头像',
  birth date DEFAULT NULL COMMENT '生日',
  email varchar(60) DEFAULT NULL COMMENT '邮箱',
  phone varchar(20) DEFAULT NULL COMMENT '手机号码',
  disabled tinyint(3)  DEFAULT '0' COMMENT '是否禁用(1-禁用,0-可用)',
  PRIMARY KEY (id)
) COMMENT='系统管理员表';

DROP TABLE IF EXISTS sys_admin_role;
CREATE TABLE sys_admin_role (
  aid bigint(20) NOT NULL COMMENT '管理员id',
  rid int(11) NOT NULL COMMENT '角色id',
  PRIMARY KEY (aid,rid)
) COMMENT='管理员与角色关系表';

DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `type` int(11) DEFAULT '0' COMMENT '类别0:系统,9其他',
  `title` varchar(50) DEFAULT NULL COMMENT '参数名称（中文）',
  `name` varchar(50) NOT NULL DEFAULT '' COMMENT '配置编码（唯一）',
  `value` varchar(200) DEFAULT '' COMMENT '参数值',
  `remark` varchar(255) DEFAULT '' COMMENT '备注',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_config` (`name`)
) COMMENT='系统设置';

DROP TABLE IF EXISTS sys_log;
CREATE TABLE sys_log (
  id bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  ip varchar(20) DEFAULT NULL COMMENT 'ip地址',
  username varchar(50) DEFAULT NULL COMMENT '用户名',
  op_name varchar(64) DEFAULT NULL COMMENT '操作',
  spend_time int(10)  DEFAULT NULL COMMENT '花费时间(毫秒)',
  create_time datetime DEFAULT NULL COMMENT '创建时间',
  station_id int(11) DEFAULT NULL COMMENT '站点ID',
  PRIMARY KEY (id)
) COMMENT='系统日志';
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `pid` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '父菜单ID',
  `name` varchar(50) DEFAULT NULL COMMENT '路由名称',
  `type` tinyint(4) DEFAULT NULL COMMENT '类型(0-菜单, 1-按钮, 2-目录)',
  `path` varchar(60) DEFAULT NULL COMMENT 'URL兼路由路径',
  `pattern` varchar(60) DEFAULT NULL COMMENT '接口URL规则，实现的功能相当于perm字段（比如/menu/query/**，一般用 /模块名/操作/** 的形式定义，用于AntPathMatcher）',
  `open` tinyint(4) DEFAULT '0' COMMENT '是否展开（0-收起,1-展开）',
  `disabled` tinyint(4) DEFAULT '1' COMMENT '是否可用(1-可用,0-不可用)',
  `hidden` tinyint(4) DEFAULT '0' COMMENT '是否在sidebar菜单中展示',
  `sort_num` int(11) DEFAULT '0' COMMENT '排序号',
  `title` varchar(50) DEFAULT NULL COMMENT '在sidebar菜单项标题',
  `icon` varchar(30) DEFAULT NULL COMMENT '图标',
  `perm` varchar(50) DEFAULT NULL COMMENT '权限（一般是shiro或springsecurity资源格式，模块:操作 如user:add）',
  `breadcrumb` tinyint(4) DEFAULT '1' COMMENT '在面包屑导航中展示',
  `affix` tinyint(4) DEFAULT '0' COMMENT '固定到标签页',
  `no_cache` tinyint(4) DEFAULT '0' COMMENT '不缓存标签页',
  `active_menu` varchar(50) DEFAULT NULL COMMENT '打开页面时高亮的菜单项(路径)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) COMMENT='系统菜单表';
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
  id int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  code varchar(20) DEFAULT NULL COMMENT '角色代码( 站点+角色代码 唯一)',
  name varchar(50) DEFAULT NULL COMMENT '角色名',
  remark varchar(64) DEFAULT NULL COMMENT '角色备注',
  disabled tinyint(4) DEFAULT '0' COMMENT '是否禁用（0-启用,1-禁用）',
  station_id int(11) DEFAULT NULL COMMENT '站点ID',
  create_time datetime DEFAULT NULL COMMENT '创建时间',
  update_time datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (id)
) COMMENT='系统角色表';
DROP TABLE IF EXISTS sys_role_menu;
CREATE TABLE sys_role_menu (
  rid int(11) NOT NULL COMMENT '角色id',
  mid int(11) NOT NULL COMMENT '菜单id',
  PRIMARY KEY (rid,mid)
) COMMENT='角色与菜单关系表';
DROP TABLE IF EXISTS sys_station;
CREATE TABLE sys_station (
  id int(10) unsigned NOT NULL AUTO_INCREMENT,
  name varchar(50) DEFAULT NULL COMMENT '分站名称',
  domain varchar(50) DEFAULT NULL COMMENT '分站域名',
  kf_qq varchar(255) DEFAULT NULL COMMENT '客服QQ',
  region_id int(10)  DEFAULT NULL COMMENT '区域码',
  region_name varchar(40) DEFAULT NULL COMMENT '地名',
  disabled tinyint(3)  DEFAULT '0' COMMENT '是否禁用',
  expire_time datetime DEFAULT NULL COMMENT '过期时间',
  create_time datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (id)
) COMMENT='系统站点';
DROP TABLE IF EXISTS `sys_station_config`;
CREATE TABLE `sys_station_config` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `type` int(11) DEFAULT '0' COMMENT '类别0:系统,9其他',
  `station_id` int(11) NOT NULL COMMENT '站点id',
  `title` varchar(50) DEFAULT NULL COMMENT '参数名称（中文）',
  `name` varchar(50) NOT NULL DEFAULT '' COMMENT '配置编码（唯一）',
  `value` varchar(200) DEFAULT '' COMMENT '参数值',
  `remark` varchar(255) DEFAULT '' COMMENT '备注',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_station_config` (`station_id`,`name`)
) COMMENT='站点配置';
DROP TABLE IF EXISTS sys_job;
CREATE TABLE sys_job(
  id INT (11) unsigned NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  bean_name VARCHAR (100) NOT NULL COMMENT 'bean名称',
  method_name VARCHAR (50) NOT NULL COMMENT '方法名称',
  method_params VARCHAR (100) DEFAULT '' COMMENT '方法参数',
  cron_expression VARCHAR (100) NOT NULL COMMENT 'cron表达式',
  `status` INT (11) NOT NULL DEFAULT 0 COMMENT '状态（1正常0暂停）',
  remark VARCHAR (50) COMMENT '备注',
  create_time DATETIME NOT NULL COMMENT '创建时间',
  update_time DATETIME COMMENT '更新时间',
  PRIMARY KEY (id)
) COMMENT '定时任务';
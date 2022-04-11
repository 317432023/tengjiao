/*
Navicat MySQL Data Transfer

Source Server         : LOCAL
Source Server Version : 50644
Source Host           : 127.0.0.1:3306
Source Database       : tengjiao-part-sample

Target Server Type    : MYSQL
Target Server Version : 50644
File Encoding         : 65001

Date: 2021-05-22 03:14:44
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for sample_user
-- ----------------------------
DROP TABLE IF EXISTS `sample_user`;
CREATE TABLE `sample_user` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(30) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `nickname` varchar(30) NOT NULL COMMENT '用户名称',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `sex` int(1) DEFAULT NULL COMMENT '性别：1-男；0-女',
  `enabled` tinyint(3) unsigned DEFAULT NULL,
  `version` int(10) unsigned DEFAULT '0' COMMENT '版本号',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `USERNAME` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COMMENT='系统用户';

-- ----------------------------
-- Records of sample_user
-- ----------------------------
INSERT INTO `sample_user` VALUES ('4', 'Amanda1', 'Amanda', 'Amanda', '2020-03-05', '0', '1', '1', null, null);
INSERT INTO `sample_user` VALUES ('5', 'Jack', 'Jack', 'Jack', '2020-03-05', '1', '1', '0', null, null);
INSERT INTO `sample_user` VALUES ('6', 'Alex', 'Alex', 'Alex', '2020-03-06', '1', '1', '0', null, null);
INSERT INTO `sample_user` VALUES ('7', 'Reinhard', 'Reinhard', 'Reinhard', '2020-06-12', '1', '1', '0', null, null);
INSERT INTO `sample_user` VALUES ('8', 'David', 'David', 'David', '2020-06-12', '1', '1', '0', null, null);

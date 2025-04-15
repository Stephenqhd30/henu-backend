/*
 Navicat Premium Dump SQL

 Source Server         : StephenQiu
 Source Server Type    : MySQL
 Source Server Version : 80036 (8.0.36)
 Source Host           : localhost:3306
 Source Schema         : registration

 Target Server Type    : MySQL
 Target Server Version : 80036 (8.0.36)
 File Encoding         : 65001

 Date: 15/04/2025 12:22:47
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `admin_number` varchar(255) NOT NULL COMMENT '管理员编号',
  `admin_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '管理员姓名',
  `admin_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '管理员类型',
  `admin_password` varchar(1024) NOT NULL COMMENT '管理员密码',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '是否逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `admin_pk` (`admin_number`)
) ENGINE=InnoDB AUTO_INCREMENT=1906868834959167490 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='管理员';

-- ----------------------------
-- Table structure for cadre_type
-- ----------------------------
DROP TABLE IF EXISTS `cadre_type`;
CREATE TABLE `cadre_type` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `type` varchar(255) NOT NULL COMMENT '干部类型',
  `admin_id` bigint NOT NULL COMMENT '管理员id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '是否逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1906253812478316546 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='干部类型';

-- ----------------------------
-- Table structure for deadline
-- ----------------------------
DROP TABLE IF EXISTS `deadline`;
CREATE TABLE `deadline` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `deadline_time` datetime NOT NULL COMMENT '截止日期',
  `job_id` bigint NOT NULL COMMENT '招聘岗位id',
  `admin_id` bigint NOT NULL COMMENT '管理员id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '是否逻辑删除',
  PRIMARY KEY (`id`),
  KEY `deadline_job_id_index` (`job_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1906378654057787394 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='截止时间';

-- ----------------------------
-- Table structure for education
-- ----------------------------
DROP TABLE IF EXISTS `education`;
CREATE TABLE `education` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `school_id` bigint NOT NULL COMMENT '高校编号',
  `educational_stage` varchar(255) NOT NULL COMMENT '教育阶段',
  `major` varchar(255) NOT NULL COMMENT '专业',
  `study_time` varchar(512) NOT NULL COMMENT '学习起止年月',
  `certifier` varchar(255) NOT NULL COMMENT '证明人',
  `certifier_phone` varchar(255) NOT NULL COMMENT '证明人联系电话',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '是否逻辑删除',
  PRIMARY KEY (`id`),
  KEY `education_school_id_fk` (`school_id`),
  CONSTRAINT `education_school_id_fk` FOREIGN KEY (`school_id`) REFERENCES `school` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1907408053565710339 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='教育经历表';

-- ----------------------------
-- Table structure for family
-- ----------------------------
DROP TABLE IF EXISTS `family`;
CREATE TABLE `family` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `appellation` varchar(128) NOT NULL COMMENT '称谓',
  `family_name` varchar(128) NOT NULL COMMENT '姓名',
  `work_detail` varchar(256) NOT NULL COMMENT '工作单位及职务',
  `user_id` bigint NOT NULL COMMENT '创建用户id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '是否逻辑删除(0-否,1-是)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1903483701954641923 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='家庭关系表';

-- ----------------------------
-- Table structure for file_log
-- ----------------------------
DROP TABLE IF EXISTS `file_log`;
CREATE TABLE `file_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `file_type_id` bigint NOT NULL COMMENT '附件类型id',
  `file_name` varchar(512) NOT NULL COMMENT '附件名称',
  `file_path` varchar(512) NOT NULL COMMENT '附件存储路径',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1909212828070322179 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='文件上传日志表';

-- ----------------------------
-- Table structure for file_type
-- ----------------------------
DROP TABLE IF EXISTS `file_type`;
CREATE TABLE `file_type` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `type_name` varchar(256) NOT NULL COMMENT '文件上传类型名称',
  `type_values` varchar(512) NOT NULL COMMENT '文件上传类型值(JSON如[''jpg'',''png''])',
  `max_file_size` bigint NOT NULL DEFAULT '5242880' COMMENT '最大可上传文件大小（字节）',
  `admin_id` bigint NOT NULL COMMENT '创建人id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '是否逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `file_type_pk` (`type_name`)
) ENGINE=InnoDB AUTO_INCREMENT=1906184306464301058 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='文件上传类型表';

-- ----------------------------
-- Table structure for job
-- ----------------------------
DROP TABLE IF EXISTS `job`;
CREATE TABLE `job` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `job_name` varchar(255) NOT NULL COMMENT '岗位名称',
  `job_explanation` text NOT NULL COMMENT '岗位说明',
  `deadline_time` datetime DEFAULT NULL COMMENT '截止日期',
  `admin_id` bigint NOT NULL COMMENT '管理员id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '是否逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1906175207622221826 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='岗位信息表';

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `title` varchar(1024) NOT NULL COMMENT '通知主题',
  `content` longtext NOT NULL COMMENT '通知内容',
  `admin_id` bigint DEFAULT NULL COMMENT '创建人id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1911840050815995906 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='消息通知表';

-- ----------------------------
-- Table structure for message_notice
-- ----------------------------
DROP TABLE IF EXISTS `message_notice`;
CREATE TABLE `message_notice` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `registration_id` bigint NOT NULL COMMENT '报名登记表id',
  `content` longtext NOT NULL COMMENT '面试内容',
  `push_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '推送状态(0-未推送,1-成功,2-失败,3-重试中)',
  `user_id` bigint NOT NULL COMMENT '通知用户id',
  `admin_id` bigint DEFAULT NULL COMMENT '管理员id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否逻辑删除(0-否,1-是)',
  PRIMARY KEY (`id`),
  KEY `message_notice_registration_form_id_fk` (`registration_id`),
  KEY `message_notice_push_status_index` (`push_status`),
  CONSTRAINT `message_notice_registration_form_id_fk` FOREIGN KEY (`registration_id`) REFERENCES `registration_form` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1911381915185819650 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='消息通知表';

-- ----------------------------
-- Table structure for message_push
-- ----------------------------
DROP TABLE IF EXISTS `message_push`;
CREATE TABLE `message_push` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `message_notice_id` bigint NOT NULL COMMENT '消息通知id',
  `user_name` varchar(256) NOT NULL COMMENT '通知用户名',
  `push_type` varchar(128) NOT NULL COMMENT '推送方式(websocket/email/sms/other)',
  `push_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '推送状态(0-未推送,1-成功,2-失败,3-重试中)',
  `push_message` text COMMENT '推送消息内容',
  `retry_count` int NOT NULL DEFAULT '0' COMMENT '失败重试次数',
  `error_message` varchar(500) DEFAULT NULL COMMENT '失败原因',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `message_push_message_notice_id_index` (`message_notice_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1911840135595462658 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='消息推送表';

-- ----------------------------
-- Table structure for operation_log
-- ----------------------------
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `request_id` varchar(255) NOT NULL COMMENT '请求唯一id',
  `request_path` varchar(1024) NOT NULL COMMENT '请求路径',
  `request_method` varchar(32) NOT NULL COMMENT '请求方法（GET, POST等）',
  `request_ip` varchar(255) NOT NULL COMMENT '请求IP地址',
  `request_params` text NOT NULL COMMENT '请求参数',
  `response_time` bigint NOT NULL COMMENT '响应时间（毫秒）',
  `user_agent` varchar(512) NOT NULL COMMENT '用户代理（浏览器信息）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1911841042072637442 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='操作日志表';

-- ----------------------------
-- Table structure for registration_form
-- ----------------------------
DROP TABLE IF EXISTS `registration_form`;
CREATE TABLE `registration_form` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_id_card` varchar(1024) NOT NULL COMMENT '身份证号码',
  `user_name` varchar(255) NOT NULL COMMENT '姓名',
  `user_email` varchar(255) NOT NULL COMMENT '邮箱地址',
  `user_phone` varchar(255) NOT NULL COMMENT '联系电话',
  `user_gender` tinyint NOT NULL DEFAULT '0' COMMENT '性别(0-男,1-女)',
  `ethnic` varchar(255) NOT NULL DEFAULT '汉族' COMMENT '民族',
  `political_status` varchar(256) DEFAULT NULL COMMENT '政治面貌',
  `party_time` varchar(255) DEFAULT NULL COMMENT '入党时间',
  `birth_date` varchar(255) DEFAULT NULL COMMENT '出生日期',
  `marry_status` tinyint NOT NULL DEFAULT '0' COMMENT '婚姻状况(0-未婚，1-已婚)',
  `emergency_phone` varchar(255) DEFAULT NULL COMMENT '紧急联系电话',
  `address` varchar(512) DEFAULT NULL COMMENT '家庭住址',
  `user_avatar` varchar(1024) DEFAULT NULL COMMENT '证件照',
  `user_life_photo` varchar(1024) DEFAULT NULL COMMENT '生活照',
  `registration_form` varchar(1024) DEFAULT NULL COMMENT '报名登记表',
  `work_experience` longtext COMMENT '工作经历',
  `student_leaders` text COMMENT '主要学生干部经历',
  `leader_experience` text COMMENT '干部经历描述',
  `student_awards` longtext COMMENT '获奖情况',
  `review_status` tinyint NOT NULL DEFAULT '0' COMMENT '报名状态(0-待审核,1-审核通过,2-审核不通过)',
  `review_time` datetime DEFAULT NULL COMMENT '审核时间',
  `reviewer` varchar(255) DEFAULT NULL COMMENT '审核人姓名',
  `review_comments` text COMMENT '审核意见',
  `registration_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '报名状态(0-待报名,1-已报名,2-待发送面试通知,3-已发送面试通知)',
  `job_id` bigint NOT NULL COMMENT '岗位信息id',
  `user_id` bigint NOT NULL COMMENT '创建用户id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '是否逻辑删除(0-否,1-是)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1906978023081160707 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='报名登记表';

-- ----------------------------
-- Table structure for review_log
-- ----------------------------
DROP TABLE IF EXISTS `review_log`;
CREATE TABLE `review_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `registration_id` bigint NOT NULL COMMENT '报名登记表id',
  `reviewer` varchar(128) NOT NULL COMMENT '审核人',
  `review_status` tinyint NOT NULL COMMENT '审核状态(0-待审核,1-审核通过,2-审核不通过)',
  `review_comments` text COMMENT '审核意见',
  `review_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '审核时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '是否逻辑删除(0-否,1-是)',
  PRIMARY KEY (`id`),
  KEY `fk_review_log_registration` (`registration_id`),
  CONSTRAINT `fk_review_log_registration` FOREIGN KEY (`registration_id`) REFERENCES `registration_form` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1911819782793994242 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='审核记录表';

-- ----------------------------
-- Table structure for school
-- ----------------------------
DROP TABLE IF EXISTS `school`;
CREATE TABLE `school` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `school_name` varchar(255) NOT NULL COMMENT '高校名称',
  `admin_id` bigint NOT NULL COMMENT '管理员id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '是否逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `school_pk` (`school_name`)
) ENGINE=InnoDB AUTO_INCREMENT=1908189888041897989 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='高校信息';

-- ----------------------------
-- Table structure for school_school_type
-- ----------------------------
DROP TABLE IF EXISTS `school_school_type`;
CREATE TABLE `school_school_type` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `school_id` bigint NOT NULL COMMENT '高校id',
  `school_types` text NOT NULL COMMENT '高校类别列表(JSON存储)',
  `admin_id` bigint NOT NULL COMMENT '管理员id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '是否逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `school_id` (`school_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1908194422889828359 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='高校与高校类型关联表';

-- ----------------------------
-- Table structure for school_type
-- ----------------------------
DROP TABLE IF EXISTS `school_type`;
CREATE TABLE `school_type` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `type_name` varchar(128) NOT NULL COMMENT '高校类别名称',
  `admin_id` bigint NOT NULL COMMENT '管理员id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '是否逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `type_name` (`type_name`)
) ENGINE=InnoDB AUTO_INCREMENT=1908194137073176578 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='高校类型';

-- ----------------------------
-- Table structure for system_messages
-- ----------------------------
DROP TABLE IF EXISTS `system_messages`;
CREATE TABLE `system_messages` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `title` varchar(255) NOT NULL COMMENT '通知标题',
  `content` text NOT NULL COMMENT '消息内容',
  `push_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '推送状态(0-未推送,1-成功,2-失败,3-重试中)',
  `type` varchar(256) DEFAULT NULL COMMENT '消息发送类型',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否逻辑删除(0-否,1-是)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1904933344118956034 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='系统消息表';

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_account` varchar(256) NOT NULL COMMENT '用户账户',
  `user_password` varchar(1024) NOT NULL COMMENT '用户密码',
  `user_name` varchar(255) DEFAULT NULL COMMENT '姓名',
  `user_id_card` varchar(1024) DEFAULT NULL COMMENT '身份证号码',
  `user_email` varchar(255) DEFAULT NULL COMMENT '邮箱地址',
  `user_phone` varchar(255) DEFAULT NULL COMMENT '联系电话',
  `user_gender` tinyint DEFAULT '0' COMMENT '性别(0-男,1-女)',
  `user_avatar` varchar(1024) DEFAULT NULL COMMENT '用户头像',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint NOT NULL DEFAULT '0' COMMENT '是否逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1906869049615257602 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='用户信息表';

SET FOREIGN_KEY_CHECKS = 1;

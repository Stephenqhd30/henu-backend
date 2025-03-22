-- 管理员表
create table admin
(
    id             bigint auto_increment comment 'id'
        primary key,
    admin_number   varchar(255)                       not null comment '管理员编号',
    admin_name     varchar(255)                       not null comment '管理员姓名',
    admin_type     varchar(255)                       not null comment '管理员类型',
    admin_password varchar(1024)                      not null comment '管理员密码',
    create_time    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete      tinyint  default 0                 not null comment '是否逻辑删除'
)
    comment '管理员' row_format = DYNAMIC;

-- 干部类型表
create table cadre_type
(
    id          bigint auto_increment comment 'id'
        primary key,
    type        varchar(255)                       not null comment '干部类型',
    admin_id    bigint                             not null comment '管理员id',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete   tinyint  default 0                 not null comment '是否逻辑删除'
)
    comment '干部类型' row_format = DYNAMIC;


-- 截止时间
create table deadline
(
    id            bigint auto_increment comment 'id'
        primary key,
    deadline_time datetime                           not null comment '截止日期',
    create_time   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete     tinyint  default 0                 not null comment '是否逻辑删除'
)
    comment '截止时间' row_format = DYNAMIC;

-- 高校信息表
create table school
(
    id          bigint auto_increment comment 'id'
        primary key,
    school_name varchar(255)                       not null comment '高校名称',
    admin_id    bigint                             not null comment '管理员id',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete   tinyint  default 0                 not null comment '是否逻辑删除'
)
    comment '高校信息' row_format = DYNAMIC;

-- 高校类型表
create table school_type
(
    id          bigint auto_increment comment 'id'
        primary key,
    type_name   varchar(128)                       not null unique comment '高校类别名称',
    admin_id    bigint                             not null comment '管理员id',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete   tinyint  default 0                 not null comment '是否逻辑删除'
) comment '高校类型' row_format = DYNAMIC;

-- 高校与高校类型关联表（多对多关系）
create table school_school_type
(
    id              bigint auto_increment comment 'id'
        primary key,
    school_id       bigint                             not null comment '高校id',
    school_types text not null comment '高校类别列表(JSON存储)',
    admin_id        bigint                             not null comment '管理员id',
    create_time     datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete       tinyint  default 0                 not null comment '是否逻辑删除',
    constraint school_id
        unique (school_id),
    constraint fk_school
        foreign key (school_id) references school (id)
            on delete cascade
)
    comment '高校与高校类型关联表' row_format = DYNAMIC;

-- 岗位信息表
create table job
(
    id              bigint auto_increment comment 'id'
        primary key,
    job_name        varchar(255)                       not null comment '岗位名称',
    job_explanation text                               not null comment '岗位说明',
    admin_id        bigint                             not null comment '管理员id',
    create_time     datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete       tinyint  default 0                 not null comment '是否逻辑删除'
)
    comment '岗位信息表' row_format = DYNAMIC;

-- 用户信息表
create table user
(
    id                    bigint auto_increment comment 'id'
        primary key,
    user_id_card          varchar(1024)                          not null comment '身份证号码',
    user_name             varchar(255)                           not null comment '姓名',
    user_email            varchar(255)                           null comment '邮箱地址',
    user_phone            varchar(255)                           null comment '联系电话',
    user_gender           tinyint      default 0                 not null comment '性别(0-男,1-女)',
    user_avatar           varchar(1024)                          null comment '用户头像',
    create_time           datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time           datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete             tinyint      default 0                 not null comment '是否逻辑删除'
)
    comment '用户信息表' row_format = DYNAMIC;

-- 文件上传日志表
create table file_log
(
    id          bigint auto_increment comment 'id'
        primary key,
    file_type   varchar(512)                       not null comment '附件类型编号',
    file_name   varchar(512)                       not null comment '附件名称',
    file_path   varchar(512)                       not null comment '附件存储路径',
    user_id     bigint                             not null comment '用户id',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间'
)
    comment '文件上传日志表' row_format = DYNAMIC;

-- 教育经历表
create table education
(
    id                bigint auto_increment comment 'id'
        primary key,
    school_id         varchar(255)                       not null comment '高校编号',
    educational_stage varchar(255)                       not null comment '教育阶段',
    major             varchar(255)                       not null comment '专业',
    study_time        varchar(512)                       not null comment '学习起止年月',
    certifier         varchar(255)                       not null comment '证明人',
    certifier_phone   varchar(255)                       not null comment '证明人联系电话',
    user_id           bigint                             not null comment '用户id',
    create_time       datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time       datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete         tinyint  default 0                 not null comment '是否逻辑删除'
)
    comment '教育经历表' row_format = DYNAMIC;

-- 操作日志表
create table operation_log
(
    id             bigint auto_increment comment 'id' primary key,
    request_id     varchar(255)                       not null comment '请求唯一id',
    request_path   varchar(1024)                      not null comment '请求路径',
    request_method varchar(32)                        not null comment '请求方法（GET, POST等）',
    request_ip     varchar(255)                       not null comment '请求IP地址',
    request_params text                               not null comment '请求参数',
    response_time  bigint                             not null comment '响应时间（毫秒）',
    user_agent     varchar(512)                       not null comment '用户代理（浏览器信息）',
    create_time    datetime default CURRENT_TIMESTAMP not null comment '操作时间',
    is_delete      tinyint  default 0                 not null comment '是否逻辑删除'
)
    comment '操作日志表' row_format = DYNAMIC;

-- 文件上传类型表
create table file_type
(
    id            bigint auto_increment comment 'id'
        primary key,
    type_name     varchar(255)                       not null comment '文件上传类型名称',
    type_value    varchar(255)                       not null comment '文件上传类型值',
    max_file_size bigint   default 5242880           not null comment '最大可上传文件大小（字节）',
    admin_id      bigint                             not null comment '创建人id',
    create_time   datetime default CURRENT_TIMESTAMP not null comment '记录创建时间',
    update_time   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '记录更新时间',
    is_delete     tinyint  default 0                 not null comment '是否逻辑删除'
)
    comment '文件上传类型表' row_format = DYNAMIC;

-- 报名登记表
create table registration_form
(
    id                    bigint auto_increment comment 'id'
        primary key,
    user_id_card          varchar(1024)                          not null comment '身份证号码',
    user_name             varchar(255)                           not null comment '姓名',
    user_email            varchar(255)                           null comment '邮箱地址',
    user_phone            varchar(255)                           null comment '联系电话',
    user_gender           tinyint      default 0                 not null comment '性别(0-男,1-女)',
    user_avatar           varchar(1024)                          null comment '用户头像',
    ethnic                varchar(255) default '汉族'            not null comment '民族',
    party_time            varchar(255)                           null comment '入党时间',
    birth_date            varchar(255)                           null comment '出生日期',
    marry_status          tinyint      default 0                 not null comment '婚姻状况(0-未婚，1-已婚)',
    emergency_phone       varchar(255)                           null comment '紧急联系电话',
    address               varchar(512)                           null comment '家庭住址',
    work_experience       longtext                               null comment '工作经历',
    student_leader_awards longtext                               null comment '主要学生干部经历及获奖情况',
    review_status         tinyint      default 0                 not null comment '报名状态(0-待审核,1-审核通过,2-审核不通过)',
    review_time           datetime                               null comment '审核时间',
    reviewer              varchar(255)                           null comment '审核人姓名',
    review_comments       text                                   null comment '审核意见',
    job_id                bigint                                 not null comment '岗位信息id',
    user_id               bigint                                 not null comment '创建用户id',
    create_time           datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time           datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete             tinyint      default 0                 not null comment '是否逻辑删除(0-否,1-是)'
)
    comment '报名登记表' row_format = DYNAMIC;

-- 家庭关系表
create table family
(
    id          bigint auto_increment comment 'id'
        primary key,
    appellation varchar(255)                       not null comment '称谓',
    family_name varchar(255)                       not null comment '姓名',
    work_detail varchar(255)                       not null comment '工作单位及职务',
    user_id     bigint                             not null comment '创建用户id',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete   tinyint  default 0                 not null comment '是否逻辑删除(0-否,1-是)'
)
    comment '家庭关系表' row_format = DYNAMIC;

-- 审核记录表（硬删除）
create table review_log
(
    id              bigint auto_increment comment 'id'
        primary key,
    registration_id bigint                             not null comment '报名登记表id',
    reviewer        varchar(255)                       not null comment '审核人姓名',
    review_status   tinyint                            not null comment '审核状态(0-待审核,1-审核通过,2-审核不通过)',
    review_comments text                               null comment '审核意见',
    review_time     datetime default CURRENT_TIMESTAMP not null comment '审核时间',
    is_delete       tinyint  default 0                 not null comment '是否逻辑删除(0-否,1-是)',
    create_time     datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint fk_review_log_registration
        foreign key (registration_id) references registration_form (id)
)
    comment '审核记录表' row_format = DYNAMIC;
-- 消息通知表
create table message_notice
(
    id          bigint auto_increment comment 'id'
        primary key,
    register_id varchar(255)                       not null comment '报名编号',
    message_id  varchar(255)                       not null comment '系统消息编号',
    read_status varchar(255)                       not null comment '阅读状态',
    admin_id    bigint                             not null comment '管理员id',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete   tinyint  default 0                 not null comment '是否逻辑删除'
)
    row_format = DYNAMIC;


create table message_notice_record
(
    id          varchar(255) not null comment '消息通知记录编号'
        primary key,
    register_id varchar(255) not null comment '报名编号',
    way         varchar(255) not null comment '通知方式',
    account     varchar(255) not null comment '通知账号',
    notice_time varchar(255) not null comment '通知时间',
    status      varchar(255) not null comment '通知状态',
    create_time datetime     null comment '创建时间',
    update_time datetime     null comment '更新时间',
    is_delete int null comment '是否逻辑删除'
)
    row_format = DYNAMIC;
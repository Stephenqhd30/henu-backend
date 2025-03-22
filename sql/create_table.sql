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
    ethnic                varchar(255) default '汉族'            not null comment '民族',
    party_time            varchar(255)                           null comment '入党时间',
    birth_date            varchar(255)                           null comment '出生日期',
    marry_status          tinyint      default 0                 not null comment '婚姻状况(0-未婚，1-已婚)',
    emergency_phone       varchar(255)                           null comment '紧急联系电话',
    address               varchar(512)                           null comment '家庭住址',
    work_experience       longtext                               null comment '工作经历',
    student_leader_awards longtext                               null comment '主要学生干部经历及获奖情况',
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
    is_delete   int          null comment '是否逻辑删除',
    constraint fk_message_notice_record_to_registration
        foreign key (register_id) references registration (id)
)
    row_format = DYNAMIC;



create table family
(
    register_id varchar(255) not null comment '报名编号',
    id          bigint       not null comment 'id',
    appellation varchar(255) not null comment '称谓',
    family_name varchar(255) not null comment '姓名',
    work_detail varchar(255) not null comment '工作单位及职务',
    create_time datetime     null comment '创建时间',
    update_time datetime     null comment '更新时间',
    is_delete   int          null comment '是否逻辑删除',
    primary key (register_id, id)
)
    row_format = DYNAMIC;



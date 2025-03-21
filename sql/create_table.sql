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
    cadre_type  varchar(255)                       not null comment '干部类型',
    register_id bigint                             not null comment '报名编号',
    create_time datetime default (now())           null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete   tinyint  default 0                 not null comment '是否逻辑删除'
)
    comment '干部类型' row_format = DYNAMIC;

create index fk_cadre_type_to_registration
    on cadre_type (register_id);


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
    id             bigint auto_increment comment 'id'
        primary key,
    school_id      bigint                             not null comment '高校id',
    school_type_id bigint                             not null comment '高校类别id',
    admin_id       bigint                             not null comment '管理员id',
    create_time    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete      tinyint  default 0                 not null comment '是否逻辑删除',
    constraint fk_school foreign key (school_id) references school (id) on delete cascade,
    constraint fk_school_type foreign key (school_type_id) references school_type (id) on delete cascade,
    unique (school_id, school_type_id) -- 确保同一高校和类型的组合不会重复
) comment '高校与高校类型关联表' row_format = DYNAMIC;



create table education
(
    id                bigint auto_increment comment 'id'
        primary key,
    register_id       varchar(255)                       not null comment '报名编号',
    school_id         varchar(255)                       not null comment '高校编号',
    educational_stage varchar(255)                       not null comment '教育阶段',
    major             varchar(255)                       not null comment '专业',
    study_time        varchar(255)                       not null comment '学习起止年月',
    certifiers        varchar(255)                       not null comment '证明人',
    certifiers_phone  varchar(255)                       not null comment '证明人联系电话',
    create_time       datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time       datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete         tinyint  default 0                 not null comment '是否逻辑删除'
)
    row_format = DYNAMIC;


-- auto-generated definition
-- 管理员表
create table admin
(
    id             bigint auto_increment comment 'id'
        primary key,
    admin_number   varchar(255)                       not null comment '管理员编号',
    admin_account  varchar(255)                       not null comment '管理员账号',
    admin_name     varchar(255)                       not null comment '管理员姓名',
    admin_type     varchar(255)                       not null comment '管理员类型',
    admin_password varchar(1024)                      not null comment '管理员密码',
    create_time    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete      tinyint  default 0                 not null comment '是否逻辑删除'
)
    row_format = DYNAMIC;


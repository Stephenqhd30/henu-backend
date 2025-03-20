package com.henu.registration.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 管理员表
 * @author stephenqiu
 * @TableName admin
 */
@TableName(value ="admin")
@Data
public class Admin implements Serializable {
    private static final long serialVersionUID = 2729914775863449388L;
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 管理员编号
     */
    private String adminNumber;

    /**
     * 管理员姓名
     */
    private String adminName;

    /**
     * 管理员类型
     */
    private String adminType;

    /**
     * 管理员密码
     */
    private String adminPassword;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否逻辑删除
     */
    @TableLogic
    private Integer isDelete;
}
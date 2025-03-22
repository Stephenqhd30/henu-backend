package com.henu.registration.model.vo;

import cn.hutool.json.JSONUtil;
import com.henu.registration.model.entity.Family;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import cn.hutool.core.collection.CollUtil;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 家庭关系视图
 *
 * @author stephen
 */
@Data
public class FamilyVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 标签列表
     */
    private List<String> tagList;

    /**
     * 创建用户信息
     */
    private UserVO userVO;

    /**
     * 封装类转对象
     *
     * @param familyVO familyVO
     * @return {@link Family}
     */
    public static Family voToObj(FamilyVO familyVO) {
        if (familyVO == null) {
            return null;
        }
        Family family = new Family();
        BeanUtils.copyProperties(familyVO, family);
        List<String> tagList = familyVO.getTagList();
        if (CollUtil.isNotEmpty(tagList)) {
            family.setTags(JSONUtil.toJsonStr(tagList));
        }
        return family;
    }

    /**
     * 对象转封装类
     *
     * @param family family
     * @return {@link FamilyVO}
     */
    public static FamilyVO objToVo(Family family) {
        if (family == null) {
            return null;
        }
        FamilyVO familyVO = new FamilyVO();
        BeanUtils.copyProperties(family, familyVO);
        if (StringUtils.isNotBlank(family.getTags())) {
             familyVO.setTagList(JSONUtil.toList(family.getTags(), String.class));
        }
        return familyVO;
    }
}

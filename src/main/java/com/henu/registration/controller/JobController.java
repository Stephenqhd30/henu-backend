package com.henu.registration.controller;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.henu.registration.common.*;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.model.dto.job.JobAddRequest;
import com.henu.registration.model.dto.job.JobQueryRequest;
import com.henu.registration.model.dto.job.JobUpdateRequest;
import com.henu.registration.model.entity.Admin;
import com.henu.registration.model.entity.Deadline;
import com.henu.registration.model.entity.Job;
import com.henu.registration.model.vo.job.JobVO;
import com.henu.registration.service.AdminService;
import com.henu.registration.service.DeadlineService;
import com.henu.registration.service.JobService;
import com.henu.registration.utils.caffeine.LocalCacheUtils;
import com.henu.registration.utils.redisson.cache.CacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * 岗位信息表接口
 *
 * @author stephen qiu
 */
@RestController
@RequestMapping("/job")
@Slf4j
public class JobController {

    @Resource
    private JobService jobService;

    @Resource
    private AdminService adminService;
    
    @Resource
    private DeadlineService deadlineService;

    // region 增删改查

    /**
     * 创建岗位信息表
     *
     * @param jobAddRequest jobAddRequest
     * @param request request
     * @return {@link BaseResponse<Long>}
     */
    @PostMapping("/add")
    public BaseResponse<Long> addJob(@RequestBody JobAddRequest jobAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(jobAddRequest == null, ErrorCode.PARAMS_ERROR);
        // todo 在此处将实体类和 DTO 进行转换
        Job job = new Job();
        BeanUtils.copyProperties(jobAddRequest, job);
        // 数据校验
        jobService.validJob(job, true);

        // todo 填充默认值
        Admin loginAdmin = adminService.getLoginAdmin(request);
        job.setAdminId(loginAdmin.getId());
        // 写入数据库
        boolean result = jobService.save(job);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newJobId = job.getId();
        return ResultUtils.success(newJobId);
    }

    /**
     * 删除岗位信息表
     *
     * @param deleteRequest deleteRequest
     * @param request request
     * @return {@link BaseResponse<Boolean>}
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteJob(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Admin admin = adminService.getLoginAdmin(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Job oldJob = jobService.getById(id);
        ThrowUtils.throwIf(oldJob == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldJob.getAdminId().equals(admin.getId()) && !adminService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = jobService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 同步删除截止时间信息
        boolean remove = deadlineService.remove(Wrappers.lambdaQuery(Deadline.class)
                .eq(Deadline::getJobId, id));
        ThrowUtils.throwIf(!remove, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新岗位信息表
     *
     * @param jobUpdateRequest jobUpdateRequest
     * @return {@link BaseResponse<Boolean>}
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateJob(@RequestBody JobUpdateRequest jobUpdateRequest) {
        if (jobUpdateRequest == null || jobUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        Job job = new Job();
        BeanUtils.copyProperties(jobUpdateRequest, job);
        // 数据校验
        jobService.validJob(job, false);
        // 判断是否存在
        long id = jobUpdateRequest.getId();
        Job oldJob = jobService.getById(id);
        ThrowUtils.throwIf(oldJob == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = jobService.updateById(job);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取岗位信息表（封装类）
     *
     * @param id id
     * @return {@link BaseResponse<JobVO>}
     */
    @GetMapping("/get/vo")
    public BaseResponse<JobVO> getJobVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Job job = jobService.getById(id);
        ThrowUtils.throwIf(job == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(jobService.getJobVO(job, request));
    }

    /**
     * 分页获取岗位信息表列表（仅系统管理员可用）
     *
     * @param jobQueryRequest jobQueryRequest
     * @return {@link BaseResponse<Page<Job>>}
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<Job>> listJobByPage(@RequestBody JobQueryRequest jobQueryRequest) {
        long current = jobQueryRequest.getCurrent();
        long size = jobQueryRequest.getPageSize();
        // 查询数据库
        Page<Job> jobPage = jobService.page(new Page<>(current, size),
                jobService.getQueryWrapper(jobQueryRequest));
        return ResultUtils.success(jobPage);
    }

    /**
     * 分页获取岗位信息表列表（封装类）
     *
     * @param jobQueryRequest jobQueryRequest
     * @param request request
     * @return {@link BaseResponse<Page<JobVO>>}
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<JobVO>> listJobVOByPage(@RequestBody JobQueryRequest jobQueryRequest,
                                                               HttpServletRequest request) {
        long current = jobQueryRequest.getCurrent();
        long size = jobQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 使用多级缓存优化查询
        // 构建缓存 key（基于查询条件的 MD5 哈希值）
        String queryCondition = JSONUtil.toJsonStr(jobQueryRequest);
        String hashKey = DigestUtils.md5DigestAsHex(queryCondition.getBytes());
        String cacheKey = "listJobVOByPage:" + hashKey;
        // 1. 尝试从本地缓存中获取数据
        String cachedValue = (String) LocalCacheUtils.get(cacheKey);
        if (ObjUtil.isNotEmpty(cachedValue)) {
            // 如果缓存命中，直接返回缓存中的分页结果
            Page<JobVO> cachedPage = JSONUtil.toBean(cachedValue, Page.class);
            return ResultUtils.success(cachedPage);
        }
        // 2. 如果本地缓存未命中，尝试从 Redis 缓存中获取数据
        cachedValue = CacheUtils.get(cacheKey);
        if (ObjUtil.isNotEmpty(cachedValue)) {
            // 如果 Redis 缓存命中，将其存入本地缓存并返回
            LocalCacheUtils.put(cacheKey, cachedValue);
            Page<JobVO> cachedPage = JSONUtil.toBean(cachedValue, Page.class);
            return ResultUtils.success(cachedPage);
        }
        // 3. 如果缓存都未命中，查询数据库
        Page<Job> jobPage = jobService.page(new Page<>(current, size),
                jobService.getQueryWrapper(jobQueryRequest));
        // 4. 将数据库查询结果转换为 VO 页面对象
        Page<JobVO> jobVOPage = jobService.getJobVOPage(jobPage, request);
        String cacheValue = JSONUtil.toJsonStr(jobVOPage);
        // 5. 更新本地缓存和 Redis 缓存
        try {
            // 更新本地缓存
            LocalCacheUtils.put(cacheKey, cacheValue);
            // 更新 Redis 缓存, 并设置随机过期时间为 2~5 分钟
            CacheUtils.put(cacheKey, cacheValue, TimeUnit.MINUTES.toMinutes(RandomUtil.randomLong(2, 5)));
        } catch (Exception e) {
            // 如果 Redis 缓存更新失败，记录日志以便排查问题
            log.error("更新缓存失败, cacheKey: {}", cacheKey, e);
        }
        // 获取封装类
        return ResultUtils.success(jobVOPage);
    }

    /**
     * 分页获取当前登录用户创建的岗位信息表列表
     *
     * @param jobQueryRequest jobQueryRequest
     * @param request request
     * @return {@link BaseResponse<Page<JobVO>>}
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<JobVO>> listMyJobVOByPage(@RequestBody JobQueryRequest jobQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(jobQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        Admin loginAdmin = adminService.getLoginAdmin(request);
        jobQueryRequest.setAdminId(loginAdmin.getId());
        long current = jobQueryRequest.getCurrent();
        long size = jobQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<Job> jobPage = jobService.page(new Page<>(current, size),
                jobService.getQueryWrapper(jobQueryRequest));
        // 获取封装类
        return ResultUtils.success(jobService.getJobVOPage(jobPage, request));
    }

    // endregion
}
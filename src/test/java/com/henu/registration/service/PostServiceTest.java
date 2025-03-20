package com.henu.registration.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.henu.registration.elasticsearch.service.PostEsService;
import com.henu.registration.model.dto.post.PostQueryRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * 帖子服务测试
 *
 * @author stephenqiu
 * 
 */
@SpringBootTest
class PostServiceTest {

    @Resource
    private PostEsService postEsService;

    @Test
    void searchFromEs() {
        PostQueryRequest postQueryRequest = new PostQueryRequest();
        postQueryRequest.setUserId(1L);
        Page<Post> postPage = postEsService.searchPostFromEs(postQueryRequest);
        Assertions.assertNotNull(postPage);
    }

}
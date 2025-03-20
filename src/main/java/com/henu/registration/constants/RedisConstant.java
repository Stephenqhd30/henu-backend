package com.henu.registration.constants;

/**
 * @author: stephenqiu
 * @create: 2024-09-23 13:49
 **/
public interface RedisConstant {
	
	/**
	 * Redis key 文件上传路径前缀
	 */
	String FILE_NAME = "henu:registration:";
	
	/**
	 * Redis key 标签树
	 */
	String TAG_TREE_KEY = "tag:tree";
	
	/**
     * Redis key 匹配用户
     */
	String MATCH_USER = "match:user";
}

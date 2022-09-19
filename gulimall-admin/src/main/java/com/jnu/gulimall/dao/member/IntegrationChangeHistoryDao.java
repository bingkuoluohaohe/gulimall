package com.jnu.gulimall.dao.member;

import com.jnu.gulimall.entity.member.IntegrationChangeHistoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 积分变化历史记录
 * 
 * @author ych
 * @email 2625856353@qq.com
 * @date 2022-05-04 20:10:58
 */
@Mapper
public interface IntegrationChangeHistoryDao extends BaseMapper<IntegrationChangeHistoryEntity> {
	
}

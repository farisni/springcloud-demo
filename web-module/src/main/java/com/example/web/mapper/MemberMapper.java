package com.example.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.web.entity.Member;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper extends BaseMapper<Member> {
}

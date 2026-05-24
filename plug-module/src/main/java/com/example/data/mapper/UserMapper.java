package com.example.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.data.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}

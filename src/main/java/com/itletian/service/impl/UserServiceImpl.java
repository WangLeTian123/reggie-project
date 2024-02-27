package com.itletian.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itletian.entity.User;
import com.itletian.mapper.UserMapper;
import com.itletian.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}

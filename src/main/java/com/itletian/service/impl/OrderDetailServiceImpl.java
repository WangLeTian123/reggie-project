package com.itletian.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itletian.entity.OrderDetail;
import com.itletian.mapper.OrderDetailMapper;
import com.itletian.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
